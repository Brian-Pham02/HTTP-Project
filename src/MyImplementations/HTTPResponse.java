package MyImplementations;
import java.net.*;
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

    // JSONObject object to write the JSON object data in the API
    // response.
    private JSONObject json;

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

        // Initialize a JSONObject object to deliver a JSON body response
        json = new JSONObject();
    }

    /**
     * Writes the message and path to a JSON object.
     * 
     * @param message The message in the response
     * @param path The resource path in the response
     * @throws IOException Failed to write the content due to an IO error
     */
    @SuppressWarnings("unchecked")
    public void sendJSON(String message, String path) throws IOException {
        json.put("message", message);
        json.put("path", path);
        send("200 OK", json.toJSONString());
    }

    /**
     * Writes the message and path to a JSON object.
     * 
     * @param message The message in the response
     * @param path The resource path in the response
     * @param body The request body delivered to the response
     * @throws IOException Failed to write the content due to an IO error
     */
    @SuppressWarnings("unchecked")
    public void sendJSON(String message, String path, JSONObject body) throws IOException {
        json.put("message", message);
        json.put("path", path);
        json.put("body", body);
        send("200 OK", json.toJSONString());
    }

    /**
     * Writes a Not Allowed message to the stream.
     * 
     * @throws IOException Failed to write content due to an IO error
     */
    public void sendNotAllowed() throws IOException {
        send("405 Method Not Allowed", "405 Method Not Allowed");
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
        json.clear();
    }
}
