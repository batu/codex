package impl;

import exc.GameStateException;
import exc.IllegalMoveException;
import javafx.scene.paint.Color;
import util.Chip;

public class ConnectFour extends MNKgame {
	public void placeDisk(int col) throws GameStateException, IllegalMoveException {
		// TODO Auto-generated method stub
	    if(this.isGameOver()){
	    	return;
	    }	
	    
		if (col >= this.getColumns() || col < 0){
			throw new IllegalMoveException();
		} 
		
		
		int currentRow = this.getRows() - 1;
		for (; currentRow >= 0; currentRow--){
			Chip currentChip = this.surface[currentRow][col];
			if (currentChip.is(Color.TRANSPARENT)){
				this.surface[currentRow][col].setValue(currentPlayer);
				controller.handleFillColor(currentRow, col, currentPlayer.get());

				this.changePlayer();
				return;
			}
		}
		 throw new IllegalMoveException();	
	}
	
	public boolean isGameOver() {
		// TODO Auto-generated method stub
		for (int row = 0; row < this.getRows(); row++) {
            for (int col = 0; col < this.getColumns(); col++) {
                if(checkVertical(row, col) || checkHorizontal(row,  col) || checkDiagonal(row,  col)){
                	return true;
                }
            }
        }
		return false;
	}
}
