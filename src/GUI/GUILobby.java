package GUI;

import java.util.ArrayList;

import Game.Board;
import Server.Client;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUILobby extends Application {
	public static final int SCENE_WIDTH = 800;
	public static final int SCENE_HEIGHT = 400;
	private Client client;
	private ArrayList<Board> currentBoardList;
	private TableView<Board> gamesTable;
	private Timeline lobbyRefresher;


	public GUILobby(Client client) {
		this.client = client;
		currentBoardList = new ArrayList<>();
	}

	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		ObservableList<Node> rootChildren = root.getChildren();

		Text lobbyTitle = new Text("Active Games");
		lobbyTitle.setTextAlignment(TextAlignment.CENTER);
		Text lobbyText = new Text("Below are the games available to join:");
		Button joinGameButton = new Button("Join Game");
		Button createGameButton = new Button("Create Game");

		createGameButton.setOnAction(e -> {
			client.createGame();
			resetGamesTable();
		});
		
		joinGameButton.setOnAction(e -> {
			ReadOnlyObjectProperty<Board> selectedRow = gamesTable.getSelectionModel().selectedItemProperty();
			if(selectedRow != null) {	
				client.joinGame(selectedRow.get());
				
				// Delay to make sure game is joined before creating game page
				try {
					Thread.sleep(100);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
				
				GUIBoard boardPage = new GUIBoard(client);
				try {
					boardPage.start(stage);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {				
				System.out.println("No Game Selected");
			}			
		});
		
		gamesTable = new TableView<>();
		TableColumn<Board, Integer> gameIDColumn = new TableColumn<>("Game ID");
		gameIDColumn.setCellValueFactory(new PropertyValueFactory<>("gameID"));	    
		TableColumn<Board, Integer> player1Column = new TableColumn<>("Player 1");
		player1Column.setCellValueFactory(new PropertyValueFactory<>("player1"));	  
		TableColumn<Board, Integer> player2Column = new TableColumn<>("Player 2");
		player2Column.setCellValueFactory(new PropertyValueFactory<>("player2"));	   
		
		gamesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		gamesTable.getColumns().addAll(gameIDColumn, player1Column, player2Column);
		
		client.startObjectInThread();
		createLobbyRefresher();

		GridPane welcomeGrid = new GridPane();
		welcomeGrid.setPadding(new Insets(15));
		welcomeGrid.setHgap(5);
		welcomeGrid.setVgap(5);
		welcomeGrid.setAlignment(Pos.CENTER);
		welcomeGrid.setMinWidth(SCENE_WIDTH);
		welcomeGrid.setMinHeight(SCENE_HEIGHT);

		welcomeGrid.add(lobbyTitle, 0, 0);
		welcomeGrid.add(lobbyText, 0, 1);
		welcomeGrid.add(joinGameButton, 0, 2);
		welcomeGrid.add(createGameButton, 1, 2);
		welcomeGrid.add(gamesTable, 0, 3);
		GridPane.setConstraints(gamesTable, 0, 3, 2, 1);
		
		rootChildren.add(welcomeGrid);
		
		Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		scene.setFill(Color.BISQUE);
		stage.setScene(scene);
		stage.show();
	}
	
	public void resetGamesTable() {
		ArrayList<Board> boardList = client.getBoardList();
		if(!currentBoardList.containsAll(boardList)) {
			gamesTable.getItems().clear();		
			gamesTable.getItems().addAll(boardList);
			currentBoardList = boardList;
		}
	}
	
	private void createLobbyRefresher() {
		lobbyRefresher = new Timeline(new KeyFrame(Duration.millis(100), ev -> {
			resetGamesTable();
	    }));
		lobbyRefresher.setCycleCount(Animation.INDEFINITE);
		lobbyRefresher.play();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
