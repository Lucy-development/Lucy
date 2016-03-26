package communication;

import authentication.User;
import m.Main;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private Map<Session, User> authSessionUserMap;
    private Map<User, Session> authUserSessionMap;

    public SessionManager() {
        this.authSessionUserMap = new HashMap<>();
        this.authUserSessionMap = new HashMap<>();
    }

    public boolean isAuthenticated(Session session) {
        return authSessionUserMap.containsKey(session);
    }

    public Boolean attemptSessionAuth(Session session, String sessionToken) {
        if (Main.authManager.isAuthenticated(sessionToken)) {
            User user = Main.authManager.userBySessionToken(sessionToken);
            this.authSessionUserMap.put(session, user);
            this.authUserSessionMap.put(user, session);
            return true;
        } else {
            System.err.println("Given session token has no authorization");
            return false;
        }
    }

    public Session sessionByLid(String lid) {
        for (User user : authUserSessionMap.keySet()) {
            if (user.getLid().equals(lid)) {
                return authUserSessionMap.get(user);
            }
        }
        return null;
    }

    public User userBySession(Session session) {
        return authSessionUserMap.get(session);
    }
}

