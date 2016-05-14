package impl;

import api.Controller;
import api.Game;
import exc.GameStateException;
import exc.IllegalMoveException;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import util.Chip;

public class GameController implements Controller{
	
	private Game connectFour;
	GraphicalView view; 
	
	public GameController(Game game){
		connectFour = game;
	}
	
	
	public void handleFillColor(int row, int col, Color color){
		view.fillColor(row, col, color);
	}
	
	public void setView(GraphicalView view){
		this.view = view;
	}
	
	
	public void handleSetWidth(int row, int col, int width) {
		this.view.board[row][col].setStrokeWidth(width);
	}
	
	@Override
	public void handlePlaceDisk(int row, int col) {
		// TODO Auto-generated method stub
		try {
			connectFour.placeDisk(col);
		} catch (GameStateException | IllegalMoveException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	
}