package data;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.sql.Date;

/**
 * Created on 25/02/2016.
 */
public class Person {

    private Integer ID;
    private String firstName;
    private String lastName;
    private Date birthday;
    private String email;
    private String phone;
    private String meta;

    public Person(Integer ID, String firstName, String lastName, Date birthday, String phone, String email, String meta) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        try {
            //TODO: Error handling. PS! Remove invalid email from database.
            new InternetAddress(email).validate();
        } catch (AddressException e) {
            System.err.print("Invalid Email!");
        }
        this.meta = meta;
    }



    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return "Person{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", ID=" + ID +
                '}';
    }
}
