package communication;

import Main.Main;
import data.Person;
import data.SentMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class CommManager {

    public static boolean sendMessage(String senderLid,
                                      String senderFname,
                                      String senderLname,
                                      String receiverLid,
                                      Session receiverSession,
                                      String message, String longitude, String latitude, String location)
            throws JSONException, IOException {

        // Write msg to database
        Main.dbManager.insertSentMessageIntoDb(
                new SentMessage(String.valueOf(System.nanoTime()),
                        new Timestamp(System.currentTimeMillis()),
                        senderLid,
                        receiverLid,
                        message, longitude, latitude, location)
        );

        JSONObject messageObject = new JSONObject();
        messageObject.put("purpose", "msg_received");
        messageObject.put("status", "success");
        messageObject.put("from", senderLid);
        messageObject.put("sender_fname", senderFname);
        messageObject.put("sender_lname", senderLname);
        messageObject.put("content", message);
        messageObject.put("longitude", longitude);
        messageObject.put("latitude", latitude);
        messageObject.put("location", location);
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


    public static boolean sendContacts(String lid, Session session) throws JSONException, IOException {
        JSONObject resp = new JSONObject();
        resp.put("purpose", "init_contacts");
        resp.put("status", "success");
        JSONArray jsonContactArray = new JSONArray();

        List<Person> contacts = Main.dbManager.getFriends(lid);
        for (Person contact : contacts) {
            JSONObject jsonContact = new JSONObject();
            jsonContact.put("fname", contact.getFirstName());
            jsonContact.put("lname", contact.getLastName());
            jsonContact.put("lid", contact.getID());
            jsonContactArray.put(jsonContact);
        }

        resp.put("contacts", jsonContactArray);
        return send(resp, session);
    }

    public static boolean sendHistory(int messagesReadInSession, String requesterId, Session requesterSession) throws JSONException, IOException {
        int messagesToSend = 10;
        List<SentMessage> messages = Main.dbManager.retrieveMessagesInRange(requesterId,
                messagesReadInSession,
                messagesReadInSession + messagesToSend);

        JSONObject resp = new JSONObject();
        resp.put("purpose", "hist_response");
        resp.put("status", "success");
        JSONArray jsonMessageArray = new JSONArray();

        for(SentMessage msg : messages){
            JSONObject m = new JSONObject();
            m.put("sender", msg.getSender());
            m.put("receiver", msg.getReceiver());
            m.put("content", msg.getContent());
            jsonMessageArray.put(m);
        }

        resp.put("messages", jsonMessageArray);
        return send(resp, requesterSession);
    }
}
