package authentication;


import java.util.HashSet;
import java.util.Set;


public class Authenticator {

    private Set<User> authenticatedUsers;

    public Authenticator() {
        this.authenticatedUsers = new HashSet<>();
    }

    /**
     * @param user
     * @return true if user is not null and is authenticated, false otherwise
     */
    public Boolean isAuthenticated(User user) {
        return user != null && authenticatedUsers.contains(user);
    }

    /**
     * Grants authentication to user
     *
     * @param user
     * @return true if user has been granted authentication, false otherwise
     */
    public Boolean addAuthenticated(User user) {
        this.authenticatedUsers.add(user);
        return true;
    }



}
