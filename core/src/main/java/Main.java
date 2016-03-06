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
        try {
            Main.db = new DatabaseManager();
        } catch (ClassNotFoundException | URISyntaxException | IOException | SQLException e) {
            throw new RuntimeException(e);
        }

        // Set Heroku port
        port(getHerokuAssignedPort());
        // Fetch static layout files
        staticFileLocation("public");
        // Set up a WebSocket to listen to /chat using ChatWebSocketHandler as a request handler
        webSocket("/chat", ChatWebSocketHandler.class);
        // Initialize WebSocket
        init();  // TODO: probably not needed since we're initializing more routes afterwards

        // TODO: should allow requests only from needed origins
        // Allow requests to /login and to / from any origin
        before("/login", (req, res) -> res.header("Access-Control-Allow-Origin", "*"));
        before("/", (req, res) -> res.header("Access-Control-Allow-Origin", "*"));

        post("/login", (req, res) -> {
            // Get userID and FB access token from request body
            Map<String, String> params = Util.parseFBAuthJSONQuery(req.body());
            if (!params.containsKey("accesstoken") || !params.containsKey("userid")) {
                throw new RuntimeException("Invalid query params: " + params);
            }
            String userID = params.get("userid");
            String accessToken = params.get("accesstoken");

            // Validate that given FB access token matches given userID
            boolean authSuccessful = FBValidation.checkFBTokenValidity(userID, accessToken);
            if (authSuccessful) {
                System.out.println("FB authentication successful for userID " + userID);
                res.status(200); // This should trigger a redirect to / in browser
                res.redirect("/"); // Fallback if browser does not support AJAX
                halt();
                // TODO: somehow assign userID to session?
            } else {
                // 403 FORBIDDEN
                res.status(403);
                halt(403);
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
