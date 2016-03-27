package impl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class GraphicalView extends Application{

    GridPane gridpane = new GridPane();
	
    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {    

      
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