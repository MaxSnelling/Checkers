package GUI;

import java.util.ArrayList;

import Server.Client;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUIMain extends Application {
	public static final int SCENE_WIDTH = 1000;
	public static final int SCENE_HEIGHT = 800;
	public static final Color BACKGROUND_COLOUR = Color.BISQUE;
	public static ArrayList<Scene> scenes;
	private static Client client;
	public static final Font headingFont = Font.font("Verdana", FontWeight.BOLD, 24); 
	public static final Font boldFont = Font.font("Verdana", FontWeight.BOLD, 16); 
	public static final Font standardFont = Font.font("Verdana", 16); 

	@Override
	public void start(Stage stage) throws Exception {
		scenes = new ArrayList<>();
		GUILogIn loginPage = new GUILogIn(client);		
		loginPage.start(stage);
	}

	public static void main(String[] args) {
		client = new Client(); 
		launch(args);
	}
	
	@Override
	public void stop(){
		if(client.getProfile() != null)
			client.logOut();
	}
	
	public static GridPane createGrid() {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(15));
		grid.setHgap(10);
		grid.setVgap(10);
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
	
	public static void openProfilePage(Stage stage) {
		GUIProfile profilePage = new GUIProfile(client);	
		try {
			profilePage.start(stage);
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
	
	public static void showPreviousScene(Stage stage) {
		Scene previousScene = scenes.get(scenes.size()-2);
		stage.setScene(previousScene);
		updateSceneListOrder();
	}
	
	private static void updateSceneListOrder() {
		int sceneListSize = scenes.size();
		Scene newFinalScene = scenes.get(sceneListSize-2);
		scenes.set(sceneListSize-2, scenes.get(sceneListSize-1));
		scenes.set(sceneListSize-1, newFinalScene);
	}
	
	public static Text emptyText() {
		Text emptyText = new Text(" ");
		emptyText.setFont(Font.font(50));
		return emptyText;
	}
}
