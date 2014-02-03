
public class RunClient {

	public static void main(String[] args) {
		GroupClient gclient = new GroupClient();
		
		if(gclient.connect("localhost", GroupServer.SERVER_PORT))
			System.out.println("\nClient connected");
		
		// Username needs to be an ADMIN account or an account already created by an admin
		UserToken userToken = gclient.getToken("tdoshea90");
		System.out.println(userToken.toString());
	}
}
