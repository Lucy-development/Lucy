package authentication.facebook;

import org.apache.http.client.fluent.Request;

import java.io.IOException;


public class FBValidation {

    public static boolean checkFBTokenValidity(String userID, String userAccessToken) throws IOException {

        // Correct env vars must be set on local machine if running server locally
        String appID = System.getenv("FACEBOOK_APP_ID");
        String appSecret = System.getenv("FACEBOOK_SECRET");
        System.out.println(String.format("Checking FB user access token validity using appID '%s' and appSecret '%s'", appID, appSecret));

        String response =
                Request.Get("https://graph.facebook.com/debug_token" +
                        "?input_token=" + userAccessToken +
                        "&access_token=" + appID + "%7C" + appSecret
                ).execute().returnContent().asString();

        if (userID.equals(FBValidationResponse.getInstance(response).userID())) {
            System.out.println("Facebook user access token validation successful.");
            return true;
        } else {
            System.out.println("Facebook user access token validation failed!");
            System.out.println("Validation response was " + response);
            return false;
        }
    }
}
