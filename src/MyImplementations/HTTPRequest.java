package MyImplementations;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;


public class HTTPRequest {
    private String method, path, body;
    private Map<String, String> headers;


    public HTTPRequest(Socket socket) throws IOException {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(socket.getInputStream())
        );
        headers = new HashMap<String, String>();

        String requestLine = br.readLine();
        if(requestLine == null || requestLine.split(" ").length < 2) {
            method = "INVALID";
            path = "";
            return;
        }



        String[] parts = requestLine.split(" ");
        method = parts[0];
        path = parts[1];

        String line;
        while((line = br.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        if(headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            int read = br.read(buffer, 0, contentLength);
            body = new String(buffer, 0, read);
        }

        System.out.println("Method: " + method);
        System.out.println("Path: " + path);
        System.out.println("Body: " + body);
    }

    public String getMethod() { 
        return method; 
    }

    public String getPath() { 
        return path; 
    }

    public String getBody() { 
        return body; 
    }

    public Map<String, String> getHeaders() { 
        return headers; 
    }
}
