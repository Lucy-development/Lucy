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


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
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
