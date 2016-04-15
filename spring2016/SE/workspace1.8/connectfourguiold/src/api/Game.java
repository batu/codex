package api;

import exc.GameStateException;
import exc.IllegalMoveException;
import javafx.scene.shape.Circle;
import util.Chip;

import java.util.Observable;

public abstract class Game extends ObservableGame {
    private int rows;
    private int columns;
    protected Chip[][] surface;

    public Game(int rows, int columns) {
	this.rows = rows;
	this.columns = columns;
    }
    
    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Chip[][] getBoard() {
        return surface;
    }

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
