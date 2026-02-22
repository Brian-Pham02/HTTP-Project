package MyImplementations;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * HTTPResponse.java
 * 
 * A barebones class implementation of an HTTP Request that relies
 * on a TCP socket connection input stream.
 * 
 * Its primary purpose is to represent the structure of a basic
 * HTTP request.
 */
public class HTTPRequest {
    // The request method, path, version, and body
    private String method, version, body;
    private String[] paths;

    // The headers map
    private Map<String, String> headers;

    private Map<String, Set<String>> queries;

    private final String queryPattern = "[?&]([^=]+)(=([^&#]*))?";

    /**
     * Constructor for the HTTPRequest class
     * 
     * Initializes a HTTPRequest object that receives a TCP socket object 
     * and uses its input stream in a BufferedReader object to read in
     * the HTTP headers content.
     * 
     * @param socket The TCP socket object used to read the input stream from.
     * @throws IOException If the socket connection fails.
     */
    public HTTPRequest(Socket socket) throws IOException {

        // Initialize a BufferedReader object to read in the
        // socket's input stream.
        BufferedReader br = new BufferedReader(
            new InputStreamReader(socket.getInputStream())
        );

        // Initialize a HashMap object to store in the key-value
        // data of a HTTP headers.
        headers = new HashMap<String, String>();
        queries = new HashMap<String, Set<String>>();

        // Reads the BufferedReader object for the request object
        String requestLine = br.readLine();

        // The request line is invalid.
        if(requestLine == null || requestLine.split(" ").length < 2) {
            method = "INVALID";
            return;
        }   

        // Split the request line into three parts:
        // - Method: The request method used
        // - Path: The resource requested
        // - Version: HTTP version used
        String[] parts = requestLine.split(" ");
        method = parts[0];

        String fullPath = parts[1];

        // Separate path and query string
        String pathOnly;
        int queryIndex = fullPath.indexOf("?");

        if (queryIndex >= 0) {
            pathOnly = fullPath.substring(0, queryIndex);
        } else {
            pathOnly = fullPath;
        }

        // Build query parameters from full URL
        buildQueryParameters(fullPath);

        // Now split only the clean path
        paths = pathOnly.split("(?=\\/)");

        version = parts[2];

        String line;
        // Read in the line content and add it to the headers
        // map object.
        while((line = br.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        // Used only for POST, PUT, and PATCH requests
        // when it receives a request body.
        // 
        // Extracts the body data from the request.
        if(headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            int read = br.read(buffer, 0, contentLength);
            body = new String(buffer, 0, read);
        }

        // Output to the console
        System.out.println("Method: " + method);
        System.out.println("Paths: " + Arrays.toString(paths));
        System.out.println("Version: " + version);
        System.out.println("Body: " + body);

        if(headers != null) {
            System.out.println("Headers:");
            headers.forEach((key, value) -> {
                System.out.printf("\t%s -> %s\n", key, value);
            });
        }
        
        if(!queries.isEmpty()) {
            System.out.println("Query Parameters:");
            queries.forEach((key, value) -> {   
                System.out.printf("\t%s -> %s\n", key, value);
            });
        }
    }

    /**
     * @return The request method
     */
    public String getMethod() { 
        return method; 
    }

    /**
     * @return The resource path
     */
    public String[] getPaths() { 
        return paths; 
    }

    /**
     * @return The HTTP version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return The request body formatted as a JSON object
     */
    public String getBody() { 
        return body; 
    }

    /**
     * @return The HTTP request headers object
     */
    public Map<String, String> getHeaders() { 
        return headers; 
    }

    /**
     * @return The query parameters map
     */
    public Map<String, Set<String>> getQueryParameters() {
        return queries;
    }

    /**
     * Extracts the query parameters using regex and builds a 
     * queries HashMap object representing the query paramters
     * and its values.
     * 
     * @param url the HTTP resource path
     */
    private void buildQueryParameters(String url) {
        if (url == null || !url.contains("?")) {
            return;
        }

        Pattern regex = Pattern.compile(queryPattern);
        Matcher matcher = regex.matcher(url);

        while (matcher.find()) {
            String queryPart = matcher.group().substring(1);
            String[] queryStrings = queryPart.split("=", 2);

            String key = queryStrings[0];
            String value = queryStrings.length > 1 ? queryStrings[1] : "";

            queries
            .computeIfAbsent(key, k -> new HashSet<>())
            .add(value.toString());
        }
    }
}
