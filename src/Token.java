import java.util.ArrayList;
import java.util.List;

public class Token implements UserToken {

	String issuer;
	String subject;
	ArrayList<String> usersGroups;
	
	public Token(String name, String username, ArrayList<String> userGroups) {
		issuer = name;
		subject = username;
		usersGroups = new ArrayList<String>(userGroups.size());
		for (String group : userGroups) {
			usersGroups.add(group);
		}
	}

	@Override
	public String getIssuer() {
		return issuer;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public List<String> getGroups() {
		return usersGroups;
	}

}
