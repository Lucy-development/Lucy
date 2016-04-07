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
        public String latitude;
        public String longitude;
        public String location;

        @Override
        public String toString() {
            return "JSONMessage{" +
                    "purpose='" + purpose + '\'' +
                    ", to='" + to + '\'' +
                    ", content='" + content + '\'' +
                    ", latitude='" + latitude + '\'' +
                    ", longitude='" + longitude + '\'' +
                    ", location='" + location + '\'' +
                    '}';
        }
    }

}
