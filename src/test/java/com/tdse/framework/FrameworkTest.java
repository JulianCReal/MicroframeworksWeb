package com.tdse.framework;

import com.tdse.framework.utilities.HttpServer;
import com.tdse.framework.utilities.Request;
import com.tdse.framework.utilities.Response;
import com.tdse.framework.utilities.WebMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the web framework components:
 * - Request: query parameter parsing, getValues()
 * - WebMethod: lambda execution
 * - HttpServer: endpoint registration, staticfiles()
 * - Response: default values
 */
public class FrameworkTest {

    @BeforeEach
    void clearState() {
        HttpServer.endPoints.clear();
        HttpServer.staticFilesFolder = "";
    }

    // -------------------------------------------------------
    // Request Tests
    // -------------------------------------------------------

    @Test
    void testRequestPathWithNoQuery() {
        Request req = new Request("/hello", "");
        assertEquals("/hello", req.getPath());
    }

    @Test
    void testRequestParsesSingleQueryParam() {
        Request req = new Request("/hello", "name=Pedro");
        assertEquals("Pedro", req.getValues("name"));
    }

    @Test
    void testRequestParsesMultipleQueryParams() {
        Request req = new Request("/hello", "name=Pedro&age=25");
        assertEquals("Pedro", req.getValues("name"));
        assertEquals("25", req.getValues("age"));
    }

    @Test
    void testRequestMissingParamReturnsEmptyString() {
        Request req = new Request("/hello", "name=Pedro");
        assertEquals("", req.getValues("nonexistent"));
    }

    @Test
    void testRequestGetValuesNoTrailingSpace() {
        // Bug fix: getValues() must NOT return "Pedro " with trailing space
        Request req = new Request("/hello", "name=Pedro");
        assertFalse(req.getValues("name").endsWith(" "),
                "getValues() should not return a value with a trailing space");
        assertEquals("Pedro", req.getValues("name"));
    }

    @Test
    void testRequestEmptyQuery() {
        Request req = new Request("/pi", "");
        assertEquals("", req.getValues("anything"));
    }

    @Test
    void testRequestPathIsPreserved() {
        Request req = new Request("/App/hello", "name=World");
        assertEquals("/App/hello", req.getPath());
    }

    // -------------------------------------------------------
    // WebMethod (Lambda) Tests
    // -------------------------------------------------------

    @Test
    void testLambdaReturnsStaticString() {
        WebMethod wm = (req, res) -> "hello world";
        Request req = new Request("/test", "");
        Response res = new Response();
        assertEquals("hello world", wm.execute(req, res));
    }

    @Test
    void testLambdaUsesQueryParam() {
        WebMethod wm = (req, res) -> "Hello " + req.getValues("name");
        Request req = new Request("/hello", "name=Pedro");
        Response res = new Response();
        assertEquals("Hello Pedro", wm.execute(req, res));
    }

    @Test
    void testLambdaReturnsPI() {
        WebMethod wm = (req, res) -> "PI = " + Math.PI;
        Request req = new Request("/pi", "");
        Response res = new Response();
        assertEquals("PI = " + Math.PI, wm.execute(req, res));
    }

    @Test
    void testLambdaWithMultipleParams() {
        WebMethod wm = (req, res) ->
                "Hello " + req.getValues("name") + ", age: " + req.getValues("age");
        Request req = new Request("/hello", "name=Pedro&age=25");
        Response res = new Response();
        assertEquals("Hello Pedro, age: 25", wm.execute(req, res));
    }

    // -------------------------------------------------------
    // HttpServer Registration Tests
    // -------------------------------------------------------

    @Test
    void testGetRegistersEndpoint() {
        HttpServer.get("/test", (req, res) -> "ok");
        assertTrue(HttpServer.endPoints.containsKey("/test"));
    }

    @Test
    void testGetEndpointExecutesCorrectly() {
        HttpServer.get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        WebMethod wm = HttpServer.endPoints.get("/hello");
        assertNotNull(wm);
        Request req = new Request("/hello", "name=World");
        Response res = new Response();
        assertEquals("Hello World", wm.execute(req, res));
    }

    @Test
    void testStaticfilesSetsFolder() {
        HttpServer.staticfiles("webroot/public");
        assertEquals("webroot/public", HttpServer.staticFilesFolder);
    }

    @Test
    void testMultipleEndpointsRegistered() {
        HttpServer.get("/hello", (req, res) -> "hello");
        HttpServer.get("/pi",    (req, res) -> String.valueOf(Math.PI));
        HttpServer.get("/sqrt",  (req, res) -> "sqrt");
        assertEquals(3, HttpServer.endPoints.size());
    }

    @Test
    void testEndpointOverwrite() {
        HttpServer.get("/hello", (req, res) -> "first");
        HttpServer.get("/hello", (req, res) -> "second");
        Request req = new Request("/hello", "");
        Response res = new Response();
        assertEquals("second", HttpServer.endPoints.get("/hello").execute(req, res));
    }

    // -------------------------------------------------------
    // Response Tests
    // -------------------------------------------------------

    @Test
    void testResponseDefaultStatusCode() {
        Response res = new Response();
        assertEquals(200, res.getStatusCode());
    }

    @Test
    void testResponseSetStatusCode() {
        Response res = new Response();
        res.setStatusCode(404);
        assertEquals(404, res.getStatusCode());
    }

    @Test
    void testResponseDefaultContentType() {
        Response res = new Response();
        assertEquals("text/html", res.getContentType());
    }

    @Test
    void testResponseSetBody() {
        Response res = new Response();
        res.setBody("hello");
        assertEquals("hello", res.getBody());
    }
}
