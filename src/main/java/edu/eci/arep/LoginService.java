package edu.eci.arep;

import java.security.NoSuchAlgorithmException;

import static spark.Spark.*;

public class LoginService {

    private static Usuarios usuarios;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        usuarios = new Usuarios();
        staticFiles.location("/public");
        port(getPort());
        secure("certificados/ecikeystore.p12", "123456", null, null);
        get("/hello", (req, res) -> "Hello World");
        get("/login", (req, res) -> {
            res.type("application/json");
            return loginResult(req.queryParams("name"), req.queryParams("password"));
        });
    }

    public static String loginResult(String name, String password) throws NoSuchAlgorithmException {
        boolean result = usuarios.verifyPassword(name, password);
        return "{\"result\":" + result + "}";
    }

    public static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000; //returns default port if heroku-port isn't set (i.e. on localhost)
    }
}