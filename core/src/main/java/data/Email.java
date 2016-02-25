package data;

/**
 * Created on 25/02/2016.
 */
public class Email {

    private final String address;

    public Email(String address) {
        // TODO: Check validity
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
