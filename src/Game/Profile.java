package Game;

import java.io.Serializable;
import java.time.LocalDate;

import Server.Command;

public class Profile implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String username;
	private String firstName;
	private String lastName;
	private final String password;
	private LocalDate dateOfBirth;
	private String emailAddress;
	private Command command;
	
	public Profile(String username, String password) {
		this.username = username;
		this.password = password;
		this.command = Command.NULL;
	}
	
	public Profile(String username, String firstName, String lastName,
					String password, LocalDate dateOfBirth, String emailAddress) {
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

	public LocalDate getDateOfBirth() {
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
	
	public boolean equalsTo(Profile otherProfile) {
		return username.equals(otherProfile.getUsername()) &&
				firstName.equals(otherProfile.getFirstName()) &&
				lastName.equals(otherProfile.getLastName()) &&
				password.equals(otherProfile.getPassword()) &&
				dateOfBirth.equals(otherProfile.getDateOfBirth()) &&
				emailAddress.equals(otherProfile.getEmailAddress());
	}
	
}
