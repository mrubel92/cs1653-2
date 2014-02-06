import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
		username = JOptionPane.showInputDialog("Enter your username");
		if(username == null)
			System.exit(0);
		
		gclient = new GroupClient();
		if(!gclient.connect("localhost", GroupServer.GROUP_SERVER_PORT)) {
			JOptionPane.showMessageDialog(this, "Failed to connect to Group Server!");
			System.exit(0);
		}
		
		System.out.println("Successfully connected");
		userToken = gclient.getToken(username);
		if(userToken == null) {
			JOptionPane.showMessageDialog(this, "User does not exist! Only administrators can create users.");
			System.exit(0);
		}
		System.out.println(userToken.toString());
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
		
		if(!userToken.getGroups().contains("ADMIN")) {
			tabbedPanel.addTab("Admin Console", null);
			tabbedPanel.setEnabledAt(2, false);
		} else {
			JComponent adminPanel = new AdminPanel(gclient, userToken);
			tabbedPanel.addTab("Admin Console", adminPanel);
		}
		
		add(tabbedPanel);
		
        setTitle("CryptoShare");
        setSize(800, 600);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent event) {
        		gclient.disconnect();
//        		fclient.disconnect();
        		System.exit(0);
        	}
        });
	}
}
