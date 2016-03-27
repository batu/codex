package impl;
import java.util.Random;

import exc.GameStateException;
import exc.IllegalMoveException;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import util.Chip;


import api.Game;

public class ConnectFourGame extends Game {
	
	Random random = new Random();
	
	Chip currentPlayer;
	Chip winner;
	Chip redPlayer = new Chip(Color.RED);
	Chip bluePlayer = new Chip(Color.BLUE);
	Circle[][] board;
	
	static int rowCount = 6;
	static int columnCount = 9;
	public ConnectFourGame() {
		super(rowCount, columnCount);
		
		currentPlayer = redPlayer;
		
		this.surface = new Chip[rowCount][columnCount];
		
		for(int row = 0; row < super.getRows(); row++){
			for(int column = 0; column < super.getColumns(); column++){
				super.surface[row][column] = new Chip();
			}
		}
		
		//Initialize the board with EMPTY
		for (int row = 0; row < this.getRows(); row++) {
            for (int col = 0; col < this.getColumns(); col++) {
                this.surface[row][col].set(Color.TRANSPARENT);
            }
        }
	}

	@Override
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
				board[currentRow][col].setFill(currentPlayer.get());
				
				if(currentPlayer.is(Color.RED)){
					currentPlayer = bluePlayer;
					board[rowCount][columnCount].setFill(currentPlayer.get());
				}
				else{
					currentPlayer = redPlayer;
					board[rowCount][columnCount].setFill(currentPlayer.get());
				}
				
				if(this.isGameOver() && currentPlayer.is(Color.RED)){
					board[rowCount][columnCount].setFill(Color.BLUE);
				}else{
					board[rowCount][columnCount].setFill(Color.RED);
				}
				
				setChanged();
			    notifyObservers();

			    			    
	    
				return;
			}
		}
		 throw new IllegalMoveException();
		
		
	}



	@Override
	// isGameOver iterate all instead of calling it once and being done with it.
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

	public boolean checkVertical(int row, int col){

		int checkCount = 4;
		Chip currentChip = surface[row][col];
		if (currentChip.is(Color.TRANSPARENT)){return false;}	
		try{
			for(int i = 1; i < checkCount; i++){
				if(!surface[row + i][col].is(currentChip.getValue())){
					return false;
				};
			}

			
			
			for(int i = 0; i < checkCount +1; i++){
				board[row + i][col].setStrokeWidth(7);
			}
			winner = currentChip;
			return true;
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}

	}
	

	public boolean checkHorizontal(int row, int col){
		
		int checkCount = 4;
		Chip currentChip = surface[row][col];
		if (currentChip.is(Color.TRANSPARENT)){return false;}	
		try{
			if( col < (this.getColumns() - checkCount) - 1){
				for(int i = 1; i < checkCount; i++){
					if(!surface[row][col + i].is(currentChip.getValue())){
						return false;
					};
				}
				
				
				winner = currentChip;
				for(int i = 0; i < checkCount; i++){
					board[row][col + i].setStrokeWidth(7);
				}
				return true;
			}
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
		return false;
	}

	public boolean checkDiagonal(int row, int col){
		if(checkDiagonalLeft(row, col) || checkDiagonalRight(row, col)){
			return true;
		} else{
			return false;
		}
	}
	
public boolean checkDiagonalLeft(int row, int col){
		int checkCount = 4;
		int winCounter = 0;
		Chip currentChip = surface[row][col];
		if (currentChip.is(Color.TRANSPARENT)){return false;}
		try{
			for(int i = 1; i < checkCount; i++){
				Chip ChipCheck = surface[row + i][col + i];
				if(ChipCheck.is(currentChip.getValue())){
					winCounter++;
				};
			}
			if(winCounter == 3){
				for(int i = 0; i < checkCount; i++){
					board[row + i][col + i].setStrokeWidth(7);
				}
				winner = currentChip;
				return true;
			}
		} catch(ArrayIndexOutOfBoundsException e){
			
		}		
		return false;
	}

public boolean checkDiagonalRight(int row, int col){
		int checkCount = 4;
		int winCounter = 0;
		Chip currentChip = surface[row][col];
		if (currentChip.is(Color.TRANSPARENT)){return false;}
		try{
			for(int i = 1; i < checkCount; i++){
				Chip ChipCheck = surface[row + i][col - i];
				if(ChipCheck.is(currentChip.getValue())){
					winCounter++;
				};
			}
			if(winCounter == 3){
				winner = currentChip;
				for(int i = 0; i < checkCount; i++){
					board[row + i][col - i].setStrokeWidth(7);
				}
				return true;
			}
		} catch(ArrayIndexOutOfBoundsException e){
			
		}		
		return false;
	}


public void setBoard(Circle[][] board){
	this.board = board;
}
@Override
public Chip getWinningPlayer() throws GameStateException {
	// TODO Auto-generated method stub
	return winner;
}

@Override
public Chip getCurrentPlayer() {
	// TODO Auto-generated method stub
	return currentPlayer;
}

@Override
public void bindToWinningPlayer(ObjectProperty<Paint> property) {
	// TODO Auto-generated method stub
	
}

}
