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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUILobby extends Application {
	private Stage stage;
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
		this.stage = stage;
		Group root = new Group();
		ObservableList<Node> rootChildren = root.getChildren();

		Text lobbyTitle = new Text("Active Games");
		lobbyTitle.setFont(Font.font(16));
		Button joinGameButton = new Button("Join Game");
		Button createGameButton = new Button("Create Game");

		createGameButton.setOnAction(eventHandlerCreateGame);		
		joinGameButton.setOnAction(eventHandlerJoinGame);
		
		gamesTable = createLobbyTable();	
		
		client.startObjectInThread();
		createLobbyRefresher();

		GridPane grid = GUIMain.createGrid();
		grid.add(lobbyTitle, 0, 0);	
		grid.add(joinGameButton, 0, 2);
		grid.add(createGameButton, 1, 2);		
		grid.add(gamesTable, 0, 3);
		
		GridPane.setHalignment(lobbyTitle, HPos.CENTER);
		GridPane.setConstraints(lobbyTitle, 0, 0, 2, 1);
		GridPane.setHalignment(createGameButton, HPos.RIGHT);
		GridPane.setConstraints(gamesTable, 0, 3, 2, 1);
//		GridPane.setHgrow(, Priority.ALWAYS);
//		GridPane.setVgrow(, Priority.ALWAYS);
		
		rootChildren.add(grid);
		
		Scene scene = new Scene(root, GUIMain.SCENE_WIDTH, GUIMain.SCENE_HEIGHT);
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
	
	private TableView<Board> createLobbyTable() {
		TableView<Board> gamesTable = new TableView<>();
		
		TableColumn<Board, Integer> gameIDColumn = new TableColumn<>("Game ID");
		gameIDColumn.setCellValueFactory(new PropertyValueFactory<>("gameID"));	    
		TableColumn<Board, Integer> player1Column = new TableColumn<>("Player 1");
		player1Column.setCellValueFactory(new PropertyValueFactory<>("player1"));	  
		TableColumn<Board, Integer> player2Column = new TableColumn<>("Player 2");
		player2Column.setCellValueFactory(new PropertyValueFactory<>("player2"));
		
		gamesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		gamesTable.getColumns().addAll(gameIDColumn, player1Column, player2Column);
		gamesTable.setMinWidth(GUIMain.SCENE_WIDTH*0.7);
		gamesTable.setMaxHeight(GUIMain.SCENE_HEIGHT*0.6);
		
		return gamesTable;
	}
	
	private final EventHandler<ActionEvent> eventHandlerCreateGame = e -> {
		client.createGame();
		resetGamesTable();
	};
	
	private final EventHandler<ActionEvent> eventHandlerJoinGame = e -> {
		ReadOnlyObjectProperty<Board> selectedRow = gamesTable.getSelectionModel().selectedItemProperty();
		if(selectedRow != null) {	
			client.joinGame(selectedRow.get());				
			// Delay to make sure game is joined before creating game page
			threadSleep();	
			GUIMain.openBoardPage(stage);				
		} else {				
			System.out.println("No Game Selected");
		}	
	};
	
	private void threadSleep() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}

	public static void main(String[] args) {
		launch(args);
	}
}
