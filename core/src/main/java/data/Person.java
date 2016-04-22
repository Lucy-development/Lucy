package data;

import java.sql.Date;

/**
 * Created on 25/02/2016.
 */
public class Person {

    private String ID;
    private String firstName;
    private String lastName;
    private Date birthday;
    private String email;
    private String phone;
    private String meta;

    public Person(String ID, String firstName, String lastName, Date birthday, String phone, String email, String meta) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.meta = meta;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return ID != null ? ID.equals(person.ID) : person.ID == null;

    }

    @Override
    public int hashCode() {
        return ID != null ? ID.hashCode() : 0;
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
