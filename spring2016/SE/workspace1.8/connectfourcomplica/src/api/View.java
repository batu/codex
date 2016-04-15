package api;

import java.util.Observer;

public abstract class View implements Observer {
    public abstract void render(Game game);
}
