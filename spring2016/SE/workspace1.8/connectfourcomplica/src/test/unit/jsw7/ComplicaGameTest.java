package test.unit.jsw7;

import api.Game;
import exc.GameStateException;
import exc.IllegalMoveException;
import util.Chip;
import impl.ComplicaGame;
import impl.GraphicalView;

import org.junit.Test;
import java.util.Stack;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ComplicaGameTest {
    private Game diagonalWinnerSetup() throws GameStateException,
					      IllegalMoveException {
    	GraphicalView view = new GraphicalView();
        Game game = new ComplicaGame();
	
        for (int i = 0; i < 1; i++) {
    	    for (int j = 0; j < game.getColumns(); j++) {
    		game.placeDisk(j);
    	    }
    	    for (int j = game.getColumns() - 1; j >= 0; j--) {
    		game.placeDisk(j);
    	    }
    	}

        for (int j = 0; j < game.getColumns(); j++) {
            game.placeDisk(j);
        }

	return game;
    }

    private Game createTieGame() throws GameStateException,
					IllegalMoveException {
	Game game = diagonalWinnerSetup();

        for (int i = 0; i < 2; i++) {
    	    for (int j = 0; j < game.getColumns(); j++) {
    		game.placeDisk(j);
    	    }
        }
        
        for (int i = 0; i < 2; i++) {
            for (int j = game.getColumns() - 1; j >= 0; j--) {
                game.placeDisk(j);
            }
        }

        return game;
    }
    
    @Test
    public void testInitSetsPlayer() {
        Game game = new ComplicaGame();
        Chip chip = game.getCurrentPlayer();
        assertFalse(chip.is(Chip.INITIAL_COLOR));
    }

    @Test
    public void testInitBlanksBoard() {
        Game game = new ComplicaGame();
        Chip[][] surface = game.getBoard();

        for (int row = 0; row < game.getRows(); row++) {
            for (int col = 0; col < game.getColumns(); col++) {
                Chip chip = surface[row][col];
                assertTrue(chip.is(Chip.INITIAL_COLOR));
            }
        }
    }

    @Test
    public void testInitSetsRows() {
        Game game = new ComplicaGame();
        assertEquals(game.getRows(), 7);
    }

    @Test
    public void testInitSetsColumns() {
        Game game = new ComplicaGame();
        assertEquals(game.getColumns(), 4);
    }

    @Test
    public void testNextPlayerSetAfterPlacedDisk()
        throws GameStateException,
               IllegalMoveException {
        Game game = new ComplicaGame();
        
        Chip firstPlayer = new Chip(game.getCurrentPlayer());
        game.placeDisk(0);
        Chip secondPlayer = new Chip(game.getCurrentPlayer());
        
        assertNotEquals(firstPlayer, secondPlayer);
    }

    @Test(expected = IllegalMoveException.class)
    public void testDisksCannotBePlacedOutOfBounds()
        throws GameStateException,
               IllegalMoveException {
        Game game = new ComplicaGame();
        
        Chip firstPlayer = game.getCurrentPlayer();
        game.placeDisk(game.getColumns() + 1);
    }

    @Test(expected = IllegalMoveException.class)
    public void testDisksCannotBePlacedNegatively()
        throws GameStateException,
               IllegalMoveException {
        Game game = new ComplicaGame();
        
        Chip firstPlayer = game.getCurrentPlayer();
        game.placeDisk(-1);
    }
        
    @Test
    public void testVerticalWinnerFound() throws GameStateException,
                                                 IllegalMoveException {
	Game game = new ComplicaGame();
	
        for (int i = 0; i < 7; i++) {
            game.placeDisk(i % 2);
        }
	
        assertTrue(game.isGameOver());
    }

    @Test
    public void testWinningPlayerIsSet() throws GameStateException,
                                                IllegalMoveException {
	Game game = new ComplicaGame();
	Chip player = null;
	
        for (int i = 0; i < 7; i++) {
	    player = new Chip(game.getCurrentPlayer());
            game.placeDisk(i % 2);
        }
	
	assertEquals(game.getWinningPlayer(), player);
    }

    @Test
    public void testHorizontalWinnerFound() throws GameStateException,
                                                   IllegalMoveException {
        Game game = new ComplicaGame();

        for (int i = 0; i < 4; i++) {
            int stop = (i < 3) ? 2 : 1;
            for (int j = 0; j < stop; j++) {
                game.placeDisk(i);
            }
        }

        assertTrue(game.isGameOver());
    }

    @Test
    public void testOutofOrderHorizontalWinnerFound()
        throws GameStateException,
               IllegalMoveException {
        Game game = new ComplicaGame();

        for (int i = 0; i < 4; i++) {
            if (i != 1) {
                for (int j = 0; j < 2; j++) {
                    game.placeDisk(i);
                }
            }
        }
        game.placeDisk(1);
        
        assertTrue(game.isGameOver());
    }
	   
    @Test
    public void testLeftRightDiagonalWinnerFound()
	throws GameStateException,
	       IllegalMoveException {
	Game game = diagonalWinnerSetup();

        for (int i = 1; i >= 0; i--) {
            game.placeDisk(i);
        }
        
        assertTrue(game.isGameOver());
    }

    @Test
    public void testRightLeftDiagonalWinnerFound()
	throws GameStateException,
	       IllegalMoveException {
	Game game = diagonalWinnerSetup();

       	game.placeDisk(game.getColumns() - 1);
        assertTrue(game.isGameOver());
    }

    @Test
    public void thereIsNoTieGameIdentified() throws GameStateException,
                                                    IllegalMoveException {
        Game game = createTieGame();
        assertFalse(game.isGameOver());
    }

    @Test(expected = GameStateException.class)
    public void testTieHasNoWinner() throws GameStateException,
					    IllegalMoveException {
        Game game = createTieGame();
	game.getWinningPlayer();
    }

    @Test
    public void canAddToFullBoard() throws GameStateException,
                                           IllegalMoveException {
        Game game = createTieGame();
        game.placeDisk(0);
        game.notifyObservers();
    }

    @Test
    public void multipleWinnersIsTieGame() throws GameStateException,
                                                  IllegalMoveException {
        Game game = createTieGame();
        game.placeDisk(3);
        assertFalse(game.isGameOver());
    }

    @Test
    public void moreInARowsWins() throws GameStateException,
                                         IllegalMoveException {
        Game game = createTieGame();
        Chip player = new Chip(game.getCurrentPlayer());

        for (int i = 0; i < 2; i++) {
            game.placeDisk(3);
        }

        assertEquals(game.getWinningPlayer(), player);
    }

    @Test
    public void shiftMaintainsRestOfBoard() throws GameStateException,
                                                   IllegalMoveException {
        Game game1 = createTieGame();
        Game game2 = createTieGame();

        game2.placeDisk(0);

        Chip[][] g1 = game1.getBoard();
        Chip[][] g2 = game2.getBoard();
        for (int i = 0; i < game1.getRows(); i++) {
            for (int j = 1; j < game1.getColumns(); j++) {
                Chip a = g1[i][j];
                Chip b = g2[i][j];
                
                assertEquals(a, b);
            }
        }
    }
    
    @Test
    public void shiftIsWorking() throws GameStateException,
                                        IllegalMoveException {
        Game game1 = createTieGame();
        Game game2 = createTieGame();

        game2.placeDisk(0);

        Chip[][] g1 = game1.getBoard();
        Chip[][] g2 = game2.getBoard();
        for (int i = game1.getRows() - 1; i > 0; i--) {
            Chip a = g1[i-1][0];
            Chip b = g2[i][0];
            
            assertEquals(a, b);
        }

        assertEquals(game1.getCurrentPlayer(), g2[0][0]);
    }
}