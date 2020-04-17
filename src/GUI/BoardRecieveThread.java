package GUI;

import java.io.IOException;
import Game.Board;
import Server.Client;

public class BoardRecieveThread extends Thread {
	private Client client;
	
	public BoardRecieveThread(Client client) {
		this.client = client;
	}
	
	public void run() {
		while(true) {
			Board inputBoard = null;
			while(inputBoard == null) {
				System.out.println("recieving...");
				inputBoard = recieveBoard();
			}
			System.out.println("recieved");
			client.updateBoard(inputBoard);
		}
	}
	
	Board recieveBoard() {
		try {
			return (Board) client.getIn().readObject();
		} catch (ClassNotFoundException | IOException e) {
			threadWait();
		}
		return null;
	}
	
	void threadWait() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
}