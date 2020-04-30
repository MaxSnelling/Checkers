package GUI;

import java.util.ArrayList;
import Game.Board;
import Game.Profile;
import Server.Client;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUIProfile extends Application {
	private Client client;
	private Stage stage;
	
	public GUIProfile(Client client) {
		this.client = client;
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		Profile profile = client.getProfile();
		
		stage.setTitle("Checkers " + profile.getUsername() + " Profile Page");
		Group root = new Group();

		GridPane grid = GUIMain.createGrid();
		
		Text titleText = new Text("Profile: " + profile.getUsername());
		Text firstNameLabel = new Text("First Name: ");
		Text firstNameText = new Text(profile.getFirstName());
		Text lastNameLabel = new Text("Last Name: ");
		Text lastNameText = new Text(profile.getLastName());
		Text emailAddressLabel = new Text("Email Address: ");
		Text emailAddressText = new Text(profile.getEmailAddress());
		Text dateOfBirthLabel = new Text("Date of Birth: ");
		Text dateOfBirthText = new Text(profile.getDateOfBirth().toString());
		Text recentGamesLabel= new Text("Recent Games: ");
		Button backButton = new Button("Back");
		
		TableView<Board> recentGamesTable = createRecentGamesTable();
		
		titleText.setFont(GUIMain.headingFont);
		firstNameLabel.setFont(GUIMain.boldFont);
		lastNameLabel.setFont(GUIMain.boldFont);
		emailAddressLabel.setFont(GUIMain.boldFont);
		dateOfBirthLabel.setFont(GUIMain.boldFont);
		recentGamesLabel.setFont(GUIMain.boldFont);
		firstNameText.setFont(GUIMain.standardFont);
		lastNameText.setFont(GUIMain.standardFont);
		emailAddressText.setFont(GUIMain.standardFont);
		dateOfBirthText.setFont(GUIMain.standardFont);
		backButton.setOnAction(eventHandlerBack);
		
		grid.add(titleText, 0, 0);
		grid.add(GUIMain.emptyText(), 0, 1);
		grid.add(firstNameLabel, 0, 2);
		grid.add(firstNameText,1, 2);
		grid.add(lastNameLabel, 0, 3);
		grid.add(lastNameText, 1, 3);
		grid.add(emailAddressLabel, 0, 4);
		grid.add(emailAddressText, 1, 4);
		grid.add(dateOfBirthLabel, 0, 5);
		grid.add(dateOfBirthText, 1, 5);		
		grid.add(recentGamesLabel, 0, 6);
		grid.add(recentGamesTable, 0, 7);
		grid.add(GUIMain.emptyText(), 0, 8);
		grid.add(backButton, 0, 9);
		
		GridPane.setHalignment(titleText, HPos.CENTER);
		GridPane.setColumnSpan(titleText, 2);
		GridPane.setColumnSpan(recentGamesTable, 2);
		
		root.getChildren().add(grid);
		
		Scene scene = new Scene(root, GUIMain.SCENE_WIDTH, GUIMain.SCENE_HEIGHT);
		scene.setFill(GUIMain.BACKGROUND_COLOUR);
		GUIMain.scenes.add(scene);
		stage.setScene(scene);
	}	
	
	private TableView<Board> createRecentGamesTable() {
		TableView<Board> recentGamesTable = new TableView<>();
		
		TableColumn<Board, Integer> gameIDColumn = new TableColumn<>("Game ID");
		gameIDColumn.setCellValueFactory(new PropertyValueFactory<>("gameID"));	    
		TableColumn<Board, Integer> player1Column = new TableColumn<>("Player 1");
		player1Column.setCellValueFactory(new PropertyValueFactory<>("player1"));	  
		TableColumn<Board, Integer> player2Column = new TableColumn<>("Player 2");
		player2Column.setCellValueFactory(new PropertyValueFactory<>("player2"));
		TableColumn<Board, Integer> winnerColumn = new TableColumn<>("Winner");
		winnerColumn.setCellValueFactory(new PropertyValueFactory<>("winner"));
		TableColumn<Board, Integer> timeStartedColumn = new TableColumn<>("Time Started");
		timeStartedColumn.setCellValueFactory(new PropertyValueFactory<>("timeStarted"));
		TableColumn<Board, Integer> timeEndedColumn = new TableColumn<>("Time Ended");
		timeEndedColumn.setCellValueFactory(new PropertyValueFactory<>("timeEnded"));
		
		recentGamesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		recentGamesTable.getColumns().addAll(gameIDColumn, player1Column, player2Column, winnerColumn, timeStartedColumn, timeEndedColumn);
		recentGamesTable.setMinWidth(GUIMain.SCENE_WIDTH*0.7);
		recentGamesTable.setMaxHeight(GUIMain.SCENE_HEIGHT*0.6);
		recentGamesTable.setFixedCellSize(25);
		
		addRecentGames(recentGamesTable);
		
		IntegerBinding rowNumber = Bindings.size(recentGamesTable.getItems());
		double cellSize = recentGamesTable.getFixedCellSize();		
		recentGamesTable.prefHeightProperty().bind(rowNumber.multiply(cellSize).add(30));
		
		return recentGamesTable;
	}
	
	private void addRecentGames(TableView<Board> gamesTable) {
		client.updateRecentGames();
		threadSleep();
		ArrayList<Board> recentGameList = client.getRecentGames();
		
		for(int i=1; i<recentGameList.size(); i++) {
			gamesTable.getItems().add(recentGameList.get(i));
		}		
	}
	
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
	
}
