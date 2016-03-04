package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Priit Paluoja on 24.02.2016.
 */

//TODO Add exception handling
// TODO: Remove duplicate code
public class DatabaseManager {

    private final String PERSON_ID_COL = "id";
    private final String PERSON_FIRST_NAME_COL = "f_name";
    private final String PERSON_LAST_NAME_COL = "l_name";
    private final String PERSON_BIRTHDAY_COL = "b_day";
    private final String PERSON_EMAIL_COL = "email";
    private final String PERSON_PHONE_COL = "phone";
    private final String PERSON_META_COL = "meta";
    private final String MESSAGE_ID_COL = "id";
    private final String MESSAGE_TIMESTAMP_COL = "timestamp";
    private final String MESSAGE_SENDER_COL = "sender";
    private final String MESSAGE_RECEIVER_COL = "recipient";
    private final String MESSAGE_CONTENT_COL = "content";
    private final String MESSAGE_META_COL = "meta";
    private final String FILE_ID_COL = "id";
    private final String FILE_TIMESTAMP_COL = "timestamp";
    private final String FILE_SENDER_COL = "sender";
    private final String FILE_RECEIVER_COL = "recipient";
    private final String FILE_FILE_COL = "file";
    private final String FILE_META_COL = "meta";
    private final String PERSON_FRIENDS_COL = "person_friends";
    private Connection connection;


    public DatabaseManager() throws ClassNotFoundException, URISyntaxException, IOException, SQLException {
        Properties XMLprops = new Properties();

        // Load XML file
        XMLprops.loadFromXML(new FileInputStream(new File(DatabaseManager.class.getClassLoader().getResource("db.xml").toURI())));
        // Load driver
        Class.forName("org.postgresql.Driver");
        // Read from XML
        String hostname = XMLprops.getProperty("hostname");
        String port = XMLprops.getProperty("port");
        String dbName = XMLprops.getProperty("dbName");
        String user = XMLprops.getProperty("user");
        String pswd = XMLprops.getProperty("password");
        String ssl = XMLprops.getProperty("ssl");

        // JDBC URL: https://github.com/heroku/devcenter-java-database
        String url = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName +
                "?user=" + user +
                "&password=" + pswd +
                "&ssl=" + ssl + "&sslfactory=org.postgresql.ssl.NonValidatingFactory";

        // Create connection properties
        Properties connectionProps = new Properties();
        connectionProps.setProperty("user", user);
        connectionProps.setProperty("password", pswd);
        connectionProps.setProperty("ssl", ssl);

        this.connection = DriverManager.getConnection(url, connectionProps);
    }

    public void closeConnection() throws SQLException {
        if (connection != null) connection.close();
    }

    public Person getPersonByID(Integer ID) {
        String query = String.format("SELECT * FROM person_by_id('%s')", ID);

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            Person person;
            if (rs.next()) {
                person = new Person(
                        rs.getInt(PERSON_ID_COL),
                        rs.getString(PERSON_FIRST_NAME_COL),
                        rs.getString(PERSON_LAST_NAME_COL),
                        rs.getDate(PERSON_BIRTHDAY_COL),
                        rs.getString(PERSON_PHONE_COL),
                        rs.getString(PERSON_EMAIL_COL),
                        rs.getString(PERSON_META_COL)
                );
            } else throw new RuntimeException();

            if (rs.next()) throw new RuntimeException();

            return person;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Method for inserting messages into database.
     */
    public void insertSentMessageIntoDb(SentMessage sentMessage) {
        String sql = "INSERT INTO message(timestamp, sender, recipient, content, meta)"
                + " VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            statement.setInt(2, sentMessage.getSender());
            statement.setInt(3, sentMessage.getReceiver());
            statement.setString(4, sentMessage.getContent());
            statement.setString(5, sentMessage.getMeta());
            statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException excep) {
                    throw new RuntimeException("Rollback has failed!");
                }
            }
            e.printStackTrace();
        }
    }


    /**
     * Method for inserting person into database.
     */
    public void insertPersonIntoDb(Person person) {
        String sql = "INSERT INTO person(f_name,l_name,b_day,email,phone,meta)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            statement.setString(1, person.getFirstName());
            statement.setString(2, person.getLastName());
            statement.setDate(3, person.getBirthday());
            statement.setString(4, person.getEmail());
            statement.setString(5, person.getPhone());
            statement.setString(6, person.getMeta());
            statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException excep) {
                    throw new RuntimeException("Rollback has failed!");
                }
            }
            e.printStackTrace();
        }
    }


    /**
     * method for retrieving messages by sender ID
     *
     * @param count - N.o messages to fetch
     */
    public List<SentMessage> retrieveMessagesBySender(Integer senderID, Integer count) {
        String sql = String.format("SELECT * FROM messages_by_sender_id(%s,%s)", senderID, count);
        return retrieveMessages(sql);
    }

    /**
     * method for retrieving messages by sender email
     *
     * @param count - N.o messages to fetch
     */
    public List<SentMessage> retrieveMessagesBySender(String senderEmail, Integer count) {
        String sql = String.format("SELECT * FROM messages_by_sender_id(%s,%s)", getPersonByEmail(senderEmail).getID(), count);
        return retrieveMessages(sql);
    }


    /**
     * method for retrieving messages by recipient ID
     *
     * @param count - N.o messages to fetch
     */
    public List<SentMessage> retrieveMessagesByRecipient(Integer recipientID, Integer count) {
        String sql = String.format("SELECT * FROM messages_by_recipient_id(%s,%s)", recipientID, count);
        return retrieveMessages(sql);
    }

    /**
     * method for retrieving messages by recipient email
     *
     * @param count - N.o messages to fetch
     */
    public List<SentMessage> retrieveMessagesByRecipient(String recipientEmail, Integer count) {
        String sql = String.format("SELECT * FROM messages_by_recipient_id(%s,%s)", getPersonByEmail(recipientEmail).getID(), count);
        return retrieveMessages(sql);
    }

    /**
     * General method for messages retrieval
     */
    private List<SentMessage> retrieveMessages(String sql) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<SentMessage> messages = new ArrayList<>();

            while (rs.next()) {
                messages.add(new SentMessage(rs.getTimestamp(1), rs.getInt(2), rs.getInt(3), rs.getString(4)));
            }
            return messages;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Method for retrieving contact id's from the database.
     */
    private List<Integer> getContactFriends(Integer ID) {
        String sql = String.format("SELECT person_friends(%s)", ID);

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Integer> contactFriends = new ArrayList<>();
            while (rs.next()) {
                contactFriends.add(rs.getInt(PERSON_FRIENDS_COL));
            }
            return contactFriends;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * @param ID - The ID of the person which contact we want to retrieve
     * @return List of friends
     */
    public List<Person> getFriends(Integer ID) {
        List<Person> friends = new ArrayList<>();
        getContactFriends(ID).forEach(id -> friends.add(getPersonByID(id)));
        return friends;
    }

    /**
     * @param email - The email of the person which contact we want to retrieve
     * @return List of friends
     */
    public List<Person> getFriends(String email) {
        return getFriends(getPersonByEmail(email).getID());
    }


    // TODO: method to files

    // TODO: figure out how to use messages_today_by(int)

    /**
     * Return sent message count today by SenderId
     */
    public BigDecimal getSentMessageCountToday(Integer senderId) {
        String query = String.format("SELECT  messages_today_by(%s)", senderId);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            rs.next();
            return rs.getBigDecimal("messages_today_by");

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Person getPersonByEmail(String email) {
        String query = String.format("SELECT * FROM person_by_email('%s')", email);

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            Person person;
            if (rs.next()) {
                person = new Person(
                        rs.getInt(PERSON_ID_COL),
                        rs.getString(PERSON_FIRST_NAME_COL),
                        rs.getString(PERSON_LAST_NAME_COL),
                        rs.getDate(PERSON_BIRTHDAY_COL),
                        rs.getString(PERSON_PHONE_COL),
                        email,
                        rs.getString(PERSON_META_COL)
                );
            } else throw new RuntimeException();

            if (rs.next()) throw new RuntimeException();

            return person;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}



