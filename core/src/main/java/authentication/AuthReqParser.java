package authentication;

import com.google.gson.Gson;
import data.Person;
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
        String method = gson.fromJson(authReq, Req.class).authmethod;
        if (method == null) {
            throw new NoAuthMethodException();
        }
        return method;
    }


    public static Person getUser(String authReq) {
        Gson gson = new Gson();
        Req r = gson.fromJson(authReq, Req.class);
        return new Person(
                r.userid,
                r.myname,
                "",
                null,
                null,
                null,
                null
        );
    }

    private class Req {
        protected String authmethod;
        protected String userid;
        protected String myname;
    }
}
