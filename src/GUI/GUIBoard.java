package GUI;

import java.util.ArrayList;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUIBoard extends Application {
	public static final int SCENE_WIDTH = 800;
	public static final int SCENE_HEIGHT = 800;
	private final int BOARD_XOFFSET = 100;
	private final int BOARD_YOFFSET = 100;
	private final int TILE_SIZE = 50;
	private Group root;
	private Client client;
	private ArrayList<Circle> counters;
	private String selectedCounterLocation;
	
	public GUIBoard(Client client) {
		this.client = client;
		this.counters = new ArrayList<>();
		root = new Group();
	}

	@Override
	public void start(Stage stage) throws Exception {		
		Text titleText = new Text("Checkers");
		titleText.setX(BOARD_XOFFSET+TILE_SIZE*4);
		titleText.setY(BOARD_YOFFSET/2);
		root.getChildren().add(titleText);
		
		addBoardSquares();
		addCounters();
		createCounterRefresher();
		
		client.createInThread();
		
		Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
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
					counter.setFill(Color.DARKRED);
					root.getChildren().add(counter);
					counters.add(counter);
				} else if(Math.abs(boardState[j][i]) == 2) {
					counter.setFill(Color.DARKBLUE);
					root.getChildren().add(counter);
					counters.add(counter);					
				} if(boardState[j][i] < 0) {
					Circle counterInner = new Circle(centreX, centreY, TILE_SIZE*0.2);
					counter.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
						selectedCounterLocation = counter.getId();
					});
					
					counterInner.setFill(Color.DARKGREEN);
					counters.add(counterInner);
					root.getChildren().add(counterInner);
				}
			}
		}
	}
	
	private void createCounterRefresher() {
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
	    	redrawCounters();
	    }));
	    timeline.setCycleCount(Animation.INDEFINITE);
	    timeline.play();
	}
	
	public void redrawCounters() {
		root.getChildren().removeAll(counters);
		addCounters();
	}
	
	public Client getClient() {
		return client;
	}
	
	public void main(String[] args) {
		launch();
	}
	
	

}
