package impl;


import java.util.Observable;
import java.util.Random;

import com.sun.prism.paint.Color;

import api.Game;
import api.View;
import exc.GameStateException;
import exc.IllegalMoveException;
import util.Chip;


public class ConsoleView  extends View {

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		this.render((Game) arg0);
	}

	@Override
	public void render(Game game) {

		Chip[][] gameBoard = game.getBoard();		
		int col = game.getColumns();
		int row = game.getRows();
		
		for(int i = 0; i < row ; i++ ){
			for(int j = 0; j < col ; j++ ){
				if (gameBoard[i][j].equals(Color.TRANSPARENT)){
					System.out.print("O ");
				}else if(gameBoard[i][j].equals(Color.RED)){
					System.out.print("R ");
				}else if(gameBoard[i][j].equals(Color.BLUE)){
					System.out.print("B ");
				}
			}
			System.out.println("\n");
		}
	
	}

	
	
}