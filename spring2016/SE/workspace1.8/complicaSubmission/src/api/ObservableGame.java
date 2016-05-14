package api;

import java.lang.UnsupportedOperationException;
import java.util.Observable;
import javafx.scene.paint.Paint;
import javafx.beans.property.ObjectProperty;

public class ObservableGame extends Observable {
    /*
     * Bind a color property to the winning player. For the current
     * player, this can be done directly (by getting a reference from
     * the method); however, with the winning player such a reference
     * is not gauranteed to be returned: if there is no winner, that
     * method throws an exception. For clients wishing to bind to the
     * winner, however, that distinction should not be made.
     */
    public void bindToWinningPlayer(ObjectProperty<Paint> property) {
	throw new java.lang.UnsupportedOperationException();
    }
}
