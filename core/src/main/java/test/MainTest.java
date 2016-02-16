package test;

import static spark.Spark.get;

/**
 * Created by Kaspar on 15/02/2016.
 */
public class MainTest {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World - LUCY");
    }
}
