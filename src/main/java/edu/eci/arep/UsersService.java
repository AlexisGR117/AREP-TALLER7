package edu.eci.arep;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

import static spark.Spark.*;

/**
 * Clase que gestiona los usuarios y sus contraseñas.
 * La clase almacena los usuarios y sus contraseñas hash en un mapa estático.
 * La clase proporciona métodos para agregar usuarios, verificar contraseñas y obtener el puerto en el que el servidor debe escuchar.
 *
 * @author Jefer Alexis Gonzalez Romero
 * @version 1.0 (17/03/2023)
 */
public class UsersService {

    private static final HashMap<String, byte[]> users = new HashMap<>();

    /**
     * Método principal que agrega usuarios y configura el servidor.
     *
     * @param args Argumentos de la línea de comandos.
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo de seguridad.
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        addUser("Alexis", "123456");
        addUser("Jefer", "654321");
        port(getPort());
        secure("certificados/ecikeystore.p12", "123456", null, null);
        get("/user", (req, res) -> {
            res.type("application/json");
            boolean result = verifyPassword(req.queryParams("name"), req.queryParams("password"));
            return "{\"result\":" + result + "}";
        });
    }

    /**
     * Hashea una contraseña utilizando el algoritmo SHA-256.
     * Este método crea un objeto MessageDigest con el algoritmo SHA-256 y utiliza el objeto para hash la contraseña proporcionada.
     *
     * @param password La contraseña a hash.
     * @return El hash de la contraseña.
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo de seguridad.
     */
    public static byte[] hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes();
        return md.digest(bytes);
    }

    /**
     * Verifica si una contraseña coincide con el hash almacenado en el mapa de usuarios.
     * Este método obtiene el hash de la contraseña proporcionada y lo compara con el hash almacenado en el mapa de usuarios.
     *
     * @param userName El nombre de usuario asociado con el hash de la contraseña.
     * @param password La contraseña a verificar.
     * @return Verdadero si la contraseña coincide con el hash almacenado, falso en caso contrario.
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo de seguridad.
     */
    public static boolean verifyPassword(String userName, String password) throws NoSuchAlgorithmException {
        byte[] hash = users.get(userName);
        byte[] attemptedHash = hashPassword(password);
        return Arrays.equals(hash, attemptedHash);
    }

    /**
     * Obtiene el puerto en el que el servidor debe escuchar.
     * Si la variable de entorno "PORT" está definida, se devuelve el valor de la variable de entorno como un entero.
     * De lo contrario, se devuelve el valor predeterminado 5002.
     *
     * @return El puerto en el que el servidor debe escuchar.
     */
    public static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 8088; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

    /**
     * Agrega un usuario al mapa de usuarios con su contraseña hash.
     * Este método agrega un usuario al mapa de usuarios con su contraseña hash utilizando el método hashPassword.
     *
     * @param name     El nombre de usuario.
     * @param password La contraseña del usuario.
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo de seguridad.
     */
    public static void addUser(String name, String password) throws NoSuchAlgorithmException {
        users.put(name, hashPassword(password));
    }
}
