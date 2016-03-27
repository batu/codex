package test.system;

import api.View;
import api.Game;
import exc.GameStateException;
import impl.ConnectFourGame;
import impl.ConsoleView;
import util.Chip;

public class GameTest {
    public static void main(String[] args) {
	
	 // Code to make your game interact should go here. Feel free
     //to alter this code depending on your implementation (what
     // is here is just an example).
	 
	Game game = new ConnectFourGame(6, 6);
	View view = new ConsoleView();
	
	while (!game.isGameOver()) {
	    view.render(game);
	}

	try {
	    Chip winner = game.getWinningPlayer();
	    System.out.println(winner);
	}
	catch (GameStateException e) {
	    System.out.println("It was a tie!");
	}
    }
}
