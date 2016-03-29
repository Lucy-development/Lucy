package data;


import java.sql.Timestamp;

/**
 * Created on 25/02/2016.
 */
public class SentMessage implements Sendable {
    private Integer id = -1;
    private Timestamp time;
    private String sender;
    private String receiver;
    private String meta;
    private String content;


    public SentMessage(Timestamp time, String sender, String receiver, String content) {
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }


    @Override
    public Integer getID() {
        if (id == -1) throw new RuntimeException("ID not assigned!");
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
