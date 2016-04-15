package test.system;

import api.Game;
import exc.GameStateException;
import exc.IllegalMoveException;
import impl.Complica;
import impl.ConnectFour;
import impl.ConsoleView;
import impl.GameController;
import impl.GraphicalView;
import util.Chip;

public class GameTest {
	
    public static void main(String[] args) throws InterruptedException, GameStateException, IllegalMoveException {
    	
	//the decider to which game to render is in a string form in Graphical View. couldnt find a non hacky way to pass a string to Javafx.
	GraphicalView view = new GraphicalView();
	view.launch(GraphicalView.class);
	}
}
