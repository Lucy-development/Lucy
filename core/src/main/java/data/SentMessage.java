package data;


import java.sql.Timestamp;

/**
 * Created on 25/02/2016.
 */
public class SentMessage implements Sendable {
    private Integer id = -1;
    private Timestamp time;
    private Person sender;
    private Person receiver;
    private String meta;
    private String content;

    public SentMessage(Timestamp time, Person sender, Person receiver, String meta, String content) {
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
        this.meta = meta;
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
    public Person getSender() {
        return sender;
    }

    @Override
    public Person getReceiver() {
        return receiver;
    }

    @Override
    public String getMeta() {
        return meta;
    }

    public String getContent() {
        return content;
    }
}
