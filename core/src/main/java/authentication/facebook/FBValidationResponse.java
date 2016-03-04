package authentication.facebook;


import com.google.gson.Gson;

import java.util.List;

public class FBValidationResponse {

    private jsonData data;

    public static FBValidationResponse getInstance(String response) {
        Gson gson = new Gson();
        return gson.fromJson(response, FBValidationResponse.class);
    }

    private FBValidationResponse() {
    }

    public String userID() {
        return this.data.user_id;
    }


    // Inner class for GSON reflection
    private class jsonData {
        String app_id;
        String application;
        String expires_at;
        String is_valid;
        List<String> scopes;
        String user_id;
    }
}
