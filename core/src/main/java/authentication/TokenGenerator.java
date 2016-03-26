package authentication;

import java.math.BigInteger;
import java.security.SecureRandom;

public class TokenGenerator {

    SecureRandom rand;

    public TokenGenerator() {
        this.rand = new SecureRandom();
    }

    public String genSessToken() {
        return new BigInteger(130, rand).toString(32);
    }
}
