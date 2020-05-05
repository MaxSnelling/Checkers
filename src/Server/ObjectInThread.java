package Server;

import java.io.IOException;
import java.util.ArrayList;

import Game.Board;
import Game.Profile;

public class ObjectInThread extends Thread {
	private final Client client;
	private boolean running;
	
	public ObjectInThread(Client client) {
		this.client = client;
		running = true;
	}
	
	public void run() {
		while(running) {
			Object objectIn = waitForObjectIn();
			System.out.println("recieved");	
			
			if(objectIn instanceof Board) {
				boardInProtocol(objectIn);
			} else if(objectIn instanceof ArrayList){
				boardListInProtocol(objectIn);
			} else if(objectIn instanceof Profile) {
				profileInProtocol(objectIn);
			} else {
				System.out.println(objectIn + "not recognised");
			}					
		}
		System.out.println("Object in thread closed");
	}
	
	private Object waitForObjectIn() {
		System.out.println("recieving");
		Object objectIn = null;
		while(objectIn == null) {			
			objectIn = getObject();
		}
		return objectIn;
	}
	
	private Object getObject() {
		try {
			return client.getObjectInStream().readObject();
		} catch (ClassNotFoundException | IOException e) {			
			closeThread();
			return null;
		}
				
	}
	
	private void boardInProtocol(Object objectIn) {
		Board boardIn = (Board) objectIn;
		client.updateBoard(boardIn);
		
		if(boardIn.getCommand().equals(Command.GAME_END)) {
			client.endGame();
		}
	}
	
	private void boardListInProtocol(Object objectIn) {
		ArrayList<Board> boardListIn = (ArrayList<Board>) objectIn;
		if(recentGamesListCheck(boardListIn)) {
			client.setRecentGames(boardListIn);
		} else {
			client.updateBoardList(boardListIn);
		}
	}
	
	private void profileInProtocol(Object objectIn) {
		Profile profileIn = (Profile) objectIn;
		client.updateProfile(profileIn);
	}
	
	private boolean recentGamesListCheck(ArrayList<Board> boardList) {
		return boardList.size()>0 &&
				boardList.get(0).getCommand() != null  &&
				boardList.get(0).getCommand().equals(Command.RECENT_GAMES);
	}
	
	public void closeThread() {
		System.out.println("Client connection closed");
		running = false;
		System.exit(0);
	}
}
