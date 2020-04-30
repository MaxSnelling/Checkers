package GUI;

import Game.Profile;
import Server.Client;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUILogIn extends Application {
	TextField usernameField;
	PasswordField passwordField;
	Text incorrectDetailsText;
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
		incorrectDetailsText = new Text("Username/Password incorrect");
		Button logInButton = new Button("Log In");
		Button signUpButton = new Button("Sign Up");
		usernameField = new TextField();
		passwordField = new PasswordField();
		
		titleText.setFont(GUIMain.headingFont);
		incorrectDetailsText.setFont(GUIMain.standardFont);
		incorrectDetailsText.setVisible(false);
		
		logInButton.setOnAction(eventHandlerLogIn);	
		signUpButton.setOnAction(eventHandlerSignUp);

		grid.add(titleText, 0,0);
		grid.add(usernameText, 0, 2);
		grid.add(usernameField, 1, 2);
		grid.add(passwordText, 0, 3);
		grid.add(passwordField, 1,3);
		grid.add(signUpButton, 0, 4);
		grid.add(logInButton, 1, 4);	
		grid.add(GUIMain.emptyText(), 0, 5);
		grid.add(incorrectDetailsText, 0, 6);
		
		GridPane.setColumnSpan(titleText, 2);
		GridPane.setColumnSpan(incorrectDetailsText, 2);
		GridPane.setHalignment(incorrectDetailsText, HPos.CENTER);
		GridPane.setHalignment(titleText, HPos.CENTER);
		GridPane.setHalignment(signUpButton, HPos.RIGHT);		
		
		root.getChildren().add(grid);

		Scene scene = new Scene(root, GUIMain.SCENE_WIDTH, GUIMain.SCENE_HEIGHT);
		scene.setFill(GUIMain.BACKGROUND_COLOUR);
		GUIMain.scenes.add(scene);
		stage.setScene(scene);
		stage.show();
	}

	private final EventHandler<ActionEvent> eventHandlerLogIn = e -> {
		String usernameInput = usernameField.getText();
		String passwordInput = passwordField.getText();
		Profile inputProfile = new Profile(usernameInput, passwordInput);
		
		incorrectDetailsText.setVisible(false);
		if(client.passwordCheck(inputProfile)) {
			client.logIn(inputProfile);
			stage.setTitle("Checkers : " + usernameInput);
			GUIMain.openLobbyPage(stage);	
		} else {
			incorrectDetailsText.setVisible(true);
		}
	};
	
	private final EventHandler<ActionEvent> eventHandlerSignUp = e -> {
		GUIMain.openSignUpPage(stage);
	};

	public static void main(String[] args) {
		launch(args);
	}
}
