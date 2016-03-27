package impl;

import java.util.ArrayList;
import java.util.Observable;

import api.Game;
import api.View;
import javafx.scene.paint.Color;
import util.Chip;

public class ConsoleView extends View{

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void render(Game game) {
		
		Chip[][] board = game.getBoard();
		int numR = game.getRows();
		int numC = game.getColumns();
		
		ArrayList<String> boardview = new ArrayList<String>();
		
		//add column numbers at the bottom of board
		boardview.add("|0||1||2||3||4||5||6||7||8|");
		boardview.add("---------------------------");
		
		//recursively generate board
		for(int i = 0; i < numR; i++){
			String temp = "";
			for(int j = 0; j < numC; j++){
				temp += "|" + printToken(board[i][j]) + "|";
			}
			boardview.add(temp);
		}
		
		//print board
		int boardLen = boardview.size() - 1;
		for(int i = 0; i < boardview.size(); i++){
			System.out.println(boardview.get(boardLen-i));
		}
		
	}
	
	//print R for RED tokens and B for BLUE tokens
	private String printToken(Chip token){
		String printT = " ";
		if(token.is(Color.RED)){
			printT = "R";
		}
		else if(token.is(Color.BLUE)){
			printT = "B";
		}
		return printT;
		
	}
	
}