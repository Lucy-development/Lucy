package data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//TODO Add exception handling
public class DatabaseManager {

    private final String PERSON_ID_COL = "id";
    private final String PERSON_FIRST_NAME_COL = "f_name";
    private final String PERSON_LAST_NAME_COL = "l_name";
    private final String PERSON_BIRTHDAY_COL = "b_day";
    private final String PERSON_EMAIL_COL = "email";
    private final String PERSON_PHONE_COL = "phone";
    private final String PERSON_META_COL = "meta";
    private final String PERSON_FRIENDS_COL = "person_friends";
    private Connection connection;

    public DatabaseManager() throws ClassNotFoundException, URISyntaxException, IOException, SQLException {
        Properties XMLprops = new Properties();

        // Load XML file
        XMLprops.loadFromXML((DatabaseManager.class.getClassLoader().getResourceAsStream("db.xml")));

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

        System.out.println(url);
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

    public Connection getConnection() {
        return connection;
    }

    public Person getPersonByID(String ID) {
        //language=PostgreSQL
        String query = "SELECT * FROM person_by_id(?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, ID);
            try (ResultSet rs = stmt.executeQuery()) {
                Person person;
                if (rs.next()) {
                    person = new Person(
                            rs.getString(PERSON_ID_COL),
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
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method for inserting messages into database.
     */
    public void insertSentMessageIntoDb(SentMessage sentMessage) {
        String sql = "{call insert_sent_message(?,?,?,?,?,?,?,?,?)}";

        try (CallableStatement statement = connection.prepareCall(sql)) {
            connection.setAutoCommit(false);

            statement.setString(1, sentMessage.getID());
            statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            statement.setString(3, sentMessage.getSender());
            statement.setString(4, sentMessage.getReceiver());
            statement.setString(5, sentMessage.getContent());
            statement.setString(6, sentMessage.getMeta());
            statement.setString(7, sentMessage.getLongitude());
            statement.setString(8, sentMessage.getLatitude());
            statement.setString(9, sentMessage.getLocation());
            statement.execute();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            cleanUpAfterException();
            e.printStackTrace();
        }
    }


    /**
     * Method for deleting contacts friends from database.
     */
    public void deleteAllContactFriends(String personId) {
        String sql = "{call delete_all_contacts(?)}";

        try (CallableStatement statement = connection.prepareCall(sql)) {
            connection.setAutoCommit(false);
            statement.setString(1, personId);
            statement.execute();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            cleanUpAfterException();
            e.printStackTrace();
        }
    }


    /**
     * Method for inserting person into database.
     */
    public void insertPersonIntoDb(Person person) {
        String sql = "{call insert_person(?,?,?,?,?,?,?)}";

        try (CallableStatement statement = connection.prepareCall(sql)) {
            connection.setAutoCommit(false);

            statement.setString(1, person.getID());
            statement.setString(2, person.getFirstName());
            statement.setString(3, person.getLastName());
            statement.setDate(4, person.getBirthday());
            statement.setString(5, person.getEmail());
            statement.setString(6, person.getPhone());
            statement.setString(7, person.getMeta());
            statement.execute();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            cleanUpAfterException();
            e.printStackTrace();
        }
    }

    /**
     * Method for adding friends
     *
     * @param personId - the one who will get a new friend
     */
    public void insertFriendIntoDb(String personId, String friendId) {
        String sql = "{call insert_friend(?,?)}";

        try (CallableStatement statement = connection.prepareCall(sql)) {
            connection.setAutoCommit(false);

            statement.setString(1, personId);
            statement.setString(2, friendId);
            statement.execute();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            cleanUpAfterException();
            e.printStackTrace();
        }
    }


    /**
     * method for retrieving messages by sender ID
     *
     * @param count - N.o messages to fetch
     */
    public List<SentMessage> retrieveMessagesBySender(String senderID, Integer count) {
        //language=PostgreSQL
        String sql = "SELECT * FROM messages_by_sender_id(?,?)";
        return retrieveMessages(sql, senderID, count);
    }


    /**
     * method for retrieving messages by recipient ID
     *
     * @param count - N.o messages to fetch
     */
    public List<SentMessage> retrieveMessagesByRecipient(String recipientID, Integer count) {
        //language=PostgreSQL
        String sql = "SELECT * FROM messages_by_recipient_id(?,?)";
        return retrieveMessages(sql, recipientID, count);
    }


    /**
     * General method for messages retrieval
     */
    private List<SentMessage> retrieveMessages(String sql, String id, Integer count) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setInt(2, count);
            try (ResultSet rs = stmt.executeQuery()) {
                List<SentMessage> messages = new ArrayList<>();
                while (rs.next()) {
                    messages.add(new SentMessage(rs.getString(1), rs.getTimestamp(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
                }
                return messages;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Method for retrieving contact id's from the database.
     */
    private List<String> getContactFriends(String ID) {
        String sql = "SELECT person_friends(?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ID);
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> contactFriends = new ArrayList<>();
                while (rs.next()) {
                    contactFriends.add(rs.getString(PERSON_FRIENDS_COL));
                }
                return contactFriends;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * @param ID - The ID of the person which contact we want to retrieve
     * @return List of friends
     */
    public List<Person> getFriends(String ID) {
        List<Person> friends = new ArrayList<>();
        getContactFriends(ID).forEach(id -> friends.add(getPersonByID(id)));
        return friends;
    }


    public Person getPersonByEmail(String email) {
        String query = "SELECT * FROM person_by_email(?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                Person person;
                if (rs.next()) {
                    person = new Person(
                            rs.getString(PERSON_ID_COL),
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
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Used in methods that try to insert data into the db
     * (insertPersonIntoDb and insertSentMessageIntoDb)
     */
    private void cleanUpAfterException() {
        if (connection != null) {
            try {
                System.err.print("Transaction is being rolled back");
                connection.rollback();
            } catch (SQLException excep) {
                throw new RuntimeException("Rollback has failed!");
            }
        }
    }

}