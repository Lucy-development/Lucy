package main;

import authentication.AuthManager;
import authentication.AuthReqParser;
import authentication.facebook.FBAuthenticator;
import communication.WSHandler;
import data.DatabaseManager;
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
        // Allow requests to /login and to / from any origin
        before("/login", (req, res) -> res.header("Access-Control-Allow-Origin", "*"));
        before("/", (req, res) -> res.header("Access-Control-Allow-Origin", "*"));

        post("/login", (req, res) -> {
            boolean authSuccessful = false;
            String lid = null;
            try {
                String authMethod = AuthReqParser.getAuthMethod(req.body());
                // LID := FB UID
                lid = AuthReqParser.getUid(req.body());
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
                String token = authManager.addAuthenticated(lid);
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
