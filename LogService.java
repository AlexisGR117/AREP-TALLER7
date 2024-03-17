package com.example.awsprimerlogservice;

import com.mongodb.client.MongoDatabase;

import java.util.Date;

import static spark.Spark.get;
import static spark.Spark.port;

/**
 * Esta clase proporciona un servicio de registro que registra mensajes en una base de datos de MongoDB utilizando la clase `LogDAO`.
 * La clase también configura un servidor web local utilizando la biblioteca Spark y proporciona un punto de entrada para las solicitudes HTTP GET a la ruta "/logservice".
 *
 * @author Jefer Alexis Gonzalez Romero
 * @version 1.0 (10/03/2023)
 */
public class LogService {

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String... args) {
        MongoDatabase database = MongoUtil.getDB();
        LogDAO logDAO = new LogDAO(database);
        port(getPort());
        get("/logservice", (req, res) -> {
            Date fechaActual = new Date();
            String fechaTexto = fechaActual.toString();
            logDAO.addLog(fechaTexto, req.queryParams("msg"));
            System.out.println(req.queryParams("msg"));
            res.type("application/json");
            return logDAO.listLogs();
        });
    }

    /**
     * Obtiene el puerto en el que se ejecutará el servidor web local.
     *
     * @return El puerto en el que se ejecutará el servidor web local.
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
