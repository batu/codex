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
	}
}
