// Class that implements the client methods, if necessary instantiates the leader 
//
// libs
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.SocketException;

public class ClientThread extends Thread {
    // attributes
    protected int defaultPortR = 5000; // multicast receive and send ports
    protected String defaultHostname = "224.4.4.4";
    protected int bufferLength = 8192;

    private boolean leader = false;
    private LeaderThread leaderThread;
    private String hostname;
    private int portR;
    private String id;

    // constructor
    public ClientThread(String[] args) {
        if (args.length == 3) {
            int pos = 0;
            hostname = args[pos++];
            portR = Integer.parseInt(args[pos++]);
        } else {
            hostname = defaultHostname;
            portR = defaultPortR;
        }
    }

    // check if it's the first process in the group
    private boolean First(MulticastGroup ms) throws SocketException {
        try {
            DatagramPacket incoming = new DatagramPacket(new byte[bufferLength], bufferLength);
            ms.getSocket().setSoTimeout(5000); // wait for 5 sec.
            ms.getSocket().receive(incoming);
            ms.getSocket().setSoTimeout(0);
            return false;
        } catch (IOException e) { // receive nothing within the timeout
            System.out.println("... Multicast vazio, criando lider");
            leader = true; // mark as leader
            ms.getSocket().setSoTimeout(0);
            return true;
        }
    }

    // sets the client id based on process id and machine name
    private void SetClientID() {
        id = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println("... Meu Id: " + id);
    }

    @Override
	public void run() {
        try {            
            // creates the multicast socket
            MulticastGroup ms = new MulticastGroup(portR,hostname);
            System.out.println("... Escutando a porta " + ms.getPort());
            
            System.out.println("ClientThread ...");
            SetClientID();

            // if it's first in the group, starts the leader thread
            if (First(ms)) {
                leaderThread = new LeaderThread(ms, id);
                leaderThread.start();
            }
            
            // starts the send/receive threads
            if (!leader) {
                SendThread sendth = new SendThread(ms, id);
                ReceiveThread receiveth = new ReceiveThread(ms, leader, id);
                sendth.start();
                receiveth.start();
            }
        } catch (SocketException e) {
            System.err.println(e);
        }
    }
}