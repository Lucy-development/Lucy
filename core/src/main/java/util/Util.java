package util;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class Util {

    public Util() {
        throw new RuntimeException("This is a static class!");
    }

    public static Map<String, String> parseFBAuthJSONQuery(String query) {
        Map<String, String> queryParams = new HashMap<>();
        Gson gson = new Gson();
        FBAuthQuery q = gson.fromJson(query, FBAuthQuery.class);
        queryParams.put("userid", q.userid);
        queryParams.put("accesstoken", q.accesstoken);
        return queryParams;
    }

    private class FBAuthQuery {
        protected String userid;
        protected String accesstoken;
    }
}
