import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class RunClient extends JFrame {
	private static final long serialVersionUID = -7140891447099041238L;

	private String username;
	GroupClient gclient;
	FileClient fclient;
	private UserToken userToken;

	// Constructor. Instantiates the GroupClient and tries to connect.
	public RunClient() {
		username = askForValidInput("Enter your username", this);
		if (username == null)
			System.exit(0);

		gclient = new GroupClient();
		if (!gclient.connect("localhost", GroupServer.GROUP_SERVER_PORT)) {
			JOptionPane.showMessageDialog(this, "Failed to connect to Group Server!");
			System.exit(0);
		}

		System.out.println("Successfully connected");
		userToken = gclient.getToken(username.toUpperCase());
		if (userToken == null) {
			JOptionPane.showMessageDialog(this, "User does not exist! Only administrators can create users.");
			System.exit(0);
		}
		initGUI();
	}

	// Main
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				RunClient rc = new RunClient();
				rc.setVisible(true);
			}
		});
	}

	// Adds all the components to the main frame.
	private void initGUI() {
		JTabbedPane tabbedPanel = new JTabbedPane();

		JComponent groupPanel = new GroupPanel(gclient, userToken);
		tabbedPanel.addTab("Groups", groupPanel);

		JComponent fserverPanel = new FileServerPanel(gclient, userToken);
		tabbedPanel.addTab("File Servers", fserverPanel);

		add(tabbedPanel);

		setTitle("CryptoShare");
		setSize(800, 600);
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				gclient.disconnect();
				// fclient.disconnect();
				System.exit(0);
			}
		});
	}

	protected static String askForValidInput(String question, Component c) {
		String input;
		Pattern p = Pattern.compile("[^\\w]");
		input = JOptionPane.showInputDialog(question);
		if (input != null) {
			if (input.length() > GroupServer.MAX_USERNAME_LENGTH)
				input = input.substring(0, GroupServer.MAX_USERNAME_LENGTH);
			Matcher m = p.matcher(input);
			if (!m.find())
				return input.toUpperCase();
			else
				JOptionPane.showMessageDialog(c, "Invalid characters");
		}
		return null;
	}
}
