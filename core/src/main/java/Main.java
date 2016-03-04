import authentication.facebook.FBValidation;
import data.DatabaseManager;
import data.SentMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;
import util.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;


public class Main {
    static Map<User, Session> sessionMap = new HashMap<>();
    private static DatabaseManager db;

    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        // Do not comment it out without changing ChatWebSocketHandler!
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
            System.err.println("test");
            return "";
        });


        post("/login", (req, res) -> {

            Map<String, String> params = Util.parseQueryString(req.body());

            if (!params.containsKey("accesstoken") || !params.containsKey("userid")) {
                throw new RuntimeException("Invalid query params: " + params);
            }

            String userID = params.get("userid");
            String accessToken = params.get("accesstoken");

            boolean authSuccessful = FBValidation.checkFBTokenValidity(userID, accessToken);

            if (authSuccessful) {
                System.out.println("Authentication successful for UID " + userID);
                // TODO: somehow assign userID to session?
                // TODO: redirect to /chat
            } else {
                // TODO: generate res & return
            }
            return null;
        });

    }




    // TODO: this method should definitely not be in Main
    public static void sendMessage(Session senderSession, String message, boolean onConnect) throws JSONException, IOException {
        // TODO: handle exceptions appropriately

        // TODO: figure out how to simplify this; we shouldn't need so many duplicating data structures
        // Extract receiver, message and sender info
        User receiver = User.getById(message.split(";")[0]);
        Session receiverSession = sessionMap.get(receiver);
        String msg = message.split(";")[1];
        User sender = User.getBySession(senderSession);

        // Write msg to database
        if (!onConnect)
            getDb().insertSentMessageIntoDb(new SentMessage(new Timestamp(System.currentTimeMillis()), Integer.parseInt(sender.getID()), Integer.parseInt(receiver.getID()), msg));

        // Send this info as JSON
        // TODO: should change this to XML or something to get points?
        if (receiverSession.isOpen()) {
            receiverSession.getRemote().sendString(String.valueOf(
                    new JSONObject()
                            .put("from", sender.getID())
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
