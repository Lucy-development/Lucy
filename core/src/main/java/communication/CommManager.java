package communication;

import data.SentMessage;
import m.Main;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;

public class CommManager {

    public static boolean sendMessage(String senderLid,
                                      String receiverLid,
                                      Session receiverSession,
                                      String message,
                                      boolean onConnect)
            throws JSONException, IOException {

        // Write msg to database
        if (!onConnect) {
            Main.dbManager.insertSentMessageIntoDb(
                    new SentMessage(
                            new Timestamp(System.currentTimeMillis()),
                            Integer.parseInt(senderLid),
                            Integer.parseInt(receiverLid),
                            message)
            );
        }

        JSONObject messageObject = new JSONObject();
        messageObject.put("purpose", "msg_received");
        messageObject.put("status", "success");
        messageObject.put("from", senderLid);
        messageObject.put("content", message);
        return send(messageObject, receiverSession);
    }

    public static boolean sendResponse(ServerResponse responseType, Session session) throws JSONException, IOException {
        String SUCCESS = "success";
        String FAIL = "fail";
        String purpose = null;
        String status = null;
        switch (responseType) {
            case messageDelivered:
                purpose = "msg_sent";
                status = SUCCESS;
                break;
            case messageDeliveryFailed:
                purpose = "msg_sent";
                status = FAIL;
                break;
            case authSuccessful:
                purpose = "auth_resp";
                status = SUCCESS;
                break;
            case authFailed:
                purpose = "auth_resp";
                status = FAIL;
                break;
            case notAuthorized:
                purpose = "auth_resp";
                status = FAIL;
                break;
            case alreadyAuthenticated:
                purpose = "auth_resp";
                status = SUCCESS;
                break;
            case unknownPurpose:
                purpose = "unk";
                status = FAIL;
                break;
            default:
                throw new AssertionError(String.format("Unknown ServerResponse: '%s'", responseType));
        }
        JSONObject response = new JSONObject();
        response.put("purpose", purpose);
        response.put("status", status);
        return send(response, session);
    }

    private static boolean send(JSONObject JSONString, Session session) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(
                    String.valueOf(JSONString));
            return true;
        } else {
            return false;
        }
    }


}
