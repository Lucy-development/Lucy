package authentication.facebook;

import org.apache.http.client.fluent.Request;
import util.Util;

import java.io.IOException;
import java.util.Map;


public class FBAuthenticator {

    /**
     * Checks if client's FB authentication request is legitimate and valid
     *
     * @param authRequest JSON string representing the auth request by client
     * @return true if request is legitimate and valid, false otherwise
     */
    public static Boolean checkFBAuth(String authRequest) {
        // Get userID and FB access token from request body
        Map<String, String> params = Util.parseFBAuthJSONQuery(authRequest);
        if (!params.containsKey("accesstoken") || !params.containsKey("userid")) {
            System.err.println("Invalid query params: " + params);
            return false;
        }
        String userID = params.get("userid");
        String accessToken = params.get("accesstoken");
        return FBAuthenticator.checkFBTokenValidity(userID, accessToken);
    }

    private static Boolean checkFBTokenValidity(String userID, String userAccessToken) {
        // Correct env vars must be set on local machine if running server locally
        String appID = System.getenv("FACEBOOK_APP_ID");
        String appSecret = System.getenv("FACEBOOK_SECRET");
        System.err.println(String.format("Checking FB user access token validity using appID '%s' and appSecret '%s'", appID, appSecret));

        try {
            String response = Request.Get("https://graph.facebook.com/debug_token" +
                            "?input_token=" + userAccessToken +
                            "&access_token=" + appID + "%7C" + appSecret
                    ).execute().returnContent().asString();
            if (userID.equals(FBValidationResponse.getInstance(response).userID())) {
                System.err.println("Facebook user access token validation successful.");
                return true;
            } else {
                System.err.println("Facebook user access token validation failed!");
                System.err.println("Validation response was " + response);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
