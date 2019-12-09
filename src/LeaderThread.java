// Class that implements a Leader's methods
//
// libs
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LeaderThread extends Thread {
  // attributes
  private LeaderServerSocket lss;
  private int portLSS = 7000;
  private MulticastGroup ms;
  private String msg;
  private byte[] data;
  private DatagramPacket outgoing;
  private String id;
  private boolean alive;

  // constructor
  public LeaderThread(MulticastGroup ms, String id) {
    this.ms = ms;
    this.id = id;
    this.alive = true;
  }

  // creates leader ss with external
  private void CreateLeaderServerSocket() {
    lss = new LeaderServerSocket(portLSS, ms, id);
    lss.start();
  }

  // sends leader message to the multicast
  private void SendMessage(String msg) {
    try {
      data = msg.getBytes();
      outgoing = new DatagramPacket(data, data.length, ms.getGroupAddrs(), ms.getPort());
      ms.getSocket().send(outgoing);
    } catch (IOException e) {
      e.printStackTrace();
      if (e.getMessage().equals("A rede está fora de alcance (sendto failed)")) {
        this.alive = false;
      }
    }
  }

  private void ImAlive() {
    msg = "LG- meu id: " + id;
    SendMessage(msg);
  }

  @Override
  public void run() {
    try {
      CreateLeaderServerSocket();

      while (alive) {
        ImAlive();
        Thread.sleep(3000);
      }

      while (!alive) {
        try {
          System.out.println(InetAddress.getByName("www.google.com"));
          alive = true;
        } catch (UnknownHostException e) {
          alive = false;
        }
      }

      if (alive) {
        ClientThread c = new ClientThread(new String[1]);
        c.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}