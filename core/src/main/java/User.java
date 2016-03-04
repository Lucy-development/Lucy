import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 18/02/2016.
 */
public class User {

    // TODO: is this really necessary? Should this info be here, in this class?
    // All created users' sessions are preserved here
    private static Map<Session, User> users = new HashMap<>();

    private String id;
    private String name;

    public User(String name, String id, Session session) {
        this.id = id;
        this.name = name;
        User.users.put(session, this);
    }

    public static User getById(String id) {
        for (User user : users.values()) {
            if (user.getID().equals(id)) {
                return user;
            }
        }
        throw new RuntimeException("User not found");
    }

    public static User getBySession(Session session) {
        return users.get(session);
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return id;
    }

}
