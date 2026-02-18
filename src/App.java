import MyImplementations.Server;

public class App {
    public static void main(String[] args) {
        if(args.length > 0) {
            if(args.length != 1) {
                System.err.println("\tIncorrect number of arguments");
                System.err.println("\tUsage: ");
                System.err.println("\tjava Server <port>");
                System.exit(1);
            }

            int port = Integer.parseInt(args[0]);
            new Server(port); 
        } else {
            new Server();
        }
    }
}
