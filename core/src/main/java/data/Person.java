package data;

import java.sql.Date;

/**
 * Created on 25/02/2016.
 */
public class Person {

    private Integer ID;
    private String firstName;
    private String lastName;
    private Date birthday;
    private Email email;
    private String phone;
    private String meta;

    public Person(Integer ID, String firstName, String lastName, Date birthday, String phone, Email email, String meta) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.meta = meta;
    }

    public Person(String firstName, String lastName, Date birthday, Email email, String phone, String meta) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.email = email;
        this.phone = phone;
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

    public Email getEmail() {
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
