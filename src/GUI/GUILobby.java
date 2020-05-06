package GUI;

import java.util.ArrayList;
import Game.Board;
import Server.Client;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Lobby GUI page shown after logging in
 * @author Max Snelling
 * @version 5/5/20
 */
public class GUILobby extends Application {
	private Stage stage;
	private Client client;
	private ArrayList<Board> currentBoardList;
	private TableView<Board> gamesTable;
	private Button reJoinButton;
	private Timeline lobbyRefresher;


	public GUILobby(Client client) {
		this.client = client;
		currentBoardList = new ArrayList<>();
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		Group root = new Group();

		Text lobbyTitle = new Text("Active Games");
		lobbyTitle.setFont(GUIMain.headingFont);
		Button joinGameButton = new Button("Join Game");
		Button createGameButton = new Button("Create Game");
		Button viewProfileButton = new Button("View Profle");
		Button logOutButton = new Button("Log Out");
		reJoinButton = new Button("Re-join Game");

		createGameButton.setOnAction(eventHandlerCreateGame);		
		joinGameButton.setOnAction(eventHandlerJoinGame);
		viewProfileButton.setOnAction(eventHandlerViewProfile);
		logOutButton.setOnAction(eventHandlerLogOut);
		reJoinButton.setOnAction(eventHandlerBack);
		
		gamesTable = createLobbyTable();	

		GridPane grid = GUIMain.createGrid();
		grid.add(lobbyTitle, 0, 0);
		grid.add(GUIMain.emptyText(), 0, 1);
		grid.add(joinGameButton, 0, 2);
		grid.add(createGameButton, 1, 2);	
		grid.add(viewProfileButton, 2, 2);
		grid.add(gamesTable, 0, 3);
		grid.add(logOutButton, 0, 4);
		grid.add(reJoinButton, 1, 4);
		reJoinButton.setVisible(false);
		
		GridPane.setHalignment(lobbyTitle, HPos.CENTER);
		GridPane.setColumnSpan(lobbyTitle, 3);
		GridPane.setHalignment(joinGameButton, HPos.CENTER);
		GridPane.setHalignment(createGameButton, HPos.CENTER);
		GridPane.setHalignment(viewProfileButton, HPos.CENTER);
		GridPane.setColumnSpan(gamesTable, 3);
//		grid.gridLinesVisibleProperty().set(true);
		
        ColumnConstraints colConst0 = new ColumnConstraints();
        ColumnConstraints colConst1 = new ColumnConstraints();
        ColumnConstraints colConst2 = new ColumnConstraints();
        colConst0.setPercentWidth(25);
        colConst1.setPercentWidth(25);
        colConst2.setPercentWidth(25);
        grid.getColumnConstraints().add(colConst0);
        grid.getColumnConstraints().add(colConst1);
        grid.getColumnConstraints().add(colConst2);
		
        root.getChildren().add(grid);
        
        client.startObjectInThread();
		createLobbyRefresher();
		
		Scene scene = new Scene(root, GUIMain.SCENE_WIDTH, GUIMain.SCENE_HEIGHT);
		scene.setFill(GUIMain.BACKGROUND_COLOUR);
		GUIMain.scenes.add(scene);
		stage.setScene(scene);
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
	};
	
	private final EventHandler<ActionEvent> eventHandlerJoinGame = e -> {
		ReadOnlyObjectProperty<Board> selectedRow = gamesTable.getSelectionModel().selectedItemProperty();
		if(selectedRow != null && !client.isPlaying()) {	
			reJoinButton.setVisible(true);
			client.joinGame(selectedRow.get());				
			// Delay to make sure game is joined before creating game page
			threadSleep();	
			GUIMain.openBoardPage(stage);				
		} else {				
			System.out.println("No Game Selected");
		}	
	};
	
	private final EventHandler<ActionEvent> eventHandlerViewProfile = e -> {
		GUIMain.openProfilePage(stage);
	};
	
	private final EventHandler<ActionEvent> eventHandlerLogOut = e -> {
		client.logOut();
		client.stopObjectInThread();
		Platform.exit();
		System.exit(0);
		GUIMain.main(null);
	};
	
	private void threadSleep() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	private final EventHandler<ActionEvent> eventHandlerBack = e -> {
		GUIMain.showPreviousScene(stage);
	};

	public static void main(String[] args) {
		launch(args);
	}
}
