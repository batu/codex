package test.system;

import api.View;
import api.Game;
import api.Game.Token;
import exc.GameStateException;
import impl.ConnectFourGame;
import impl.ConnectFourView;

public class GameTest {
    public static void main(String[] args) {
	/*
	 * Code to make your game interact should go here. Feel free
	 * to alter this code depending on your implementation (what
	 * is here is just an example).
	 */
	Game game = new ConnectFourGame();
	View view = new ConnectFourView();
	
	while (!game.isGameOver()) {
	    view.render(game);
	}

	try {
	    Token winner = game.getWinner();
	    System.out.println(winner);
	}
	catch (GameStateException e) {
	    System.out.println("It was a tie!");
	}
    }
}
