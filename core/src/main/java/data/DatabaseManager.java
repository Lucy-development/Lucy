package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Priit Paluoja on 24.02.2016.
 */

//TODO Exception handling
public class DatabaseManager {
    private Connection connection;


    public DatabaseManager() throws ClassNotFoundException, URISyntaxException, IOException, SQLException {
        Properties props = new Properties();
        props.loadFromXML(new FileInputStream(new File(DatabaseManager.class.getClassLoader().getResource("db.xml").toURI())));
        Class.forName("org.postgresql.Driver");


        String hostname = props.getProperty("hostname");
        String port = props.getProperty("port");
        String dbName = props.getProperty("dbName");
        String user = props.getProperty("user");
        String pswd = props.getProperty("password");
        String ssl = "true";


        // https://github.com/heroku/devcenter-java-database
        String url = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName +
                "?user=" + user +
                "&password=" + pswd +
                "&ssl=" + ssl + "&sslfactory=org.postgresql.ssl.NonValidatingFactory";

        props.setProperty("user", user);
        props.setProperty("password", pswd);
        props.setProperty("ssl", ssl);
        connection = DriverManager.getConnection(url, props);
    }


    public Connection getConnection() {
        return connection;
    }


    public void closeConnection() throws SQLException {
        if (connection != null) connection.close();
    }


}



