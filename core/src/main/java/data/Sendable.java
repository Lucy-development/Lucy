package data;

import java.sql.Timestamp;

/**
 * Created on 25/02/2016.
 */
public interface Sendable {

    String getID();

    Timestamp getTime();

    String getSender();

    String getReceiver();

    String getMeta();

}
