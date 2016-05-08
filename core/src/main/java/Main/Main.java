package Main;

import authentication.AuthManager;
import authentication.AuthReqParser;
import authentication.facebook.FBAuthenticator;
import communication.WSHandler;
import data.DatabaseManager;
import data.Person;
import exceptions.NoAuthMethodException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static spark.Spark.*;


public class Main {

    private static final String FB_AUTH = "fb";
    public static AuthManager authManager;
    public static DatabaseManager dbManager;

    public static void main(String[] args) {
        Main.authManager = new AuthManager();

        try {
            Main.dbManager = new DatabaseManager();
        } catch (ClassNotFoundException | URISyntaxException | IOException | SQLException e) {
            throw new RuntimeException(e);
        }

        // Set Heroku port
        port(getHerokuAssignedPort());
        // Fetch static layout files
        staticFileLocation("public");
        // Set up a WebSocket to listen to /chat using WSHandler as a request handler
        webSocket("/chat", WSHandler.class);
        // Initialize WebSocket
        init();  // TODO: probably not needed since we're initializing more routes afterwards

        // TODO: should allow requests only from needed origins
        // TODO: should cache for less and add ETags
        before("/login", (req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
        });
        before("/", (req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
        });
        before("/*", (req, res) -> {
            res.header("Cache-Control", "max-age=604800"); // Cache for 1 week
        });

        post("/login", (req, res) -> {
            boolean authSuccessful = false;
            Person person = null;
            try {
                String authMethod = AuthReqParser.getAuthMethod(req.body());
                // LID := FB UID
                person = AuthReqParser.getUser(req.body());
                if (authMethod.equals(Main.FB_AUTH)) {
                    // Facebook login
                    System.err.println("Attempting FB authentication");
                    authSuccessful = FBAuthenticator.checkFBAuth(req.body());
                } else {
                    // Unknown login method
                    System.err.println("Unknown auth method sent by client");
                    authSuccessful = false;
                }
            } catch (NoAuthMethodException e) {
                // No login method specified by client
                System.err.println("No auth method specified by client");
                authSuccessful = false;
            }

            if (authSuccessful) {
                System.err.println("Auth successful");
                if (dbManager.getPersonByID(person.getID()) == null) {
                    dbManager.insertPersonIntoDb(person);
                }
                String token = authManager.addAuthenticated(person.getID());
                res.cookie("sessiontoken", token);
                System.err.println(token);
                res.status(200); // This should trigger a client-side redirect to / in browser
                res.redirect("/"); // Fallback if browser does not support AJAX, probably bullshit
            } else {
                System.err.println("Auth failed");
                // 403 FORBIDDEN
                res.status(403);
                halt(403);
            }
            return null;
        });
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

}
