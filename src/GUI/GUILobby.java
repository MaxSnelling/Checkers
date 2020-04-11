package GUI;

import java.util.ArrayList;

import Game.Board;
import Server.Client;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GUILobby extends Application {
	public static final int SCENE_WIDTH = 800;
	public static final int SCENE_HEIGHT = 400;
	private Client client;
	private TableView<Board> gamesTable;


	public GUILobby(Client client) {
		this.client = client;
	}

	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		ObservableList<Node> rootChildren = root.getChildren();

		Text lobbyTitle = new Text("Active Games");
		Text lobbyText = new Text("Below are the games available to join:");
		Button refreshButton = new Button("Refresh");
		Button joinGameButton = new Button("Join Game");
		Button createGameButton = new Button("Create Game");

		gamesTable = new TableView<>();
		TableColumn<Board, Integer> gameIDColumn = new TableColumn<>("Game ID");
		gameIDColumn.setCellValueFactory(new PropertyValueFactory<>("gameID"));	    
		TableColumn<Board, Integer> player1Column = new TableColumn<>("Player 1");
		player1Column.setCellValueFactory(new PropertyValueFactory<>("player1"));	  
		TableColumn<Board, Integer> player2Column = new TableColumn<>("Player 2");
		player2Column.setCellValueFactory(new PropertyValueFactory<>("player2"));	   

		resetGamesTable();
		
		gamesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		gamesTable.getColumns().addAll(gameIDColumn, player1Column, player2Column);
		
		createGameButton.setOnAction(e -> {
			client.createGame();
			resetGamesTable();
		});
		
		joinGameButton.setOnAction(e -> {
			Board selectedGame = gamesTable.getSelectionModel().selectedItemProperty().get();
			client.joinGame(selectedGame);
			resetGamesTable();
			
		});
		
		refreshButton.setOnAction(e -> {
			resetGamesTable();
		});

		GridPane welcomeGrid = new GridPane();
		welcomeGrid.setPadding(new Insets(15));
		welcomeGrid.setHgap(5);
		welcomeGrid.setVgap(5);
		welcomeGrid.setAlignment(Pos.CENTER);

		welcomeGrid.add(lobbyTitle, 0, 0);
		welcomeGrid.add(lobbyText, 0, 1);
		welcomeGrid.add(joinGameButton, 0, 2);
		welcomeGrid.add(createGameButton, 1, 2);
		welcomeGrid.add(refreshButton, 2, 2);
		welcomeGrid.add(gamesTable, 0, 3);
		
		rootChildren.add(welcomeGrid);
		
		Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		scene.setFill(Color.BISQUE);
		stage.setScene(scene);
		stage.show();
	}
	
	public void resetGamesTable() {
		gamesTable.getItems().clear();
		ArrayList<Board> gameList = client.getActiveGames();
		for(Board game:gameList) {
			System.out.println(game);
			gamesTable.getItems().add(game);
		}	
	}

	public static void main(String[] args) {
		launch(args);
	}
}
