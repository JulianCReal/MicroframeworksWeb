package com.tdse.framework.utilities;

import java.util.HashMap;

/**
 * Represents an incoming HTTP request.
 * Parses the path and query parameters from the URI.
 *
 * Example URL: /hello?name=Pedro&age=25
 * - getPath()         -> "/hello"
 * - getValues("name") -> "Pedro"
 * - getValues("age")  -> "25"
 */
public class Request {

    private String path;
    private HashMap<String, String> queryParams;

    /**
     * Constructs a Request with an already-split path and query string.
     * This matches the pattern used in HttpServer where URI.getPath() and
     * URI.getQuery() are called before constructing the Request.
     *
     * @param path  The path portion of the URI (e.g., "/hello")
     * @param query The raw query string (e.g., "name=Pedro&age=25"), or empty string
     */
    public Request(String path, String query) {
        this.path = path;
        this.queryParams = new HashMap<>();
        parseQuery(query);
    }

    /**
     * Parses a query string like "name=Pedro&age=25" into the queryParams map.
     */
    private void parseQuery(String query) {
        if (query == null || query.isEmpty()) return;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                queryParams.put(keyValue[0], "");
            }
        }
    }

    /**
     * Returns the value of a query parameter by name.
     * Example: for URL /hello?name=Pedro, getValues("name") returns "Pedro".
     *
     * NOTE: Returns empty string (not null) when the parameter is not present,
     * and does NOT append a trailing space — safe to use directly in string concatenation.
     *
     * @param key The name of the query parameter
     * @return The value of the parameter, or empty String if not found
     */
    public String getValues(String key) {
        return queryParams.getOrDefault(key, "");
    }

    /**
     * Returns the path portion of the request URI (without query string).
     */
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Request{path='" + path + "', queryParams=" + queryParams + "}";
    }
}
