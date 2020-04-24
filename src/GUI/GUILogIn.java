package GUI;

import Database.DatabaseQuery;
import Game.Profile;
import Server.Client;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUILogIn extends Application {
	TextField usernameField;
	TextField passwordField;
	public Stage stage;
	private Client client;



	public GUILogIn(Client client) {
		this.client = client;
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		stage.setTitle("Checkers");
		Group root = new Group();

		GridPane grid = GUIMain.createGrid();

		Text titleText = new Text("Checkers");
		Text usernameText = new Text("Username: ");
		Text passwordText = new Text("Password: ");
		Button logInButton = new Button("Log in");
		Button signUpButton = new Button("Sign up");
		usernameField = new TextField();
		passwordField = new TextField();
		
		titleText.setFont(Font.font(16));
		
		logInButton.setOnAction(eventHandlerLogIn);	
		signUpButton.setOnAction(eventHandlerSignUp);

		grid.add(titleText, 0,0);
		grid.add(usernameText, 0, 2);
		grid.add(usernameField, 1, 2);
		grid.add(passwordText, 0, 3);
		grid.add(passwordField, 1,3);
		grid.add(logInButton, 0, 4);
		grid.add(signUpButton, 1, 4);
		
		GridPane.setConstraints(titleText, 0, 0, 2, 1);
		GridPane.setHalignment(titleText, HPos.CENTER);
		GridPane.setHalignment(logInButton, HPos.RIGHT);
		
		root.getChildren().add(grid);

		Scene scene = new Scene(root, GUIMain.SCENE_WIDTH, GUIMain.SCENE_HEIGHT);
		scene.setFill(Color.BISQUE);
		stage.setScene(scene);
		stage.show();
	}

	private final EventHandler<ActionEvent> eventHandlerLogIn = e -> {
		String usernameInput = usernameField.getText();
		String passwordInput = passwordField.getText();
		
		Profile inputProfile = new Profile(usernameInput, passwordInput);
		if(DatabaseQuery.passwordCheck(inputProfile)) {
			client.logIn(inputProfile);
			stage.setTitle("Checkers : " + usernameInput);
			GUIMain.openLobbyPage(stage);	
		} else {
			System.out.println("Username/Password are incorrect");
		}
	};
	
	private final EventHandler<ActionEvent> eventHandlerSignUp = e -> {
		GUIMain.openSignUpPage(stage);
	};

	public static void main(String[] args) {
		launch(args);
	}
}
