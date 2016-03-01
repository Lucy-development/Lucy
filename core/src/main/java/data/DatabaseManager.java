package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Properties;

/**
 * Created by Priit Paluoja on 24.02.2016.
 */

//TODO Add exception handling
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

    public Person personByID(Integer ID) {
        String query = String.format("SELECT * FROM person_by_id('%s')",ID);

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
                        new Email(rs.getString(PERSON_EMAIL_COL)),
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

    // TODO: method to add messages and files

    // TODO: figure out how to use messages_today_by(int)

    // TODO: use VIEW birthday_today somehow


    public Person personByEmail(Email email) {
        String query = String.format("SELECT * FROM person_by_email('%s')", email.getAddress());

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



