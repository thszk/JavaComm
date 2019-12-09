// thread thats receive messages from multicast
//
// libs
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

class ReceiveThread extends Thread {
    // attributes
    protected int bufferLength = 8192;

    private MulticastGroup ms;
    private boolean leader;
    private String id;
    private DatagramPacket incoming;

    private BufferedReader in;
    private PrintStream out;

    private String msg, exMsg;
    private String[] tag;    
    private int lastTimeReceivedFromLeader, winner, count;
    private boolean condition, electionParticipant;

    // construtor
    public ReceiveThread(MulticastGroup ms, boolean leader, String id) {
        this.ms = ms;
        this.leader = leader;
        this.id = id;
        incoming = new DatagramPacket(new byte[bufferLength], bufferLength);
        in = null;
        out = null;
        lastTimeReceivedFromLeader = 0;
        condition = true;
        electionParticipant = false;
        winner = 0;
        count = 0;
    }

    public void setIn(BufferedReader in) { this.in = in; }

    public void setOut(PrintStream out) { this.out = out; }

    public void sendToMulticast(String msg) {
        try {
            byte[] data;
            DatagramPacket outgoing;

            data = msg.getBytes();
            outgoing = new DatagramPacket(data, data.length, ms.getGroupAddrs(), ms.getPort());
            
            ms.getSocket().send(outgoing);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToExternal(String msg) {
        out.println(msg);
    }

    public void election() {
        if ((electionParticipant && winner > 0) || !electionParticipant) {
            electionParticipant = true;
            sendToMulticast("E- [" + id + "]: " + id);
        }
    }

    public void verifyElection(String receivedId) {
        int received = Integer.parseInt(receivedId.split(":")[1].split("@")[0].split(" ")[1]);
        int my = Integer.parseInt(id.split("@")[0]);

        if (winner != -1) {
            if (my > received) {
                winner++;
                count++;
                if (!electionParticipant) {
                    electionParticipant = true;
                    election();
                }
            }
            else if (my < received) {
                winner = -1; // loser
                count++;
            }
            else if (my == received) {
                if (count == 0) {
                    newLeader();
                }
                else {
                    if (winner == count && winner != 1) {
                        newLeader();
                    }
                }
            }
        }
    }

    public void newLeader() {
        ClientThread c = new ClientThread(new String[1]);
        condition = false;
        lastTimeReceivedFromLeader = 0;
        winner = 0;
        count = 0;
        c.start();
    }

    @Override
    public void run() {
        try{
            System.out.println("Iniciando ReceiveThread");
            while(condition){
                // is leader and have connection with the external
                if (leader && in != null) {
                    if(in.ready()) { // has message
                        exMsg = in.readLine();
                        exMsg = "EG- " + exMsg;
                        sendToMulticast(exMsg); // send to multicast
                    }
                }

                try { // reveive from multicast
                    lastTimeReceivedFromLeader += 1;

                    ms.getSocket().setSoTimeout(2000); // wait 2sec.
                    ms.getSocket().receive(incoming); 
                    ms.getSocket().setSoTimeout(0);

                    msg = new String(incoming.getData(), 0, incoming.getLength());
                    tag = msg.split("-");
                    tag[0] = tag[0].toUpperCase();

                    switch (tag[0]) {
                        case "LG":
                            System.out.println("[LIDER]: " + tag[1]);
                            lastTimeReceivedFromLeader = 0;
                            electionParticipant = false;
                            winner = 0;
                        break;

                        case "EG":
                            System.out.println("[EXTERNO]: " + tag[1]);
                        break;

                        case "CG":
                            tag = tag[1].split("&");
                            System.out.println("[" + tag[1] + "]:" + tag[0]);
                        break;

                        case "CE":
                            tag = tag[1].split("&");
                            System.out.println("[" + tag[1] + "] p/ EXTERNO:" + tag[0]);
                            
                            if (leader && out != null)
                                sendToExternal(tag[0]);
                        break;

                        case "E":
                            System.out.println("[ELEIÇÃO]: " + tag[1]);
                            verifyElection(tag[1]);
                        break;
                    }
                } 
                catch (SocketTimeoutException e) {
                    ms.getSocket().setSoTimeout(0);

                    if (!leader && lastTimeReceivedFromLeader >= 4) {
                        election();
                    } else {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
};