package GUI;

import java.util.ArrayList;
import Game.Board;
import Server.Client;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Board GUI page shown when in game
 * @author Max Snelling
 * @version 5/5/20
 */
public class GUIBoard extends Application {
	private final int BOARD_XOFFSET = 100;
	private final int BOARD_YOFFSET = 150;
	private final int TILE_SIZE = 50;
	private final int INFORMATION_XOFFSET = BOARD_XOFFSET + 8*TILE_SIZE + 100;
	private final Color PLAYER1_COLOR = Color.DARKRED;
	private final Color PLAYER2_COLOR = Color.DARKBLUE;
	private final Group root;
	private final Client client;
	private Stage stage;
	private final ArrayList<Circle> counters;
	private final ArrayList<Text> gameInformation;
	private Timeline counterRefresher;
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
		this.stage = stage;
		Text titleText = new Text("Checkers");
		titleText.setX(GUIMain.SCENE_WIDTH/2 - 50);
		titleText.setY(BOARD_YOFFSET*0.4);
		titleText.setFont(GUIMain.headingFont);
		
		Button backButton = new Button("Back");
		backButton.setLayoutX(BOARD_XOFFSET);
		backButton.setLayoutY(BOARD_YOFFSET + TILE_SIZE*9);
		backButton.setOnAction(eventHandlerBack);
		
		Text player1InfoText = new Text("Player 1: \nRemaining Counters: ");
		Text player2InfoText = new Text("Player 2: \nRemaining Counters: ");
		player1InfoText.setFont(GUIMain.boldFont);
		player2InfoText.setFont(GUIMain.boldFont);		
		player1InfoText.setX(INFORMATION_XOFFSET);
		player1InfoText.setY(BOARD_YOFFSET + 100);
		player2InfoText.setX(INFORMATION_XOFFSET);
		player2InfoText.setY(BOARD_YOFFSET + 200);
		
		Rectangle informationBox = new Rectangle(INFORMATION_XOFFSET-10, BOARD_YOFFSET, 250, 8*TILE_SIZE);
		informationBox.setStrokeWidth(3);
		informationBox.setStroke(Color.MAROON);
		informationBox.setFill(Color.BLANCHEDALMOND);		
		
		root.getChildren().add(informationBox);
		root.getChildren().add(titleText);
		root.getChildren().add(backButton);
		root.getChildren().add(player1InfoText);
		root.getChildren().add(player2InfoText);
		
		addBoardSquares();
		addCounters();
		createCounterRefresher();
		addGameInformationText();
		addPlayerText();
		addTurnInformationText();
		
		Scene scene = new Scene(root, GUIMain.SCENE_WIDTH, GUIMain.SCENE_HEIGHT);
		scene.setFill(GUIMain.BACKGROUND_COLOUR);
		GUIMain.scenes.add(scene);
		stage.setScene(scene);
	}

	private void addBoardSquares() {		
		Rectangle border = new Rectangle(BOARD_XOFFSET, BOARD_YOFFSET, 8*TILE_SIZE, 8*TILE_SIZE);
		border.setStrokeWidth(10);
		border.setStroke(Color.MAROON);
		border.setFill(Color.TRANSPARENT);
		root.getChildren().add(border);		
		
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
					if(selectedCounterLocation != null && client.isPlaying()) {
						int counterXLocation = selectedCounterLocation.charAt(0) - 48;
						int counterYLocation = selectedCounterLocation.charAt(2) - 48;
						int squareXLocation = square.getId().charAt(0) - 48;
						int squareYLocation = square.getId().charAt(2) - 48;		
						client.moveCounter(counterXLocation, counterYLocation, squareXLocation, squareYLocation);
						selectedCounterLocation = null;
						redrawCounters();
						}
				});
				root.getChildren().add(square);
			}
		}	
	}
	
	private void addCounters() {		
		int[][] boardState = client.getCurrentGame().getBoardState();
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
		if(!client.isPlaying()) {
			counterRefresher.stop();
			System.out.println("Game ended");
		}
	}
	
	private void addGameInformationText() {
		Board currentGame = client.getCurrentGame();
		
		Text username1Text = new Text(currentGame.getPlayer1());
		Text countersRemaining1Text = new Text(currentGame.getPlayer1TileCount() + "");
		
		username1Text.setX(INFORMATION_XOFFSET + 90);
		username1Text.setY(BOARD_YOFFSET + 100);
		username1Text.setFill(PLAYER1_COLOR);
		username1Text.setFont(GUIMain.standardFont);
		countersRemaining1Text.setX(INFORMATION_XOFFSET + 200);
		countersRemaining1Text.setY(BOARD_YOFFSET + 120);
		countersRemaining1Text.setFill(PLAYER1_COLOR);
		countersRemaining1Text.setFont(GUIMain.standardFont);
		
		Text username2Text = new Text(currentGame.getPlayer2());
		Text countersRemaining2Text = new Text(currentGame.getPlayer2TileCount() + "");
		
		username2Text.setX(INFORMATION_XOFFSET + 90);
		username2Text.setY(BOARD_YOFFSET + 200);
		username2Text.setFill(PLAYER2_COLOR);
		username2Text.setFont(GUIMain.standardFont);
		countersRemaining2Text.setX(INFORMATION_XOFFSET + 200);
		countersRemaining2Text.setY(BOARD_YOFFSET + 220);
		countersRemaining2Text.setFill(PLAYER2_COLOR);
		countersRemaining2Text.setFont(GUIMain.standardFont);
		
		gameInformation.add(username1Text);
		gameInformation.add(username2Text);
		gameInformation.add(countersRemaining1Text);
		gameInformation.add(countersRemaining2Text);
		root.getChildren().add(username1Text);
		root.getChildren().add(username2Text);
		root.getChildren().add(countersRemaining1Text);
		root.getChildren().add(countersRemaining2Text);
	}
	
	private void addPlayerText() {
		int playerNumber = client.getPlayerNumber();	
		
		Text playerText = new Text();
		playerText.setX(INFORMATION_XOFFSET);
		playerText.setY(BOARD_YOFFSET + 20);
		playerText.setTextAlignment(TextAlignment.CENTER);	
		playerText.setFont(GUIMain.standardFont);
		
		if(playerNumber == 1) {
			playerText.setText("You are player 1");
			playerText.setFill(PLAYER1_COLOR);		
		} else {
			playerText.setText("You are player 2");
			playerText.setFill(PLAYER2_COLOR);	
		}
		
		root.getChildren().add(playerText);
	}
	
	private void addTurnInformationText() {
		int playersTurn = client.getCurrentGame().getPlayersTurn();
		int playerNumber = client.getPlayerNumber();
		
		turnText = new Text();
		turnText.setX(INFORMATION_XOFFSET);
		turnText.setY(BOARD_YOFFSET + TILE_SIZE*8 - 10);
		turnText.setTextAlignment(TextAlignment.CENTER);
		turnText.setFont(GUIMain.standardFont);
		
		if(client.getCurrentGame().playing()) {
			if(playersTurn == playerNumber) {
				turnText.setText("It's your turn");
			} else {
				turnText.setText("Waiting for opponent");
			}
		} else {
			turnText.setFont(GUIMain.boldFont);
			
			String winner = client.getCurrentGame().getWinner();		
			if(winner.equals(client.getProfile().getUsername())) {
				turnText.setText("Congratualtions! You Won!");	
			} else {
				turnText.setText("Unfortunately, you have lost.");
			}				
		}
		
		root.getChildren().add(turnText);		
	}
	
	private void createCounterRefresher() {
		counterRefresher = new Timeline(new KeyFrame(Duration.millis(750), ev -> {
	    	redrawCounters();
	    	redrawGameInformation();
	    }));
		counterRefresher.setCycleCount(Animation.INDEFINITE);
		counterRefresher.play();
	}
	
	private void redrawCounters() {
		root.getChildren().removeAll(counters);
		addCounters();
	}
	
	private void redrawGameInformation() {
		root.getChildren().removeAll(gameInformation);
		addGameInformationText();
		
		root.getChildren().remove(turnText);
		addTurnInformationText();
	}
	
	private final EventHandler<ActionEvent> eventHandlerBack = e -> {
		GUIMain.showPreviousScene(stage);		
	};
	
	public void main(String[] args) {
		launch();
	}
}
