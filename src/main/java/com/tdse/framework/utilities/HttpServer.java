package com.tdse.framework.utilities;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpServer - A lightweight web framework for building REST services and serving static files.
 *
 * Features:
 *  - Register GET endpoints via get(path, WebMethod) using lambda functions
 *  - Serve static files from a configurable directory via staticfiles(folder)
 *  - Extract query parameters via Request.getValues(name)
 *  - Port-configurable via start(port)
 *  - Binary-safe static file serving (images, CSS, JS) via Files.readAllBytes()
 *
 * Based on the original EchoServer and HttpServer examples from:
 * "Introducción a esquemas de nombres, redes, clientes y servicios con Java"
 * Luis Daniel Benavides Navarro, Escuela Colombiana de Ingeniería
 *
 * Usage example:
 * <pre>
 *   HttpServer.staticfiles("webroot/public");
 *   HttpServer.get("/hello", (req, res) -> "Hello " + req.getValues("name"));
 *   HttpServer.get("/pi", (req, res) -> String.valueOf(Math.PI));
 *   HttpServer.start(35000);
 * </pre>
 */
public class HttpServer {

    /** Registry of REST endpoint handlers, keyed by path */
    public static Map<String, WebMethod> endPoints = new HashMap<>();

    /** Folder inside target/classes where static files are located */
    public static String staticFilesFolder = "";

    /**
     * Registers a GET handler for the given path using a lambda (WebMethod).
     *
     * @param path The URL path to register (e.g., "/hello")
     * @param wm   A lambda or WebMethod implementation that handles the request
     *
     * Example:
     *   get("/hello", (req, res) -> "Hello " + req.getValues("name"));
     */
    public static void get(String path, WebMethod wm) {
        endPoints.put(path, wm);
    }

    /**
     * Defines the folder (relative to target/classes) from which static files will be served.
     *
     * @param folder Path to the static files folder (e.g., "webroot/public")
     *
     * Example:
     *   staticfiles("webroot/public");
     * Then a request to /index.html will serve target/classes/webroot/public/index.html
     */
    public static void staticfiles(String folder) {
        staticFilesFolder = folder;
    }

    /**
     * Starts the HTTP server on the given port.
     * Handles multiple sequential (non-concurrent) requests in a loop.
     *
     * Request processing order:
     * 1. Parse HTTP request line → extract path and query string via URI
     * 2. Check registered REST endpoints (get() handlers)
     * 3. If not found, serve a static file (binary-safe via Files.readAllBytes)
     * 4. If neither, return 404
     *
     * @param port The TCP port to listen on (e.g., 35000)
     */
    public static void start(int port) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }

        boolean running = true;

        System.out.println("Server started on port " + port);
        System.out.println("Static files folder: " + staticFilesFolder);
        System.out.println("Registered endpoints: " + endPoints.keySet());

        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            // Separate OutputStream for binary file serving (images, fonts, etc.)
            OutputStream outputStream = clientSocket.getOutputStream();

            String inputLine;
            boolean firstLine = true;
            String reqPath = "/";
            String reqQuery = "";

            // Read the HTTP request headers
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);

                if (firstLine) {
                    // Parse the first line: "GET /path?query HTTP/1.1"
                    String[] reqTokens = inputLine.split(" ");
                    if (reqTokens.length >= 2) {
                        // Use URI to correctly parse path and query separately
                        URI requestedURI = new URI(reqTokens[1]);
                        reqPath = requestedURI.getPath();
                        reqQuery = requestedURI.getQuery() != null ? requestedURI.getQuery() : "";
                        System.out.println("Path: " + reqPath);
                        System.out.println("Query: " + reqQuery);
                    }
                    firstLine = false;
                }

                if (!in.ready()) {
                    break;
                }
            }

            // Build Request and Response objects
            Request req = new Request(reqPath, reqQuery);
            Response res = new Response();

            // 1. Check if there is a registered REST endpoint for this path
            WebMethod wm = endPoints.get(reqPath);

            if (wm != null) {
                // Found a REST handler — execute the lambda
                String body = wm.execute(req, res);
                String outputLine = buildHttpResponse(200, "text/html", wrapInHtml(body));
                out.println(outputLine);

            } else if (!staticFilesFolder.isEmpty()) {
                // 2. Serve static file using binary-safe OutputStream + Files.readAllBytes()
                serveStaticFile(reqPath, outputStream);

            } else {
                // 3. Nothing found — 404
                String outputLine = buildHttpResponse(404, "text/html",
                        wrapInHtml("<h1>404 Not Found</h1><p>Resource not found: " + reqPath + "</p>"));
                out.println(outputLine);
            }

            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }

    /**
     * Serves a static file using a binary-safe OutputStream and Files.readAllBytes().
     * This correctly handles images, fonts, and other binary files — not just text.
     *
     * Looks for files in: target/classes/{staticFilesFolder}/{requestPath}
     *
     * @param requestPath The requested URL path (e.g., "/index.html")
     * @param outStream   The raw OutputStream of the client socket
     */
    private static void serveStaticFile(String requestPath, OutputStream outStream) {
        try {
            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }

            // Files.readAllBytes() handles binary content correctly (images, CSS, JS, etc.)
            File file = new File("target/classes/" + staticFilesFolder + requestPath);

            if (file.exists() && !file.isDirectory()) {
                String contentType = getContentType(requestPath);
                byte[] fileBytes = Files.readAllBytes(file.toPath());

                String header = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: " + contentType + "\r\n"
                        + "Content-Length: " + fileBytes.length + "\r\n"
                        + "\r\n";

                outStream.write(header.getBytes());
                outStream.write(fileBytes);
                outStream.flush();
                System.out.println("Serving static file: " + file.getPath());
            } else {
                String response = "HTTP/1.1 404 Not Found\r\n\r\n"
                        + "<h1>404 Not Found</h1><p>" + requestPath + "</p>";
                outStream.write(response.getBytes());
                outStream.flush();
                System.out.println("Static file not found: " + file.getPath());
            }
        } catch (IOException e) {
            System.err.println("Error serving static file: " + e.getMessage());
        }
    }

    /**
     * Determines the MIME content type based on the file extension.
     */
    private static String getContentType(String path) {
        if (path.endsWith(".html") || path.endsWith(".htm")) return "text/html";
        if (path.endsWith(".css"))  return "text/css";
        if (path.endsWith(".js"))   return "application/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif"))  return "image/gif";
        if (path.endsWith(".ico"))  return "image/x-icon";
        if (path.endsWith(".txt"))  return "text/plain";
        return "text/html";
    }

    /**
     * Builds a complete HTTP/1.1 response string (for text/REST responses).
     */
    private static String buildHttpResponse(int statusCode, String contentType, String body) {
        String statusText = statusCode == 200 ? "OK" : "Not Found";
        return "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length() + "\r\n"
                + "\r\n"
                + body;
    }

    /**
     * Wraps a content string in a basic HTML page structure.
     */
    private static String wrapInHtml(String content) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset=\"UTF-8\"><title>Web Framework</title></head>"
                + "<body>"
                + content
                + "</body>"
                + "</html>";
    }
}
