package Server;

import java.io.IOException;
import java.util.ArrayList;

import Game.Board;
import Game.Profile;

public class ObjectInThread extends Thread {
	private Client client;
	
	public ObjectInThread(Client client) {
		this.client = client;
	}
	
	public void run() {
		while(true) {
			Object objectIn = null;
			while(objectIn == null) {
				System.out.println("recieving");
				objectIn = getObject();
			}
			System.out.println(objectIn);
			
			if(objectIn instanceof Board) {
				Board boardIn = (Board) objectIn;
				boardIn.printBoard();
				client.updateBoard(boardIn);
			} else if(objectIn instanceof ArrayList){
				ArrayList<Board> boardListIn = (ArrayList<Board>) objectIn;
				client.updateBoardList(boardListIn);
			} else if(objectIn instanceof Profile) {
				Profile profileIn = (Profile) objectIn;
				client.updateProfile(profileIn);
			} else {
				System.out.println(objectIn + "not recognised");
			}
			System.out.println("recieved");			
		}
	}
	
	Object getObject() {
		try {
			return client.getObjectInStream().readObject();
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
