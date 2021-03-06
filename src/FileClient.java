/* FileClient provides all the client functionality regarding the file server */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileClient extends Client implements FileClientInterface {

	public boolean delete(String filename, UserToken token) {
		String remotePath;
		if (filename.charAt(0) == '/') {
			remotePath = filename.substring(1);
		} else {
			remotePath = filename;
		}

		Envelope message = null, response = null;
		message = new Envelope("DELETEF"); // Success
		message.addObject(remotePath);
		message.addObject(token);
		try {
			output.writeObject(message);
			response = (Envelope) input.readObject();

			if (response.getMessage().compareTo("OK") == 0) {
				System.out.printf("File %s deleted successfully\n", filename);
			} else {
				System.out.printf("Error deleting file %s (%s)\n", filename, response.getMessage());
				return false;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return true;
	}

	public boolean download(String sourceFile, String destFile, UserToken token) {
		if (sourceFile.charAt(0) == '/') {
			sourceFile = sourceFile.substring(1);
		}

		File file = new File(destFile);
		try {
			Envelope message = null, response = null;

			if (!file.exists()) {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);

				message = new Envelope("DOWNLOADF"); // Success
				message.addObject(sourceFile);
				message.addObject(token);
				output.writeObject(message);

				response = (Envelope) input.readObject();

				while (response.getMessage().compareTo("CHUNK") == 0) {
					fos.write((byte[]) response.getObjContents().get(0), 0, (Integer) response.getObjContents().get(1));
					System.out.printf(".");
					message = new Envelope("DOWNLOADF"); // Success
					output.writeObject(message);
					response = (Envelope) input.readObject();
				}
				fos.close();

				if (response.getMessage().compareTo("EOF") == 0) {
					fos.close();
					System.out.printf("\nTransfer successful file %s\n", sourceFile);
					message = new Envelope("OK"); // Success
					output.writeObject(message);
				} else {
					System.out.printf("Error reading file %s (%s)\n", sourceFile, response.getMessage());
					file.delete();
					return false;
				}
			}

			else {
				System.out.printf("Error couldn't create file %s\n", destFile);
				return false;
			}
		} catch (IOException e1) {
			System.out.printf("Error couldn't create file %s\n", destFile);
			return false;
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<String> listFiles(UserToken token) {
		try {
			Envelope message = null, response = null;
			// Tell the server to return the member list
			message = new Envelope("LFILES");
			message.addObject(token); // Add requester's token
			output.writeObject(message);

			response = (Envelope) input.readObject();

			// If server indicates success, return the member list
			if (response.getMessage().equals("OK")) {
				return (List<String>) response.getObjContents().get(0);
			}
			return null;
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	public boolean upload(String sourceFile, String destFile, String group, UserToken token) {

		if (destFile.charAt(0) != '/') {
			destFile = "/" + destFile;
		}

		try {
			Envelope message = null, response = null;
			message = new Envelope("UPLOADF");
			message.addObject(destFile);
			message.addObject(group);
			message.addObject(token); // Add requester's token
			output.writeObject(message);

			FileInputStream fis = new FileInputStream(sourceFile);

			response = (Envelope) input.readObject();

			if (response.getMessage().equals("READY")) {
				System.out.printf("Meta data upload successful\n");
			} else {
				System.out.printf("Upload failed: %s\n", response.getMessage());
				fis.close();
				return false;
			}

			do {
				byte[] buf = new byte[4096];
				if (response.getMessage().compareTo("READY") != 0) {
					System.out.printf("Server error: %s\n", response.getMessage());
					fis.close();
					return false;
				}
				message = new Envelope("CHUNK");
				int n = fis.read(buf); // can throw an IOException
				if (n > 0) {
					System.out.printf(".");
				} else if (n < 0) {
					System.out.println("Read error");
					fis.close();
					return false;
				}

				message.addObject(buf);
				message.addObject(new Integer(n));

				output.writeObject(message);
				response = (Envelope) input.readObject();

			} while (fis.available() > 0);
			fis.close();

			if (response.getMessage().compareTo("READY") == 0) {
				message = new Envelope("EOF");
				output.writeObject(message);

				response = (Envelope) input.readObject();
				if (response.getMessage().compareTo("OK") == 0) {
					System.out.printf("\nFile data upload successful\n");
				} else {
					System.out.printf("\nUpload failed: %s\n", response.getMessage());
					return false;
				}
			} else {
				System.out.printf("Upload failed: %s\n", response.getMessage());
				return false;
			}
		} catch (Exception e1) {
			System.err.println("Error: " + e1.getMessage());
			e1.printStackTrace(System.err);
			return false;
		}
		return true;
	}
}
