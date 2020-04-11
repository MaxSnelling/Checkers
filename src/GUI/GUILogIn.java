package GUI;


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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GUILogIn extends Application {
	public static final int SCENE_WIDTH = 800;
	public static final int SCENE_HEIGHT = 400;
	public Stage stage;
	private Client client;



	public GUILogIn(Client client) {
		this.client = client;
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		Group root = new Group();
		ObservableList<Node> rootChildren = root.getChildren();

		GridPane welcomeGrid = new GridPane();
		welcomeGrid.setPadding(new Insets(15));
		welcomeGrid.setHgap(5);
		welcomeGrid.setVgap(5);
		welcomeGrid.setAlignment(Pos.CENTER);

		Text titleText = new Text("Checkers");
		titleText.setTextAlignment(TextAlignment.CENTER);
		Text welcomeText = new Text("Welcome to our new online checkers game. Please Log in");
		TextField usernameTextField = new TextField();
		Button logInButton = new Button("Log In");

		logInButton.setOnAction(e -> {
			client.logIn(usernameTextField.getText());
			openLobbyPage();			 
		});

		welcomeGrid.add(titleText, 0, 1);
		GridPane.setHalignment(titleText, HPos.CENTER);
		welcomeGrid.add(welcomeText, 0, 3);	    
		welcomeGrid.add(usernameTextField, 0, 4);
		welcomeGrid.add(logInButton, 1, 4);	    
		rootChildren.add(welcomeGrid);

		Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		scene.setFill(Color.BISQUE);
		stage.setScene(scene);
		stage.show();
	}

	void openLobbyPage() {
		GUILobby lobbyPage = new GUILobby(client);
		try {
			lobbyPage.start(stage);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
