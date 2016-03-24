package authentication;


import java.util.HashSet;
import java.util.Set;


public class Authenticator {
    // TODO: users' authentication should expire after some time of inactivity

    private Set<User> authenticatedUsers;

    public Authenticator() {
        this.authenticatedUsers = new HashSet<>();
    }

    /**
     * @param user
     * @return true if user is authenticated, false otherwise
     * @throws NullPointerException if user is null
     */
    public Boolean isAuthenticated(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be 'null'");
        }
        return authenticatedUsers.contains(user);
    }

    /**
     * Grants authentication to user
     *
     * @param user
     * @return true if user has been granted authentication, false otherwise
     * @throws NullPointerException if user is null
     */
    public Boolean addAuthenticated(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be 'null'");
        }
        this.authenticatedUsers.add(user);
        return true;
    }



}
