package MyImplementations;
import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;

public class HTTPResponse {
    private BufferedWriter bw;
    private JSONObject json;

    public HTTPResponse(Socket socket) throws IOException {
        bw = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream())
        );
        json = new JSONObject();
    }

    @SuppressWarnings("unchecked")
    public void sendJSON(String message, String path) throws IOException {
        json.put("message", message);
        json.put("path", path);
        send("200 OK", json.toJSONString());
    }

    @SuppressWarnings("unchecked")
    public void sendJSON(String message, String path, JSONObject body) throws IOException {
        json.put("message", message);
        json.put("path", path);
        json.put("body", body);
        send("200 OK", json.toJSONString());
    }

    public void sendNotAllowed() throws IOException {
        send("405 Method Not Allowed", "405 Method Not Allowed");
    }

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
