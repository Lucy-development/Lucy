package util;

import exceptions.MalformedQueryStringException;

import java.util.HashMap;
import java.util.Map;


public class Util {

    public Util() {
        throw new RuntimeException("This is a static class!");
    }

    public static Map<String, String> parseQueryString(String query) {
        Map<String, String> queryParams = new HashMap<>();

        for (String param : query.split("&")) {
            try {
                String key = param.split("=")[0];
                String val = param.split("=")[1];
                queryParams.put(key, val);
            } catch (IndexOutOfBoundsException e) {
                throw new MalformedQueryStringException("Malformed query string: " + query);
            }
        }

        return queryParams;
    }

}
