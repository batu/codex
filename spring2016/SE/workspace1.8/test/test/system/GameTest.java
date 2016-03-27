package test.system;

import api.View;

import java.util.InputMismatchException;
import java.util.Scanner;

import api.Controller;
import api.Game;
import exc.GameStateException;
import exc.IllegalMoveException;
import impl.ConnectFourGame;
import impl.ConsoleView;
import impl.GameController;
import impl.GraphicalView;
import javafx.application.Application;
import util.Chip;

public class GameTest {
    public static void main(String[] args) throws InterruptedException, GameStateException, IllegalMoveException {

	Game game = new ConnectFourGame();
	GameController gameController = new GameController(game);
	GraphicalView view = new GraphicalView();
	view.launch(GraphicalView.class);
	
	
	//game.addObserver(view);
	//render blank board
	//view.render(game);
	
	while (!game.isGameOver()) {
		view.getPane(gameController.render());
	}

		//check for winner when isGameOver is true
		try {
		    Chip winner = game.getWinningPlayer();
		    String winnermessage = "Game over. The winner is " + winner;
		    System.out.println(winnermessage);
		}
		catch (GameStateException e) {
		    System.out.println("It was a tie!");
		}
    }
}