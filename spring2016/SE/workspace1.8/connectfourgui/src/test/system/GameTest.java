package test.system;

import api.Game;
import exc.GameStateException;
import exc.IllegalMoveException;
import impl.ConnectFourGame;
import impl.ConsoleView;
import impl.GameController;
import impl.GraphicalView;
import util.Chip;

public class GameTest {
    public static void main(String[] args) throws InterruptedException, GameStateException, IllegalMoveException {


	GraphicalView view = new GraphicalView();
	Game game = new ConnectFourGame();
	GameController gameController = new GameController(game);
	view.launch(GraphicalView.class);
	
	
	//game.addObserver(view);
	//render blank board
	//view.render(game);
	
	while (!game.isGameOver()) {
		//view.getPane(gameController.render());
	}
		try {
		    Chip winner = game.getWinningPlayer();
		    String gameEnd = "Game over. The winner is " + winner;
		    System.out.println(gameEnd);
		}
		catch (GameStateException e) {
		    System.out.println("It was a tie!");
		}
    }
}