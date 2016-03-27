package impl;

import api.Controller;
import api.Game;
import exc.GameStateException;
import exc.IllegalMoveException;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameController implements Controller{
	
	Pane pane = new Pane();
	private Game connectFour;
	
	public GameController(Game game){
		
		connectFour = game;
        
        double radius = 20.0f;
        double padding = 5.0f;
        int row, column;
        // Create a circle and set its properties
		for(row = 0; row < 6; row++){
			for(column = 0; column < 9; column++){
				double xcoord = padding * (column + 1) + radius * (2 * column + 1);
				double ycoord = padding * (row + 1) + radius * (2* row + 1);
				Circle circle = new Circle(xcoord, ycoord, radius);
		        circle.setStroke(Color.BLACK); 
		        circle.setFill(Color.WHITE);
		        pane.getChildren().add(circle);
		        final int r = row;
		        final int c = column;
		        circle.setOnMouseClicked(e -> {       
		        	handlePlaceDisk(r, c);
	            });
			}
		}
	}
	
	public Pane render(){
		return this.pane;
	}

	@Override
	public void handlePlaceDisk(int row, int col) {
		// TODO Auto-generated method stub
		try {
			connectFour.placeDisk(col);
		} catch (GameStateException | IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}