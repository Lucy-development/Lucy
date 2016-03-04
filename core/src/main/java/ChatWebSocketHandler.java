import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created on 18/02/2016.
 */
@WebSocket
public class ChatWebSocketHandler {
    private String lineSeparator = System.getProperty("line.separator");

    // ID counter
    private int i = 0;

    @OnWebSocketConnect
    public void onConnect(Session userSession) throws IOException, JSONException {
        // TODO: Design a system to manage these first-time connections
        // TODO: The client should send its unique ID to the server -> server sends back a list of contact IDs
        // TODO: Should first build database mocks to simulate fetching contact info

        // This is a temporary naive system for testing
        Main.sessionMap.put(
                // TODO: how to get username from front end? Should probably encode it into JSON.
                new User("Generic username", String.valueOf(i), userSession),
                userSession
        );

        Main.sendMessage(userSession, i + "; You have sent today " + Main.getDb().getSentMessageCountToday(i) + " messages." +
                lineSeparator + "Logged in with ID: " + i +
                lineSeparator + getLastReceivedMessage(i));
        i++;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, JSONException {
        Main.sendMessage(session, message);
    }

    /**
     * Method returns last send message
     */
    private StringBuilder getLastReceivedMessage(Integer messageSenderID) {
        StringBuilder lastReceivedMessage = new StringBuilder();

        // Lambda is for case where retrievedMessage list is empty, then nothing is added to the StringBuilder.
        // Otherwise person firstname + content is added to the StringBuilder
        Main.getDb().retrieveMessagesByRecipient(messageSenderID, 1).forEach(msg ->
                lastReceivedMessage.append
                        (Main.getDb().getPersonByID(msg.getSender()).getFirstName()).append(": ").append(msg.getContent()));
        return lastReceivedMessage;
    }


}
