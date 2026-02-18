package MyImplementations;
import java.net.*;
import java.io.*;
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
    private void handleClient(Socket client) {
        try {
            // Parse the request
            HTTPRequest request = new HTTPRequest(client);

            // Prepare response
            HTTPResponse response = new HTTPResponse(client);

            // Only parse body for methods that can have one
            JSONObject requestBody = null;
            String method = request.getMethod();
            if (method.equals("POST") || method.equals("PUT") || method.equals("PATCH")) {
                String rawBody = request.getBody();
                if(rawBody != null && !rawBody.isEmpty()) {
                    try {
                        requestBody = (JSONObject) new org.json.simple.parser.JSONParser().parse(rawBody);
                    } catch (org.json.simple.parser.ParseException e) {
                        requestBody = new JSONObject(); // empty JSON if parse fails
                    }
                }
            }

            switch (request.getMethod()) {
                case "GET": 
                    response.sendJSON("GET request received!", request.getPath()); 
                    break;
                case "POST": 
                    response.sendJSON("POST request received!", request.getPath(), requestBody); 
                    break;
                case "PUT": 
                    response.sendJSON("PUT request received!", request.getPath(), requestBody); 
                    break;
                case "PATCH": 
                    response.sendJSON("PATCH request received!", request.getPath(), requestBody); 
                    break;
                case "DELETE": response.sendJSON("DELETE request received!", request.getPath()); break;
                default: response.sendNotAllowed();
            }

            client.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}