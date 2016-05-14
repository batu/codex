package impl;

import exc.GameStateException;
import exc.IllegalMoveException;
import javafx.scene.paint.Color;
import util.Chip;

public class ComplicaGame extends MNKgame {
	
	
	int rowCount = 4;
	int columnCount = 7;
	
	public ComplicaGame(){
		int ComplicaRowCount = 4;
		int ComplicaColumnCount = 7;
		
		super.rowCount = ComplicaRowCount;
		super.columnCount = ComplicaColumnCount;
		
		super.MNKgameSetUp();
	}

	@Override
	public void placeDisk(int col) throws GameStateException, IllegalMoveException {
		// TODO Auto-generated method stub

		if(this.isGameOver()){
	    	return;
	    }	
	    
		if (col >= columnCount || col < 0){
			throw new IllegalMoveException();
		} 
		
		Color tempToken = null, tempToken2 = null;
		int currentRow = rowCount - 1;

		for (; currentRow >= 0; currentRow--){
			Chip currentChip = this.surface[currentRow][col];
			if (currentChip.is(Color.TRANSPARENT)){
				this.surface[currentRow][col].setValue(currentPlayer);
				controller.handleFillColor(currentRow, col, currentPlayer.get());
				
				this.changePlayer();
				return;
			}
		}
		
		if(currentRow == -1){
			currentRow = 0;
			tempToken = this.surface[currentRow][col].getValue();
			this.surface[currentRow][col].set(currentPlayer.getValue());
			controller.handleFillColor(currentRow, col, currentPlayer.getValue());
			currentRow++;
			for (; currentRow < rowCount; currentRow++){
				if(currentRow % 2  == 1){
					tempToken2 = this.surface[currentRow][col].getValue();
					this.surface[currentRow][col].set(tempToken);
					controller.handleFillColor(currentRow, col, tempToken);
				}else{
					tempToken = this.surface[currentRow][col].getValue();
					this.surface[currentRow][col].set(tempToken2);
					controller.handleFillColor(currentRow, col, tempToken2);
				}
			}
			
			this.changePlayer();
			this.isGameOver();
		}

		 throw new IllegalMoveException();	
	}
	
	@Override
	// isGameOver iterate all instead of calling it once and being done with it.
	public boolean isGameOver() {
		// TODO Auto-generated method stub
		int redWinCount=0, blueWinCount=0;
		for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                if(checkVertical(row, col) || checkHorizontal(row,  col) || checkDiagonal(row,  col)){
                	if(this.surface[row][col].getValue() == Color.RED){
                		redWinCount++;
                	}else{
                		blueWinCount++;
                	}
                }
            }
        }
		if(redWinCount != blueWinCount){
			if(redWinCount > blueWinCount){
				controller.handleFillColor(rowCount, columnCount, Color.RED);
			}else{
				controller.handleFillColor(rowCount, columnCount, Color.BLUE);
			}
			return true;
		}else{
			for(int row = 0; row < this.rowCount; row++){
				for(int col = 0; col < this.columnCount; col++){
					controller.handleSetWidth(row, col, 1);
					//super.board[row][col].setStrokeWidth(1);
				}
			}
			return false;
		}
		
	}

	@Override
	public int getRows() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColumns() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Chip[][] getBoard() {
		// TODO Auto-generated method stub
		return null;
	}

}
