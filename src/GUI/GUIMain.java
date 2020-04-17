package GUI;

import Server.Client;
import javafx.application.Application;
import javafx.stage.Stage;

public class GUIMain extends Application {
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
}
