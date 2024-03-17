package edu.eci.arep;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static spark.Spark.*;

/**
 * Clase que implementa un servicio de inicio de sesión utilizando un servidor web.
 * El servicio de inicio de sesión utiliza un servidor web para manejar las solicitudes de inicio de sesión.
 * El servicio se configura para escuchar en el puerto especificado en la variable de entorno "PORT" o en el puerto 5000 si no se especifica.
 * El servicio utiliza un certificado para garantizar la seguridad de las comunicaciones.
 * El servicio de inicio de sesión expone un único punto final "/login" que acepta dos parámetros: "name" y "password".
 *
 * @author Jefer Alexis Gonzalez Romero
 * @version 1.0 (17/03/2023)
 */
public class LoginService {

    /**
     * Método principal que inicia el servicio de inicio de sesión.
     *
     * @param args Argumentos de la línea de comandos.
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo de seguridad.
     * @throws KeyStoreException        Si ocurre un error al acceder al almacén de claves.
     * @throws IOException              Si ocurre un error al leer o escribir datos.
     * @throws KeyManagementException   Si ocurre un error al gestionar claves.
     * @throws CertificateException     Si ocurre un error al cargar certificados.
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException, CertificateException {
        staticFiles.location("/public");
        port(getPort());
        secure("certificados/ecikeystore.p12", "123456", null, null);
        configureTrustedSSLContext();
        get("/login", (req, res) -> {
            res.type("application/json");
            return readURL("name=" + req.queryParams("name") + "&password=" + req.queryParams("password"));
        });
    }

    /**
     * Obtiene el puerto en el que el servidor debe escuchar.
     * Si la variable de entorno "PORT" está definida, se devuelve el valor de la variable de entorno como un entero.
     * De lo contrario, se devuelve el valor predeterminado 5000.
     *
     * @return El puerto en el que el servidor debe escuchar.
     */
    public static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

    /**
     * Configura el contexto SSL predeterminado con un almacén de claves de confianza.
     * Este método crea un almacén de claves de confianza a partir del archivo "certificados/myTrustStore.p12" con la contraseña "123456".
     * Luego, crea un contexto SSL utilizando el almacén de claves de confianza y lo establece como el contexto SSL predeterminado.
     *
     * @throws KeyStoreException        Si ocurre un error al acceder al almacén de claves.
     * @throws IOException              Si ocurre un error al leer o escribir datos.
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo de seguridad.
     * @throws KeyManagementException   Si ocurre un error al gestionar claves.
     * @throws CertificateException     Si ocurre un error al cargar certificados.
     */
    private static void configureTrustedSSLContext() throws KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException, CertificateException {
        File trustStoreFile = new File("certificados/myTrustStore.p12");
        char[] trustStorePassword = "123456".toCharArray();
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(new FileInputStream(trustStoreFile), trustStorePassword);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        for (TrustManager t : tmf.getTrustManagers()) System.out.println(t);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        SSLContext.setDefault(sslContext);
    }

    /**
     * Lee el contenido de una URL y devuelve la respuesta como una cadena.
     * Este método construye una URL a partir de la cadena de consulta proporcionada y la URL base estática.
     * Luego, abre una conexión a la URL y obtiene los encabezados de la respuesta.
     * Por último, lee el cuerpo de la respuesta y lo devuelve como una cadena.
     *
     * @param query La cadena de consulta para construir la URL.
     * @return La respuesta de la URL como una cadena.
     * @throws IOException Si ocurre un error al leer o escribir datos.
     */
    public static String readURL(String query) throws IOException {
        URL siteURL = new URL("https://localhost:5002/user?" + query);
        URLConnection urlConnection = siteURL.openConnection();
        Map<String, List<String>> headers = urlConnection.getHeaderFields();
        Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            String headerName = entry.getKey();
            if (headerName != null) System.out.print(headerName + ":");
            List<String> headerValues = entry.getValue();
            for (String value : headerValues) System.out.print(value);
            System.out.println("");
        }
        System.out.println("-------message-body------");
        StringBuffer response = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String inputLine;
        while ((inputLine = reader.readLine()) != null) response.append(inputLine);
        reader.close();
        System.out.println(response);
        System.out.println("GET DONE");
        return response.toString();
    }
}