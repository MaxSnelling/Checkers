package Game;

import java.io.Serializable;
import java.sql.Date;

import Server.Command;

public class Profile implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String firstName;
	private String lastName;
	private String password;
	private Date dateOfBirth;
	private String emailAddress;
	private Command command;
	
	public Profile(String username, String password) {
		this.username = username;
		this.password = password;
		this.command = Command.NULL;
	}
	
	public Profile(String username, String firstName, String lastName,
					String password, Date dateOfBirth, String emailAddress) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.dateOfBirth = dateOfBirth;
		this.emailAddress = emailAddress;
		this.command = Command.NULL;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPassword() {
		return password;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}

	public Command getCommand() {
		return command;
	}
	
	public void setCommand(Command command) {
		this.command = command;
	}
	
}
