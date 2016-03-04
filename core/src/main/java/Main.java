import data.DatabaseManager;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created on 15/02/2016.
 */
public class Main {
    static Map<User, Session> sessionMap = new HashMap<>();
    private static DatabaseManager db;

    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        try {
            db = new DatabaseManager();
        } catch (ClassNotFoundException | URISyntaxException | IOException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        // Fetch static layout files
        staticFileLocation("public");
        // Set up a WebSocket to listen to /chat using ChatWebSocketHandler as a request handler
        webSocket("/chat", ChatWebSocketHandler.class);
        // Initialize WebSocket
        init();

        // Add additional routes
        get("/hello", (req, res) -> "Lucy");


        // LOGIN
        get("/login", (req, res) -> {
            // TODO: return login screen
            return "";
        });


        post("/login", (req, res) -> {
            boolean authSuccessful = true;
            if(authSuccessful) {
                res.redirect("/chat");
            } else {
                // TODO: generate res & return
            }
            return null;
        });

    }


    // TODO: this method should definitely not be in Main
    public static void sendMessage(Session senderSession, String message) throws JSONException, IOException {
        // TODO: handle exceptions appropriately

        // TODO: figure out how to simplify this; we shouldn't need so many duplicating data structures
        // Extract receiver, message and sender info
        User receiver = User.getById(message.split(";")[0]);
        Session receiverSession = sessionMap.get(receiver);
        String msg = message.split(";")[1];
        User sender = User.getBySession(senderSession);

        // Send this info as JSON
        // TODO: should change this to XML or something to get points?
        if (receiverSession.isOpen()) {
            receiverSession.getRemote().sendString(String.valueOf(
                    new JSONObject()
                            .put("from", sender.getId())
                            .put("msg", msg)
            ));
        } else {
            // TODO: notify sender that the receiver is not available (somehow)
        }
    }


    /**
     * TODO: add description
     *
     * @return
     */
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

    public static DatabaseManager getDb() {
        return db;
    }
}
