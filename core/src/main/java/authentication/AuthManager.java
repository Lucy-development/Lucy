package authentication;


import java.util.HashMap;
import java.util.Map;


public class AuthManager {
    // TODO: users' authentication should expire after some time of inactivity

    private final TokenGenerator tokenGenerator;
    private Map<String, User> authTokenUserMap;

    public AuthManager() {
        this.authTokenUserMap = new HashMap<>();
        this.tokenGenerator = new TokenGenerator();
    }

    /**
     * @param sessionToken
     * @return true if a user corresponding to this session token is authenticated, false otherwise
     * @throws NullPointerException if sessionToken is null
     */
    public Boolean isAuthenticated(String sessionToken) {
        if (sessionToken == null) {
            throw new NullPointerException("Session token cannot be 'null'");
        }
        return authTokenUserMap.containsKey(sessionToken);
    }

    /**
     * Grants authentication to user corresponding to this session token
     *
     * @param lid Lucy ID of user to be granted authorization
     * @return true if user has been granted authorization, false otherwise
     * @throws NullPointerException if lid is null
     */
    public String addAuthenticated(String lid) {
        if (lid == null) {
            throw new NullPointerException("LID cannot be 'null'");
        }
        String sessionToken = tokenGenerator.genSessToken();
        this.authTokenUserMap.put(sessionToken, new User(lid));
        return sessionToken;
    }

    public User userBySessionToken(String sessionToken) {
        return authTokenUserMap.get(sessionToken);
    }
}
