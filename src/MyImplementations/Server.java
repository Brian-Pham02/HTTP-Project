package MyImplementations;
import java.net.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Server.java
 * 
 * A centralized HTTP server using a TCP connection.
 * 
 * Its primary function is to receive API requests and deliver
 * a response. 
 * 
 * In short, this is a barebones implementation of how a HTTP
 * server works under the hood.
 */
public class Server {
    // ServerSocket to receive client connection
    private ServerSocket serverSocket = null;

    /**
     * Server(int port)
     * 
     * Constructor for the centralized Server class.
     * Initializes the server to listen for incoming client connections
     * using default PORT 5000
     */
    public Server() {
        this(5000);
    }

    /**
     * Server(int port)
     * 
     * Constructor for the centralized Server class.
     * Initializes the server to listen for incoming client connections.
     * 
     * @param port The port number the server will listen on
     */
    public Server(int port) {
        // Run the server and wait for a client to connect
        try {
            // Set up the server on the specified port
            serverSocket = new ServerSocket(port);
            System.out.printf("Server started on port %d\n", port);

            // Indicate that the server is waiting for a client connection
            System.out.println("Waiting for a client...");    

            while(true) {
                Socket client = serverSocket.accept();
                handleClient(client);
            }

           
        } catch(IOException i) {
            System.err.println("Failed to start server: " + i);
            return;
        }
    }

    /**
     * Handles the client request and delivers the appropriate
     * response based on the given request method.
     * 
     * @param client The Socket object representing the client performing the request
     */
    @SuppressWarnings("unchecked")
    private void handleClient(Socket client) {
        try {
            // Parse the request
            HTTPRequest request = new HTTPRequest(client);

            // Prepare response
            HTTPResponse response = new HTTPResponse(client);

            String method = request.getMethod();
            String[] paths = request.getPaths();

            Map<String, Set<String>> queryParams = request.getQueryParameters();
            Map<String, String> headers = request.getHeaders();

            // Parse body if applicable
            String requestBodyString = request.getBody();
            JSONObject requestBody = null;

            if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
                if (requestBodyString != null && !requestBodyString.isEmpty()) {
                    try {
                        requestBody = (JSONObject) new org.json.simple.parser.JSONParser()
                            .parse(requestBodyString);
                    } catch (org.json.simple.parser.ParseException e) {
                        // Use sendError with ERROR_MAP for bad JSON
                        response.sendError(400, "Invalid JSON body");
                        client.close();
                        return;
                    }
                }
            }

            JSONObject responseBody = new JSONObject();

            responseBody.put("message", method + " request received!");

            // Add path segments
            JSONArray pathArray = new JSONArray();
            if (paths != null) {
                pathArray.addAll(Arrays.asList(paths));
            }
            responseBody.put("paths", pathArray);

            // Add query params
            if(!queryParams.isEmpty()) {
                JSONObject queryObject = new JSONObject();
                queryParams.forEach((key, valueSet) -> {
                    JSONArray arr = new JSONArray();
                    arr.addAll(valueSet);
                    queryObject.put(key, arr);
                });
                responseBody.put("queryParams", queryObject);
            }
            

            // Add header content
            JSONObject headerObject = new JSONObject();
            headers.forEach((key, value) -> {
                headerObject.put(key, value);
            });
            responseBody.put("headers", headerObject);


            // Add body if applicable
            if (requestBody != null) {
                responseBody.put("body", requestBody);
            }

            // Handle request methods
            switch (method) {
                case "GET":
                case "DELETE":
                case "POST":
                case "PUT":
                case "PATCH":
                    response.sendResponse(
                        200,
                        responseBody
                    );
                    break;

                default:
                    response.sendError(405, "Method Not Allowed");
            }

            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}