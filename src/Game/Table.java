package Game;

import java.io.Serializable;
import java.util.ArrayList;

import Server.ClientThread;

public class Table implements Serializable {

	private static final long serialVersionUID = 2L;
	private ArrayList<ClientThread> players;
	private Board game;
	private int gameID;
	
	public Table(Board game) {
		this.game = game;
		this.gameID = game.getGameID();
	}

	public ArrayList<ClientThread> getPlayers() {
		return players;
	}

	public void addPlayer(ClientThread player) {
		System.out.println(player.getName() + "yeet");
		this.players.add(player);
		game.addPlayer(player.getName());
	}

	public Board getGame() {
		return game;
	}

	public void updateGame(Board game) {
		this.game = game;
		updatePlayersGame();
	}
	
	public int getGameID() {
		return this.gameID;
	}
	
	public void updatePlayersGame() {
		for(ClientThread player:players) {
			player.updateGame(game);
		}
	}
	
	

}
