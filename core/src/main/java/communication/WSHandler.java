package communication;

import Main.Main;
import data.Person;
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
    private final String PURPOSE_HISTORY = "hist";
    private final String TEST = "fgjuopljgf6548345";

    private SessionManager sessionManager = new SessionManager();

    @OnWebSocketConnect
    public void onConnect(Session userSession) throws IOException, JSONException {
    }

    @OnWebSocketMessage
    public void onMessage(Session senderSession, String encodedMessage) throws IOException, JSONException {
        ClientMessageParser.JSONMessage message = ClientMessageParser.parseMessage(encodedMessage);
        switch (message.purpose) {
            case PURPOSE_MSG:
                // Client requests to send a message
                if (sessionManager.isAuthenticated(senderSession)) {
                    // Client session is authenticated
                    String receiverLid = message.to;
                    Session receiverSession = sessionManager.sessionByLid(receiverLid);
                    if (receiverSession == null) {
                        CommManager.sendResponse(ServerResponse.messageDeliveryFailed, senderSession);
                    } else {
                        String senderLid = sessionManager.userBySession(senderSession).getLid();
                        Person sender = Main.dbManager.getPersonByID(senderLid);
                        boolean messageSent = CommManager.sendMessage(
                                sessionManager.userBySession(senderSession).getLid(),
                                sender.getFirstName(),
                                sender.getLastName(),
                                sessionManager.userBySession(receiverSession).getLid(),
                                receiverSession,
                                message.content,
                                message.longitude,
                                message.latitude,
                                message.location
                        );

                        if (Main.dbManager.getPersonByID(receiverLid) != null &&
                                !Main.dbManager.getContactFriends(receiverLid).contains(senderLid)) {
                            Main.dbManager.insertFriendIntoDb(receiverLid, senderLid);
                        }
                        if (messageSent) {
                            CommManager.sendResponse(ServerResponse.messageDelivered, senderSession);
                        } else {
                            CommManager.sendResponse(ServerResponse.messageDeliveryFailed, senderSession);
                        }
                    }
                } else {
                    // Client session is NOT authenticated
                    System.err.println(String.format("Unauthorized client attempted to send message: %s", message));
                    CommManager.sendResponse(ServerResponse.notAuthorized, senderSession);
                }

                break;
            case PURPOSE_AUTH:
                // Client requests to authenticate the session
                if (sessionManager.isAuthenticated(senderSession)) {
                    // Client session is already authenticated
                    System.err.println("Already authenticated client attempted to re-authenticate session");
                    CommManager.sendResponse(ServerResponse.alreadyAuthenticated, senderSession);
                } else {
                    // Client session is NOT authenticated
                    String sessionToken = message.content;
                    boolean authSuccessful = sessionManager.attemptSessionAuth(senderSession, sessionToken);
                    if (authSuccessful) {
                        System.err.println("Client successfully authenticated session");
                        CommManager.sendResponse(ServerResponse.authSuccessful, senderSession);
                        CommManager.sendContacts(sessionManager.userBySession(senderSession).getLid(), senderSession);
                    } else {
                        System.err.println(String.format("Client's attempt to authenticate the session failed: %s", message));
                        CommManager.sendResponse(ServerResponse.authFailed, senderSession);
                        senderSession.close(new CloseStatus(1008, "Session authentication failed"));
                    }
                }

                break;
            case PURPOSE_HISTORY:
                // Client requests message history
                if (sessionManager.isAuthenticated(senderSession)) {
                    int messagesReadInSession = Integer.parseInt(message.content);
                    CommManager.sendHistory(
                            messagesReadInSession,
                            sessionManager.userBySession(senderSession).getLid(),
                            senderSession
                    );


                } else {
                    System.err.println(String.format("Unauthorized client attempted to fetch history: %s", message));
                    CommManager.sendResponse(ServerResponse.notAuthorized, senderSession);
                }

                break;

            // Send mock message to sender for testing purposes.
            case TEST:
                if (sessionManager.isAuthenticated(senderSession)) {
                    boolean messageSent = CommManager.sendMessage(
                            "TEST_LID",
                            "Ivan",
                            "Orav",
                            sessionManager.userBySession(senderSession).getLid(),
                            senderSession,
                            "This is a test message.",
                            message.longitude,
                            message.latitude,
                            message.location
                    );

                    if (messageSent) {
                        CommManager.sendResponse(ServerResponse.messageDelivered, senderSession);
                    } else {
                        CommManager.sendResponse(ServerResponse.messageDeliveryFailed, senderSession);
                    }

                } else {
                    // Client session is NOT authenticated
                    System.err.println(String.format("Unauthorized client attempted to send message: %s", message));
                    CommManager.sendResponse(ServerResponse.notAuthorized, senderSession);
                }
                break;

            default:
                System.err.println(String.format("Received message with unknown purpose from client: %s", message));
                CommManager.sendResponse(ServerResponse.unknownPurpose, senderSession);
                break;
        }
    }
}

