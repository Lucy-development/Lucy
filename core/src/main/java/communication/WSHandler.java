package communication;

import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;

import java.io.IOException;


@WebSocket
public class WSHandler {

    private final String PURPOSE_AUTH = "auth";
    private final String PURPOSE_MSG = "msg";

    private SessionManager sessionManager = new SessionManager();

    @OnWebSocketConnect
    public void onConnect(Session userSession) throws IOException, JSONException {
    }

    @OnWebSocketMessage
    public void onMessage(Session userSession, String encodedMessage) throws IOException, JSONException {
        ClientMessageParser.JSONMessage message = ClientMessageParser.parseMessage(encodedMessage);
        switch (message.purpose) {
            case PURPOSE_MSG:
                // Client requests to send a message
                if (sessionManager.isAuthenticated(userSession)) {
                    // Client session is authenticated
                    Session receiverSession = sessionManager.sessionByLid(message.to);
                    if (receiverSession == null) {
                        CommManager.sendResponse(ServerResponse.messageDeliveryFailed, userSession);
                    } else {
                        boolean messageSent = CommManager.sendMessage(
                                sessionManager.userBySession(userSession).getLid(),
                                sessionManager.userBySession(receiverSession).getLid(),
                                receiverSession,
                                message.content,
                                false);
                        if (messageSent) {
                            CommManager.sendResponse(ServerResponse.messageDelivered, userSession);
                        } else {
                            CommManager.sendResponse(ServerResponse.messageDeliveryFailed, userSession);
                        }
                    }
                } else {
                    // Client session is NOT authenticated
                    System.err.println(String.format("Unauthorized client attempted to send message: %s", message));
                    CommManager.sendResponse(ServerResponse.notAuthorized, userSession);
                }

                break;
            case PURPOSE_AUTH:
                // Client requests to authenticate the session
                if (sessionManager.isAuthenticated(userSession)) {
                    // Client session is already authenticated
                    System.err.println("Already authenticated client attempted to re-authenticate session");
                    CommManager.sendResponse(ServerResponse.alreadyAuthenticated, userSession);
                } else {
                    // Client session is NOT authenticated
                    String sessionToken = message.content;
                    boolean authSuccessful = sessionManager.attemptSessionAuth(userSession, sessionToken);
                    if (authSuccessful) {
                        System.err.println("Client successfully authenticated session");
                        CommManager.sendResponse(ServerResponse.authSuccessful, userSession);
                    } else {
                        System.err.println(String.format("Client's attempt to authenticate the session failed: %s", message));
                        CommManager.sendResponse(ServerResponse.authFailed, userSession);
                        userSession.close(new CloseStatus(1008, "Session authentication failed"));
                    }
                }

                break;
            default:
                System.err.println(String.format("Received message with unknown purpose from client: %s", message));
                CommManager.sendResponse(ServerResponse.unknownPurpose, userSession);
                break;
        }
    }
}

