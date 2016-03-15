package authentication;

import com.google.gson.Gson;
import exceptions.NoAuthMethodException;


public class AuthReqParser {

    /**
     * Returns the method of authentication as indicated by client's request
     *
     * @param authReq JSON string representing the auth request by client
     * @return String representation of the method used for authentication
     */
    public static String getAuthMethod(String authReq) {
        Gson gson = new Gson();
        String method = gson.fromJson(authReq, Req.class).authMethod;
        if (method == null) {
            throw new NoAuthMethodException();
        }
        return method;
    }

    private class Req {
        protected String authMethod;
    }
}
