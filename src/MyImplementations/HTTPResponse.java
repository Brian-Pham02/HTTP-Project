package MyImplementations;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import org.json.simple.JSONObject;

/**
 * HTTPResponse.java
 * 
 * A barebones class implementation of an HTTP Response that relies
 * on a TCP socket connection output stream.
 * 
 * Its primary purpose is to rperesent the structure of a basic
 * HTTP response.
 */
public class HTTPResponse {
    // BufferedWriter object to write from the socket output stream
    // and the HTTP content.
    private BufferedWriter bw; 

    private static final Map<Integer, String> ERROR_MAP;
    static {
        ERROR_MAP = new HashMap<Integer, String>();
        ERROR_MAP.put(400, "400 Bad Request");
        ERROR_MAP.put(401, "401 Unauthorized");
        ERROR_MAP.put(403, "403 Forbidden");
        ERROR_MAP.put(404, "404 Not Found");
        ERROR_MAP.put(405, "405 Method Not Allowed");
        ERROR_MAP.put(500, "500 Internal Server Error");
    }

    private static final Map<Integer, String> SUCCESS_MAP;
    static {
        SUCCESS_MAP = new HashMap<Integer, String>();
        SUCCESS_MAP.put(200, "200 OK");
        SUCCESS_MAP.put(201, "201 Created");
        SUCCESS_MAP.put(204, "204 No Content");
    }

    /**
     * Constructor for the HTTPResponse class
     * 
     * Initializes a HTTPResponse object that receives a TCP socket object
     * an uses its output stream in a BufferedWriter object to write the 
     * the HTTP headers content.
     * 
     * @param socket The TCP socket object used to write the output stream to.
     * @throws IOException If the socket connection fails.
     */
    public HTTPResponse(Socket socket) throws IOException {
        // Initialize a BufferedWriter object to write the 
        // socket's output stream.
        bw = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream())
        );
    }

    @SuppressWarnings("unchecked")
    public void sendResponse(int statusCode, JSONObject response) throws IOException {
        if(!SUCCESS_MAP.containsKey(statusCode)) {
            throw new Error("Status code " + statusCode + " does not exist!");
        }
        
        response.put("response", SUCCESS_MAP.get(statusCode));
        send(SUCCESS_MAP.get(statusCode), response.toJSONString());
    }

    @SuppressWarnings("unchecked")
    public void sendError(int statusCode, String message) throws IOException {
        if(!ERROR_MAP.containsKey(statusCode)) {
            throw new Error("Status code " + statusCode + " does not exist!");
        }
        JSONObject errorJson = new JSONObject();
        errorJson.put("status", statusCode);
        errorJson.put("error", ERROR_MAP.get(statusCode));
        errorJson.put("message", message);
        send(ERROR_MAP.get(statusCode), errorJson.toJSONString());
    }

    /**
     * Writes the HTTP header content using a BufferedWriter object
     * 
     * @param status The status of the HTTP response
     * @param body The HTTP request body
     * @throws IOException Failed to write the content due to an IO error
     */
    private void send(String status, String body) throws IOException {
        bw.write("HTTP/1.1 " + status + "\r\n");
        bw.write("Content-Type: application/json\r\n");
        bw.write("Content-Length: " + body.length() + "\r\n");
        bw.write("Connection: close\r\n");
        bw.write("\r\n");
        bw.write(body);
        bw.flush();
    }
}
