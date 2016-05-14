package test.unit.jsw7;

import api.Game;
import impl.ComplicaGame;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ComplicaGameTest {
    @Test
    public void testInitSetsPlayer() {
        Game game = new ComplicaGame();
        assertNotNull(game);
    }
}
