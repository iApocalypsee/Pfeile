package general;

import world.WorldViewport;

import java.io.Serializable;

/**
 * Represents the user that is playing the game on the machine. Stores the Viewport for the game.
 * @author Josip Palavra
 */
public class User implements Serializable {

    private final String username;
    private final WorldViewport viewport;

    public User(String username) {
        if(username == null) throw new NullPointerException();
        this.username = username;
        this.viewport = new WorldViewport();
    }

    public WorldViewport getViewport() {
        return viewport;
    }

    public String getUsername() {
        return username;
    }
}
