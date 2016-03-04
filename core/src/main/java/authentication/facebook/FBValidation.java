package authentication.facebook;

import org.apache.http.client.fluent.Request;

import java.io.IOException;


public class FBValidation {

    public static boolean checkFBTokenValidity(String userID, String userAccessToken) throws IOException {

        String appID = "555058557990846";
        String appSecret = "ed76d940ed103d5506e69db62ed24871";

        String response =
                Request.Get("https://graph.facebook.com/debug_token" +
                        "?input_token=" + userAccessToken +
                        "&access_token=" + appID + "%7C" + appSecret
                ).execute().returnContent().asString();

        System.out.println(response);

        if (userID.equals(FBValidationResponse.getInstance(response).userID())) {
            System.out.println("Accesstoken validation successful");
            return true;
        } else {
            System.out.println("Accesstoken validation failed");
            return false;
        }
    }
}
