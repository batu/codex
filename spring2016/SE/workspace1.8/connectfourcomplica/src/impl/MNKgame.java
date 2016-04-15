package impl;

import java.util.Random;

import api.Game;
import exc.GameStateException;
import exc.IllegalMoveException;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import util.Chip;

public abstract class MNKgame extends Game{
		Random random = new Random();
		GameController controller;
		
		Chip currentPlayer;
		Chip winner;
		Chip redPlayer = new Chip(Color.RED);
		Chip bluePlayer = new Chip(Color.BLUE);
		Circle[][] board;
		
		static int rowCount;
		static int columnCount;
		
		void setController(GameController controller){
			this.controller = controller;
		}
		
		
		public void MNKgameSetUp(int rowCount, int columnCount){
			
			currentPlayer = redPlayer;
			
			super.setRowsColumns(rowCount, columnCount);
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
		
		public void placeDiskConnectFour(int col) throws GameStateException, IllegalMoveException {
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
	
		
		public void changePlayer(){
			if(currentPlayer.is(Color.RED)){
				currentPlayer = bluePlayer;
				controller.handleFillColor(rowCount, columnCount, currentPlayer.get());
			}
			else{
				currentPlayer = redPlayer;
				controller.handleFillColor(rowCount, columnCount, currentPlayer.get());
			}
			
			if(!this.isGameOver() && currentPlayer.is(Color.BLUE)){
				controller.handleFillColor(rowCount, columnCount, Color.BLUE);
			}else{
				controller.handleFillColor(rowCount, columnCount, Color.RED);
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
			
			Color tempToken = null, tempToken2 = null;
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
			
			if(currentRow == -1){
				currentRow = 0;
				tempToken = this.surface[currentRow][col].getValue();
				this.surface[currentRow][col].set(currentPlayer.getValue());
				controller.handleFillColor(currentRow, col, currentPlayer.getValue());
				currentRow++;
				for (; currentRow < this.getRows(); currentRow++){
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
	
		public boolean isGameOverConnectFour() {
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
	
		@Override
		// isGameOver iterate all instead of calling it once and being done with it.
		public boolean isGameOver() {
			// TODO Auto-generated method stub
			int redWinCount=0, blueWinCount=0;
			for (int row = 0; row < this.getRows(); row++) {
	            for (int col = 0; col < this.getColumns(); col++) {
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
						//board[row][col].setStrokeWidth(1);
					}
				}
				return false;
			}
			
		}
	
		public boolean checkVertical(int row, int col){
	
			int checkCount = 4;
			Chip currentChip = surface[row][col];
			if (currentChip.is(Color.TRANSPARENT)){return false;}	
			try{
				if( row < (this.getRows() - checkCount) + 1){
					for(int i = 1; i < checkCount; i++){
						if(!surface[row + i][col].is(currentChip.getValue())){
							return false;
						};
					}
		
					for(int i = 0; i < checkCount; i++){
						controller.handleSetWidth(row+i, col, 7);
						//board[row + i][col].setStrokeWidth(7);
					}
					winner = currentChip;
					return true;
				}
			}catch(ArrayIndexOutOfBoundsException e){
				return false;
			}
			return false;
		}
		
	
		public boolean checkHorizontal(int row, int col){
			
			int checkCount = 4;
			Chip currentChip = surface[row][col];
			if (currentChip.is(Color.TRANSPARENT)){return false;}	
			try{
				if( col < (this.getColumns() - checkCount) + 1){
					for(int i = 1; i < checkCount; i++){
						if(!surface[row][col + i].is(currentChip.getValue())){
							return false;
						};
					}
					
					
					winner = currentChip;
					for(int i = 0; i < checkCount; i++){
						controller.handleSetWidth(row, col + i, 7);
						//board[row][col + i].setStrokeWidth(7);
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
						controller.handleSetWidth(row + i, col + i, 7);
						//board[row + i][col + i].setStrokeWidth(7);
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
						controller.handleSetWidth(row + i, col - i, 7);
						//board[row + i][col - i].setStrokeWidth(7);
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
