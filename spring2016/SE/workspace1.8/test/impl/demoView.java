package impl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


public class demoView extends Application{

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {    
        // Create a pane to hold the circle 
        Pane pane = new Pane();
    
        double radius = 20.0f;
        double padding = 5.0f;
        // Create a circle and set its properties
		for(int i = 0; i < 6; i++){
			for(int j = 0; j < 9; j++){
				Circle circle = new Circle(padding*(j+1) + radius*(2*j+1), padding*(i+1) + radius*(2*i+1), radius);
		        circle.setStroke(Color.BLACK); 
		        circle.setFill(Color.WHITE);
		        pane.getChildren().add(circle);
		        circle.setOnMouseClicked(e -> {       
		        	circle.setFill(Color.RED);
	            });
			}
		}

        // Create a scene and place it in the stage
        Scene scene = new Scene(pane, 415, 300);
        primaryStage.setTitle("ConnectFour"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }
  
    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}