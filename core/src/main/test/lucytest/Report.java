package lucytest;

import pdfcreator.WritableToXML;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for holding temporally Lucy test results.
 */
public class Report implements WritableToXML {
    public static final String PASSED = "PASS";
    public static final String FAILED = "FAIL";

    // Send message with LID (this checks retrieval also)
    private static final String TEST_SEND_LID = "L1";
    // Check that message was inserted as a sent message into the message box
    private static final String TEST_SENT_MESSAGE_WAS_INSERTED = "L2";
    // Check that message was sent
    private static final String TEST_SENT_MESSAGE_WAS_ACTUALLY_SENT = "L3";
    // Check that location data exists
    private static final String TEST_LOCATION = "L4";
    // Check contact appearance
    private static final String TEST_CONTACT_APPEARANCE = "L5";
    // Check that sending message to a contact via contact list is working (this checks retrieval also)
    private static final String TEST_SEND_LIST = "L6";
    // Check theme
    private static final String TEST_THEME = "L7";
    // Check history
    private static final String TEST_HISTORY = "L8";

    private String sendLid = FAILED;
    private String msgWasInserted = FAILED;
    private String msgWasSent = FAILED;
    private String location = FAILED;
    private String contactAppearance = FAILED;
    private String contactLst = FAILED;
    private String testTheme = FAILED;
    private String testHistory = FAILED;

    /**
     * Send message with LID (this checks retrieval also)
     */
    public void setSendLid(String sendLid) {
        this.sendLid = sendLid;
    }

    /**
     * Check that message was inserted as a sent message into the message box
     */
    public void setMsgWasInserted(String msgWasInserted) {
        this.msgWasInserted = msgWasInserted;
    }

    /**
     * Check that message was sent
     */
    public void setMsgWasSent(String msgWasSent) {
        this.msgWasSent = msgWasSent;
    }

    /**
     * Check that location data exists
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Check contact appearance
     */
    public void setContactAppearance(String contactAppearance) {
        this.contactAppearance = contactAppearance;
    }

    /**
     * Check that sending message to a contact via contact list is working (this checks retrieval also)
     */
    public void setContactLst(String contactLst) {
        this.contactLst = contactLst;
    }

    /**
     * Check theme
     */
    public void setTestTheme(String testTheme) {
        this.testTheme = testTheme;
    }

    /**
     * Check history
     */
    public void setTestHistory(String testHistory) {
        this.testHistory = testHistory;
    }

    @Override
    public Map<String, String> getContentForXML() {
        Map<String, String> results = new HashMap<>();
        results.put(TEST_SEND_LID, sendLid);
        results.put(TEST_SENT_MESSAGE_WAS_INSERTED, msgWasInserted);
        results.put(TEST_SENT_MESSAGE_WAS_ACTUALLY_SENT, msgWasSent);
        results.put(TEST_LOCATION, location);
        results.put(TEST_CONTACT_APPEARANCE, contactAppearance);
        results.put(TEST_SEND_LIST, contactLst);
        results.put(TEST_THEME, testTheme);
        results.put(TEST_HISTORY, testHistory);
        return results;
    }
}
