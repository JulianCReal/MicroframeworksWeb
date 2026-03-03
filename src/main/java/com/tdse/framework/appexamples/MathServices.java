package com.tdse.framework.appexamples;

import com.tdse.framework.utilities.HttpServer;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.tdse.framework.utilities.HttpServer.get;
import static com.tdse.framework.utilities.HttpServer.staticfiles;

/**
 * MathServices - Example application demonstrating the web framework.
 *
 * Endpoints:
 *  - http://localhost:35000/index.html
 *  - http://localhost:35000/App/hello?name=Pedro
 *  - http://localhost:35000/App/pi
 *  - http://localhost:35000/App/sqrt?value=144
 */
public class MathServices {

    public static void main(String[] args) throws IOException, URISyntaxException {

        // Serve static files from target/classes/webroot/public
        staticfiles("webroot/public");

        // REST endpoints using lambda functions
        get("/App/hello", (req, res) -> {
            String name = req.getValues("name");
            return name.isEmpty() ? "Hello, World!" : "Hello " + name + "!";
        });

        get("/App/pi", (req, res) -> "PI = " + Math.PI);

        get("/App/sqrt", (req, res) -> {
            String valueStr = req.getValues("value");
            try {
                double val = Double.parseDouble(valueStr);
                return "sqrt(" + val + ") = " + Math.sqrt(val);
            } catch (NumberFormatException e) {
                return "Please provide a valid number: ?value=<number>";
            }
        });

        // Start the server on port 35000
        HttpServer.start(35000);
    }
}
