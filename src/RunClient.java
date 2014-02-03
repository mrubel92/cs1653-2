
public class RunClient {

	public static void main(String[] args) {
		
		// SETUP
		GroupClient gclient = new GroupClient();
		if(gclient.connect("localhost", GroupServer.GROUP_SERVER_PORT))
			System.out.println("Client connected to GroupServer");
		
		// Username needs to be an ADMIN account or an account already created by an admin
		UserToken userToken = gclient.getToken("tdoshea90");
		System.out.println(userToken.toString());
		
		System.out.println(gclient.createUser("derp", userToken));
		
		// Test FileServer
		FileClient fclient = new FileClient();
		if(fclient.connect("localhost", FileServer.FILE_SERVER_PORT))
			System.out.println("Client connected to FileServer");
		
		fclient.upload("testFile.txt", "testFile.txt", "ADMIN", userToken);
		
		
		gclient.disconnect();
		fclient.disconnect();
		
		// NON ADMIN ACCOUNT EXAMPLE
		GroupClient gclient2 = new GroupClient();
		if(gclient2.connect("localhost", GroupServer.GROUP_SERVER_PORT))
			System.out.println("Client connected to GroupServer");
		
		UserToken userToken2 = gclient2.getToken("derp");
		System.out.println(userToken2.toString());
		
		gclient2.disconnect();
	}
}
