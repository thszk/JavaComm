// Main class of the program, here is instantiated and executed the Client Thread
//
public class Client {
    public static void main(String[] args) {
        ClientThread client = new ClientThread(args);
        client.start();
    }
}