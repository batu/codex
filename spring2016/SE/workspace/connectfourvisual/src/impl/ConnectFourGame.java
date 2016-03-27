package impl;
import java.util.Random;
import javafx.scene.paint.Color;

import exc.GameStateException;
import exc.IllegalMoveException;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Paint;
import util.Chip;

import java.awt.List;
import java.util.ArrayList;
import java.util.Observable;

import api.Game;

public class ConnectFourGame extends Game {
	int currentPlayer;
	int counter;
	Random random = new Random();
	Chip winner;
	Chip firstPlayer, secondPlayer, nullPlayer;
	
	public ConnectFourGame(int rows, int columns) {
		super(rows, columns);
		
		
		this.surface = new Chip[rows][columns];
		
		// TODO Auto-generated constructor stub
		
		if(random.nextBoolean()){
			this.currentPlayer = 1;
			this.counter = 1;
		} else {
			this.currentPlayer = 0;
			this.counter = 0;
		}
		
		firstPlayer = new Chip(Color.RED);
		secondPlayer = new Chip(Color.BLUE);
		nullPlayer = new Chip(Color.TRANSPARENT);
		
		//Initialize the board with EMPTY
		for (int row = 0; row < this.getRows(); row++) {
            for (int col = 0; col < this.getColumns(); col++) {
                this.surface[row][col] =  new Chip(Color.TRANSPARENT);
            }
        }
	}

	@Override
	public void placeDisk(int col) throws GameStateException, IllegalMoveException {
		// TODO Auto-generated method stub
		if (col >= this.getColumns() || col < 0){
			throw new IllegalMoveException();
		} 
		
		
		int currentRow = this.getRows() - 1;
		for (; currentRow >= 0; currentRow--){
			Chip currentChip = this.surface[currentRow][col];
			if (currentChip.equals(Color.TRANSPARENT)){
				this.surface[currentRow][col] = this.getCurrentPlayer();
				currentPlayer = counter++ %2;
				setChanged();
			    notifyObservers();
				return;
			}
		}
		 throw new IllegalMoveException();
		
		
	}


	@Override
	// isGameOver iterate all the instead of calling it once and being done with it.
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
		if (currentChip.equals(Color.TRANSPARENT)){return false;}	
		try{
			for(int i = 1; i < checkCount; i++){
				if(!surface[row + i][col].equals(currentChip)){
					return false;
				};
			}
			System.out.println("Vertical won");
			winner = currentChip;
			return true;
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}

	}
	

	public boolean checkHorizontal(int row, int col){
		
		int checkCount = 4;
		Chip currentChip = surface[row][col];
		if (currentChip.equals(Color.TRANSPARENT)){return false;}	
		try{
			if( col < (this.getColumns() - checkCount) - 1){
				for(int i = 1; i < checkCount; i++){
					if(!surface[row][col + i].equals(currentChip)){
						return false;
					};
				}
				winner = currentChip;
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
		if (currentChip.equals(Color.TRANSPARENT)){return false;}
		try{
			for(int i = 1; i < checkCount; i++){
				Chip ChipCheck = surface[row + i][col + i];
				if(ChipCheck.equals(currentChip)){
					winCounter++;
				};
			}
			if(winCounter == 3){
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
		if (currentChip.equals(Color.TRANSPARENT)){return false;}
		try{
			for(int i = 1; i < checkCount; i++){
				Chip ChipCheck = surface[row + i][col - i];
				if(ChipCheck.equals(currentChip)){
					winCounter++;
				};
			}
			if(winCounter == 3){
				winner = currentChip;
				return true;
			}
		} catch(ArrayIndexOutOfBoundsException e){
			
		}		
		return false;
	}

@Override
public Chip getWinningPlayer() throws GameStateException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Chip getCurrentPlayer() {
	if(currentPlayer == 1){
		return this.firstPlayer;
	}else{
		return this.secondPlayer;
	}

}

@Override
public void bindToWinningPlayer(ObjectProperty<Paint> property) {
	// TODO Auto-generated method stub
	
}

}
