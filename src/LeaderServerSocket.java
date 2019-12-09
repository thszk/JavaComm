// Leader's server socket class where it will communicate with the external process
//
// libs
import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class LeaderServerSocket extends Thread {
	// attributes
    private int portNumber;
    private MulticastGroup ms;
    private String id;
	private ServerSocket serverSocket;
	private Socket clientSocket;

	// constructor
	public LeaderServerSocket(int portNumber, MulticastGroup ms, String id) {
        this.portNumber = portNumber;
        this.ms = ms;
        this.id = id;
    }
    
	@Override
	public void run() {
        try {
        	System.out.println("Líder Criando Server Socket na porta: " + portNumber);  
            serverSocket = new ServerSocket(portNumber); // create ss
            
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("ServerSocket IP Address: " + inetAddress.getHostAddress()); // show ip address

            ReceiveThread receiveth = new ReceiveThread(ms, true, id);
            SendThread sendth = new SendThread(ms, id);
            receiveth.start();
            sendth.start();

            clientSocket = serverSocket.accept(); // waits until the external process makes connection
            System.out.println("Processo externo conectado ao Server Socket do Líder");

            // update communication objects
            receiveth.setIn(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
            receiveth.setOut(new PrintStream(clientSocket.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}