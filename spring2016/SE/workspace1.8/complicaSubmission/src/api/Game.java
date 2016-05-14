package api;

import exc.GameStateException;
import exc.IllegalMoveException;
import util.Chip;

import java.util.Observable;

public abstract class Game extends ObservableGame {
    public abstract int getRows();
    public abstract int getColumns();
    public abstract Chip[][] getBoard();

    /*
     * @throws GameStateException if current player is not set.
     * @throws IllegalMoveException if the move is out-of-bounds or is
     *         to a column that is full
     */
    public abstract void placeDisk(int col) throws GameStateException,
						   IllegalMoveException;
    
    /*
     * @throws GameStateException if no winner has been established.
     */
    public abstract Chip getWinningPlayer() throws GameStateException;
    
    public abstract Chip getCurrentPlayer();
    public abstract boolean isGameOver();
}
