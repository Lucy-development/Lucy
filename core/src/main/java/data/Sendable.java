package data;

import java.sql.Timestamp;

/**
 * Created on 25/02/2016.
 */
public interface Sendable {

    Integer getID();

    Timestamp getTime();

    Person getSender();

    Person getReceiver();

    String getMeta();

}
