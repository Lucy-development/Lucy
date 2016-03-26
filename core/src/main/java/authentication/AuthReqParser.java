package authentication;

import com.google.gson.Gson;
import exceptions.NoAuthMethodException;
import exceptions.NoUidException;


public class AuthReqParser {

    /**
     * Returns the method of authentication as indicated by client's request
     *
     * @param authReq JSON string representing the auth request by client
     * @return String representation of the method used for authentication
     */
    public static String getAuthMethod(String authReq) {
        Gson gson = new Gson();
        String method = gson.fromJson(authReq, Req.class).authmethod;
        if (method == null) {
            throw new NoAuthMethodException();
        }
        return method;
    }


    /**
     * Returns user ID as indicated by client's request
     *
     * @param authReq JSON string representing the auth request by client
     * @return user ID
     */
    public static String getUid(String authReq) {
        Gson gson = new Gson();
        String uid = gson.fromJson(authReq, Req.class).userid;
        if (uid == null) {
            throw new NoUidException();
        }
        return uid;
    }

    private class Req {
        protected String authmethod;
        protected String userid;
    }
}
