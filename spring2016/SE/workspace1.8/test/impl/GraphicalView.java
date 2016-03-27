package impl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class GraphicalView extends Application{

	Pane pane;
	
    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {    

        // Create a scene and place it in the stage
        Scene scene = new Scene(pane, 415, 300);
        primaryStage.setTitle("ConnectFour"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }
    
    public void getPane(Pane npane){
    	this.pane = npane;
    }
  
    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}