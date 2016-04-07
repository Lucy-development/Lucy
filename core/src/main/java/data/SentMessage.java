package data;


import java.sql.Timestamp;

/**
 * Created on 25/02/2016.
 */
public class SentMessage implements Sendable {
    private final String latitude;
    private final String longitude;
    private String id;
    private Timestamp time;
    private String sender;
    private String receiver;
    private String meta;
    private String content;
    private String location;


    public SentMessage(String id, Timestamp time, String sender, String receiver, String content, String longitude, String latitude, String location) {
        this.id = id;
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    @Override
    public String getID() {
        return id;
    }

    @Override
    public Timestamp getTime() {
        return time;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public String getReceiver() {
        return receiver;
    }

    @Override
    public String getMeta() {
        return meta;
    }

    public String getContent() {
        return content;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SentMessage that = (SentMessage) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SentMessage{" +
                "id=" + id +
                ", time=" + time +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", meta='" + meta + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
