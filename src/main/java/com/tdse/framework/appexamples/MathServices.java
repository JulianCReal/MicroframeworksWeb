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
 *  - http://localhost:35000/init
 *  - http://localhost:35000/hello?name=Pedro&lastname=Perez
 *  - http://localhost:35000/pi
 *  - http://localhost:35000/api/name?name=Pedro&age=25
 *  - http://localhost:35000/api/pi
 */
public class MathServices {

    public static void main(String[] args) throws IOException, URISyntaxException {

        staticfiles("webroot/public");

        get("/init", (req, res) -> "Hello World");

        get("/hello", (req, res) ->
                "Hello to: " + req.getValues("name") + " " + req.getValues("lastname"));

        get("/pi", (req, res) -> "PI = " + Math.PI);

        get("/api/name", (req, res) ->
                "My name is: " + req.getValues("name") + " And my age is: " + req.getValues("age"));

        get("/api/pi", (req, res) -> "The value of Pi is = " + Math.PI);

        HttpServer.start(35000);
    }
}
