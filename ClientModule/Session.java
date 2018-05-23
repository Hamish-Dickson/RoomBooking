package ClientModule;

/**
 * Class that holds the current user's username and if they are an admin
 *
 * @author Hamish Dickson
 */
public class Session {
    private final String username;
    private final boolean admin;

    /**
     * Creates a new session
     *
     * @param username the user's username
     * @param admin    the user's admin status.
     */
    public Session(String username, boolean admin) {
        this.username = username;
        this.admin = admin;
    }

    /**
     * @return The username of the current user
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return True if the current user is an admin, else false
     */
    boolean isAdmin() {
        return admin;
    }
}
