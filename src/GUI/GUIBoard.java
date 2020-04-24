package GUI;

import java.util.ArrayList;

import Game.Board;
import Server.Client;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUIBoard extends Application {
	private final int BOARD_XOFFSET = 100;
	private final int BOARD_YOFFSET = 100;
	private final int TILE_SIZE = 50;
	private final int INFORMATION_XOFFSET = BOARD_XOFFSET + 8*TILE_SIZE + 50;
	private final Color PLAYER1_COLOR = Color.DARKRED;
	private final Color PLAYER2_COLOR = Color.DARKBLUE;
	private Group root;
	private Client client;
	private ArrayList<Circle> counters;
	private ArrayList<Text> gameInformation;
	private String selectedCounterLocation;
	private Text turnText;
	
	public GUIBoard(Client client) {
		this.client = client;
		this.counters = new ArrayList<>();
		this.gameInformation = new ArrayList<>();
		root = new Group();
	}

	@Override
	public void start(Stage stage) throws Exception {		
		Text titleText = new Text("Checkers");
		titleText.setX(BOARD_XOFFSET+TILE_SIZE*4);
		titleText.setY(BOARD_YOFFSET/2);
		titleText.setFont(Font.font(16));
		root.getChildren().add(titleText);
		
		addBoardSquares();
		addCounters();
		createCounterRefresher();
		addGameInformationText();
		addPlayerText();
		addTurnText();
		
		Scene scene = new Scene(root, GUIMain.SCENE_WIDTH, GUIMain.SCENE_HEIGHT);
		scene.setFill(Color.BEIGE);
		stage.setScene(scene);
		stage.show();
	}

	void addBoardSquares() {		
		for(int i=0; i<8 ; i++) {
			for(int j=0; j<8; j++) {
				int startX = BOARD_XOFFSET + TILE_SIZE*i;
				int startY = BOARD_YOFFSET + TILE_SIZE*j;
				Rectangle square = new Rectangle(startX,startY, TILE_SIZE,TILE_SIZE);
				if((i+j) % 2 == 0)
					square.setFill(Color.SADDLEBROWN);
				else 
					square.setFill(Color.BISQUE);
				
				square.setId(i + "," + j);
				
				square.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
					if(selectedCounterLocation != null) {
						int counterXLocation = selectedCounterLocation.charAt(0) - 48;
						int coutnerYLocation = selectedCounterLocation.charAt(2) - 48;
						int squareXLocation = square.getId().charAt(0) - 48;
						int squareYLocation = square.getId().charAt(2) - 48;		
						client.moveCounter(counterXLocation, coutnerYLocation, squareXLocation, squareYLocation);
						selectedCounterLocation = null;
						redrawCounters();
						}
				});
				root.getChildren().add(square);
			}
		}		
	}
	
	void addCounters() {
		int[][] boardState = client.getCurrentGame().getTiles();
		for(int i=0; i<8 ; i++) {
			for(int j=0; j<8; j++) {
				int centreX = BOARD_XOFFSET + TILE_SIZE*i + TILE_SIZE/2;
				int centreY = BOARD_YOFFSET + TILE_SIZE*j + TILE_SIZE/2;
				Circle counter = new Circle(centreX, centreY, TILE_SIZE*0.4);
				
				counter.setId(i + "," + j);
				
				counter.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
					selectedCounterLocation = counter.getId();
				});
				
				if(Math.abs(boardState[j][i]) == 1) {
					counter.setFill(PLAYER1_COLOR);
					root.getChildren().add(counter);
					counters.add(counter);
				} else if(Math.abs(boardState[j][i]) == 2) {
					counter.setFill(PLAYER2_COLOR);
					root.getChildren().add(counter);
					counters.add(counter);					
				} if(boardState[j][i] < 0) {
					Circle counterInner = new Circle(centreX, centreY, TILE_SIZE*0.2);
					counterInner.setId(i + "," + j);
					counterInner.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
						selectedCounterLocation = counterInner.getId();
					});
					
					counterInner.setFill(Color.DARKGREEN);
					counters.add(counterInner);
					root.getChildren().add(counterInner);
				}
			}
		}
	}
	
	private void addGameInformationText() {
		Board currentGame = client.getCurrentGame();
		
		Text player1InfoText = new Text("Player 1: " + currentGame.getPlayer1() +
				"\nRemaining Counters: " + currentGame.getPlayer1TileCount());
		player1InfoText.setX(INFORMATION_XOFFSET);
		player1InfoText.setY(BOARD_YOFFSET);
		player1InfoText.setFill(PLAYER1_COLOR);
		
		Text player2InfoText = new Text("Player 2: " + client.getCurrentGame().getPlayer2() +
				"\nRemaining Counters: " + currentGame.getPlayer2TileCount());
		player2InfoText.setX(INFORMATION_XOFFSET);
		player2InfoText.setY(BOARD_YOFFSET + 100);
		player2InfoText.setFill(PLAYER2_COLOR);
		
		gameInformation.add(player1InfoText);
		gameInformation.add(player2InfoText);
		root.getChildren().add(player1InfoText);
		root.getChildren().add(player2InfoText);
	}
	
	private void addPlayerText() {
		int playerNumber = client.getPlayerNumber();	
		
		Text playerText = new Text();
		playerText.setX(GUIMain.SCENE_WIDTH/2);
		playerText.setY(50);
		playerText.setTextAlignment(TextAlignment.CENTER);	
		
		if(playerNumber == 1) {
			playerText.setText("You are player 1");
			playerText.setFill(PLAYER1_COLOR);		
		} else {
			playerText.setText("You are player 2");
			playerText.setFill(PLAYER2_COLOR);	
		}
		
		root.getChildren().add(playerText);
	}
	
	private void addTurnText() {
		int playersTurn = client.getCurrentGame().getPlayersTurn();
		int playerNumber = client.getPlayerNumber();
		
		turnText = new Text();
		turnText.setX(GUIMain.SCENE_WIDTH/2);
		turnText.setY(BOARD_YOFFSET + TILE_SIZE*8 + 50);
		turnText.setTextAlignment(TextAlignment.CENTER);
		
		
		if(playersTurn == playerNumber) {
			turnText.setText("It is your turn");
		} else {
			turnText.setText("Waiting for opponents move...");
		}
		
		root.getChildren().add(turnText);		
	}
	
	private void createCounterRefresher() {
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(750), ev -> {
	    	redrawCounters();
	    	redrawGameInformation();
	    }));
	    timeline.setCycleCount(Animation.INDEFINITE);
	    timeline.play();
	}
	
	private void redrawCounters() {
		root.getChildren().removeAll(counters);
		addCounters();
	}
	
	private void redrawGameInformation() {
		root.getChildren().removeAll(gameInformation);
		addGameInformationText();
		
		root.getChildren().remove(turnText);
		addTurnText();
	}
	
	public void main(String[] args) {
		launch();
	}
	
	

}
