package communication;

import com.google.gson.Gson;

public class ClientMessageParser {

    public static JSONMessage parseMessage(String JSONString) {
        return new Gson().fromJson(JSONString, JSONMessage.class);
    }

    public class JSONMessage {
        public String purpose;
        public String to;
        public String content;

        @Override
        public String toString() {
            return "JSONMessage{" +
                    "purpose='" + purpose + '\'' +
                    ", to='" + to + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

}
