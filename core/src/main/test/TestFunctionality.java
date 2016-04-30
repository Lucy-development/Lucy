import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import email.MailClient;
import lucytest.Report;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import pdfcreator.CreatePDF;

import static lucytest.Report.FAILED;
import static lucytest.Report.PASSED;
import static org.junit.Assert.fail;

@Ignore
public class TestFunctionality {

    private static final String usr = "tmp0602161@gmail.com";
    private static final String psw = "pir8man";

    private Selenium selenium;
    private WebDriver driver;
    private Report report;

    @Before
    public void setUp() throws Exception {
        report = new Report();
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("geo.prompt.testing", true);
        profile.setPreference("geo.prompt.testing.allow", true);
        driver = new FirefoxDriver(profile);
        String baseUrl = "http://lucy-messaging.herokuapp.com/";
        selenium = new WebDriverBackedSelenium(driver, baseUrl);
    }

    @Test
    public void testLucy() throws Exception {
        selenium.open("/");
        // Login
        selenium.click("id=login");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=login");
        // Wait for popup
        selenium.waitForPageToLoad("30000");
        logIntoFb();
        // Wait for Lucy main page to reload
        selenium.waitForPageToLoad("30000");


        for (int second = 0; second <= 60; second++) {
            try {
                if ("WebSocket session opened".equals(selenium.getText("class=logmessage"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.click("id=messagebox");
        // Test that we can send a message with LID
        selenium.type("id=searchcontact", "test");
        selenium.type("id=message", "qmwntzucpzalskqqqqqoooo11fwejnhbfibhwpkgfvwhuygwey");
        selenium.click("id=send");

        // Check that message was inserted as a sent message into the message box
        report.setMsgWasInserted(selenium.getText("class=outgoing").matches("^[\\s\\S]*qmwntzucpzalskqqqqqoooo11fwejnhbfibhwpkgfvwhuygwey[\\s\\S]*$") ? PASSED : FAILED);


        // Wait for server to respond - if server responds then message was successfully sent
        for (int second = 0; second <= 60; second++) {
            try {
                if ("Ivan Orav: This is a test message.".equals(selenium.getText("class=incoming"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        // Test message recieval - server must send a message as a mock user with my location
        report.setSendLid(selenium.getText("class=incoming").equals("Ivan Orav: This is a test message.") ? PASSED : FAILED);
        // As server responded, a contact name "Ivan Orav" must have been appeared
        report.setContactAppearance(selenium.isElementPresent("id=TEST_LID") ? PASSED : FAILED);
        report.setMsgWasSent(selenium.isElementPresent("id=TEST_LID") ? PASSED : FAILED);
        // Check that location contains word Tartu - if tester is not in Tartu then one must change Tartu to another location
        report.setLocation(selenium.getText("class=message_location").matches("^[\\s\\S]*Tartu[\\s\\S]*$") ? PASSED : FAILED);


        // Test message history
        selenium.open("/");
        selenium.waitForPageToLoad("30000");
        selenium.click("id=history");

        for (int second = 0; second <= 60; second++) {
            try {
                if ("TEST_LID: This is a test message.".equals(selenium.getText("class=incoming"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }


        report.setTestHistory(selenium.isElementPresent("class=incoming") ? PASSED : FAILED);

        // Test that we can send to contact by choosing it from the list. (Sending to myself: change ID when running on another account)
        selenium.type("id=searchcontact", "155323141522693");
        selenium.type("id=message", "Hei!");
        selenium.click("id=send");
        selenium.type("id=searchcontact", "");


        for (int second = 0; second <= 60; second++) {
            try {
                if ("Semir Juurikas".equals(selenium.getText("id=155323141522693"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.click("id=155323141522693");
        selenium.type("id=message", "Saadan iseendale.");
        // Check theme changing
        selenium.click("id=menu-button");
        selenium.click("id=darcula");
        report.setTestTheme(selenium.isElementPresent("class=messagebox_darcula") ? PASSED : FAILED);


        // Check if message "Failed to Deliver has appeared"
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("Semir Juurikas: Hei!".equals(selenium.getText("id=msg15"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        report.setContactLst(selenium.getText("class=logmessage").equals("Failed to deliver message") ? FAILED : PASSED);
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
        new MailClient().email("You may find Lucy test results in the attachment. ",new CreatePDF().createResultsPDF(report));
        //new CreatePDF().createResultsPDF(report,"C:\\Users\\Priit Paluoja\\Desktop\\test.pdf");
    }

    /**
     * Method Handles FB popup login
     */
    private void logIntoFb() {
        String parentWindowHandler = driver.getWindowHandle(); // Store your parent window
        String subWindowHandler = null;
        // Iterate over all window handles
        for (String handle : driver.getWindowHandles()) {
            subWindowHandler = handle;
        }
        driver.switchTo().window(subWindowHandler); // switch to popup window
        // perform operations on popup
        selenium.type("id=email", usr);
        selenium.type("id=pass", psw);
        selenium.click("id=u_0_2");
        driver.switchTo().window(parentWindowHandler);  // switch back to parent
    }
}