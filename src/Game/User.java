package Game;

public class User {
	private String username;
	private int gameID;
	
	public User(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public int getGameID() {
		return gameID;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}	

}
