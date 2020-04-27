package GUI;

import java.sql.Date;

import Database.DatabaseInsert;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUISignUp extends Application {
	private Client client;
	private Stage stage;	
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
		firstNameField = new TextField();
		lastNameField = new TextField();
		emailAddressField = new TextField();
		dateOfBirthField = new DatePicker();
		usernameField = new TextField();
		passwordField = new TextField();
		rePasswordField = new TextField();
		Button signUpButton = new Button("Sign up");
		Button backButton = new Button("Back");
		
		titleText.setFont(Font.font(16));
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
		
		
		GridPane.setConstraints(titleText, 0, 0, 2, 1);
		GridPane.setHalignment(titleText, HPos.CENTER);
		GridPane.setHalignment(backButton, HPos.CENTER);
		GridPane.setHalignment(signUpButton, HPos.CENTER);		
		
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
		
		if(passwordInput.equals(rePasswordInput) ) {			
			if(client.usernameCheck(usernameInput)) {

				Profile inputProfile = new Profile(usernameInput, firstNameInput, lastNameInput, 
	        									passwordInput, dateOfBirthInput, emailAddressInput);
				client.addProfileToDatabase(inputProfile);
				
				GUIMain.openLogInPage(stage);
			} else {
				System.out.println("Username already in use");
			}
		} else {
			System.out.println("Passwords don't match");
		}		
    };
    
    private final EventHandler<ActionEvent> eventHandlerBack = e -> {
		GUIMain.showPreviousScene(stage);
    };	

}
