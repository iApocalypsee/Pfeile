package general;

import java.io.Serializable;

/**
 * Represents the user that is playing the game on the machine.
 * @author Josip Palavra
 */
public class User implements Serializable {

    private final String username;

    public User(String username) {
        if(username == null) throw new NullPointerException();
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
