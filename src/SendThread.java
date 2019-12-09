// thread that sends messages to the multicast group
//
// libs
import java.net.DatagramPacket;
import java.util.Scanner;

class SendThread extends Thread {
    // attributes
    private MulticastGroup ms;
    private String id;

    // constructor
    public SendThread(MulticastGroup ms, String id) {
        this.ms = ms;
        this.id = id;
    }

    @Override
    public void run() {
        try{
            System.out.println("Iniciando SendThread");
            DatagramPacket outgoing;
            String msg, tag;
            byte[] data;
            boolean send = false;
            Scanner read = new Scanner(System.in);

            while(true){
                msg = read.nextLine();

                tag = msg.split("-")[0];
                tag = tag.toUpperCase();

                switch (tag) {
                    case "CE":
                    case "CG":
                        send = true;
                    break;
                }

                msg = msg + "&" + id;

                if (send) {
                    data = msg.getBytes();
                    outgoing = new DatagramPacket(data, data.length, ms.getGroupAddrs(), ms.getPort());
                    ms.getSocket().send(outgoing);
                    send = false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
};