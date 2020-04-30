package GUI;

import java.sql.Date;

import Game.Profile;
import Server.Client;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUISignUp extends Application {
	private Client client;
	private Stage stage;	
	Text usernameInUseText;
	Text passwordsDontMatchText;
	private TextField firstNameField;
	private TextField lastNameField;
	private TextField emailAddressField;
	private DatePicker dateOfBirthField;
	private TextField usernameField;
	private TextField passwordField;
	private TextField rePasswordField;
	
	public GUISignUp(Client client) {
		this.client = client;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		stage.setTitle("Checkers Sign Up");
		Group root = new Group();

		GridPane grid = GUIMain.createGrid();
		
		Text titleText = new Text("Sign-Up");
		Text firstNameText = new Text("First Name: ");
		Text lastNameText = new Text("Last Name: ");
		Text emailAddressText = new Text("Email Address: ");
		Text dateOfBirthText = new Text("Date of Birth: ");
		Text usernameText = new Text("Username: ");
		Text passwordText = new Text("Password: ");
		Text rePasswordText = new Text("Re-enter Password: ");
		usernameInUseText = new Text("Username already in use");
		passwordsDontMatchText = new Text("Passwords entered don't match");
		firstNameField = new TextField();
		lastNameField = new TextField();
		emailAddressField = new TextField();
		dateOfBirthField = new DatePicker();
		usernameField = new TextField();
		passwordField = new TextField();
		rePasswordField = new TextField();
		Button signUpButton = new Button("Sign up");
		Button backButton = new Button("Back");
		
		titleText.setFont(GUIMain.headingFont);
		firstNameText.setFont(GUIMain.standardFont);
		lastNameText.setFont(GUIMain.standardFont);
		emailAddressText.setFont(GUIMain.standardFont);
		dateOfBirthText.setFont(GUIMain.standardFont);
		usernameText.setFont(GUIMain.standardFont);
		passwordText.setFont(GUIMain.standardFont);
		rePasswordText.setFont(GUIMain.standardFont);
		usernameInUseText.setFont(GUIMain.boldFont);
		passwordsDontMatchText.setFont(GUIMain.boldFont);
		usernameInUseText.setVisible(false);
		passwordsDontMatchText.setVisible(false);
		signUpButton.setOnAction(eventHandlerSignUp);
		backButton.setOnAction(eventHandlerBack);
		
		grid.add(titleText, 0, 0);
		grid.add(GUIMain.emptyText(), 0, 1);
		grid.add(firstNameText, 0, 2);
		grid.add(firstNameField, 1, 2);
		grid.add(lastNameText, 0, 3);
		grid.add(lastNameField, 1, 3);
		grid.add(emailAddressText, 0, 4);
		grid.add(emailAddressField, 1, 4);
		grid.add(dateOfBirthText, 0, 5);
		grid.add(dateOfBirthField, 1, 5);
		grid.add(usernameText, 0, 6);
		grid.add(usernameField, 1, 6);
		grid.add(passwordText, 0, 7);
		grid.add(passwordField, 1, 7);
		grid.add(rePasswordText, 0, 8);
		grid.add(rePasswordField, 1, 8);	
		grid.add(GUIMain.emptyText(), 0, 9);
		grid.add(signUpButton, 1, 10);
		grid.add(backButton, 0, 10);
		grid.add(GUIMain.emptyText(), 0, 11);
		grid.add(usernameInUseText, 0, 12);
		grid.add(passwordsDontMatchText, 0, 13);
		
		
		GridPane.setColumnSpan(titleText, 2);
		GridPane.setColumnSpan(usernameInUseText, 2);
		GridPane.setColumnSpan(passwordsDontMatchText, 2);
		GridPane.setHalignment(titleText, HPos.CENTER);
		GridPane.setHalignment(backButton, HPos.CENTER);
		GridPane.setHalignment(signUpButton, HPos.CENTER);		
		GridPane.setHalignment(usernameInUseText, HPos.CENTER);
		GridPane.setHalignment(passwordsDontMatchText, HPos.CENTER);	
		
		root.getChildren().add(grid);		
		Scene scene = new Scene(root, GUIMain.SCENE_WIDTH, GUIMain.SCENE_HEIGHT);
		scene.setFill(GUIMain.BACKGROUND_COLOUR);
		GUIMain.scenes.add(scene);
		stage.setScene(scene);
	}	
	
	private final EventHandler<ActionEvent> eventHandlerSignUp = e -> {
		String usernameInput = usernameField.getText();
		String firstNameInput = firstNameField.getText();
		String lastNameInput = lastNameField.getText();		
		String passwordInput = passwordField.getText();
		String rePasswordInput = rePasswordField.getText();
		Date dateOfBirthInput = Date.valueOf(dateOfBirthField.getValue());
		String emailAddressInput = emailAddressField.getText();
		
		passwordsDontMatchText.setVisible(false);
		usernameInUseText.setVisible(false);
		
		if(passwordInput.equals(rePasswordInput) ) {			
			if(client.usernameCheck(usernameInput)) {

				Profile inputProfile = new Profile(usernameInput, firstNameInput, lastNameInput, 
	        									passwordInput, dateOfBirthInput, emailAddressInput);
				client.addProfileToDatabase(inputProfile);
				
				GUIMain.openLogInPage(stage);
			} else {
				usernameInUseText.setVisible(true);
			}
		} else {
			passwordsDontMatchText.setVisible(true);
		}		
    };
    
    private final EventHandler<ActionEvent> eventHandlerBack = e -> {
		GUIMain.showPreviousScene(stage);
    };	

}
