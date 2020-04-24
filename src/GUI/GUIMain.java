package GUI;

import Server.Client;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GUIMain extends Application {
	public static final int SCENE_WIDTH = 1000;
	public static final int SCENE_HEIGHT = 800;
	private static Client client;

	@Override
	public void start(Stage stage) throws Exception {
		GUILogIn loginPage = new GUILogIn(client);		
		loginPage.start(stage);
	}

	public static void main(String[] args) {
		client = new Client(); 
		launch(args);
	}
	
	public static GridPane createGrid() {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(15));
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setAlignment(Pos.CENTER);
		grid.setMinWidth(SCENE_WIDTH);
		grid.setMinHeight(SCENE_HEIGHT);
		return grid;
	}
	
	public static void openLogInPage(Stage stage) {
		GUILogIn loginPage = new GUILogIn(client);	
		try {
			loginPage.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void openSignUpPage(Stage stage) {
		GUISignUp signUpPage = new GUISignUp(client);	
		try {
			signUpPage.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void openLobbyPage(Stage stage) {
		GUILobby lobbyPage = new GUILobby(client);	
		try {
			lobbyPage.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void openBoardPage(Stage stage) {
		GUIBoard boardPage = new GUIBoard(client);	
		try {
			boardPage.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
