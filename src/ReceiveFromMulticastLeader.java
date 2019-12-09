// thread thats receive message from multicast leader
//
// libs
import java.util.Scanner;

public class ReceiveFromMulticastLeader extends Thread {
	// atributos
	String msg;
	Scanner in;

	public ReceiveFromMulticastLeader(Scanner in) {
		this.in = in;
	}

	@Override
	public void run() {
		while (true) {
		 	while(in.hasNextLine()) {
				msg = in.nextLine();
				System.out.println("Recebido: " + msg);
			}
		}
	}
}