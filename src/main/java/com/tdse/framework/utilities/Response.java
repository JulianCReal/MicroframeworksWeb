package com.tdse.framework.utilities;

/**
 * Represents the HTTP response that will be sent back to the client.
 * Provides statusCode and contentType fields — more complete than a body-only Response.
 */
public class Response {

    private int statusCode = 200;
    private String contentType = "text/html";
    private String body;

    public Response() {}

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
