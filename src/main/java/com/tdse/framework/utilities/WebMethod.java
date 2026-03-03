package com.tdse.framework.utilities;

/**
 * Functional interface representing a web endpoint handler.
 * Implementations receive a Request and Response object and return
 * the response body as a String.
 *
 * Usage example:
 *   get("/hello", (req, res) -> "Hello " + req.getValues("name"));
 */
@FunctionalInterface
public interface WebMethod {
    String execute(Request req, Response res);
}
