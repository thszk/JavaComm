// thread thats send messages to the multicast leader
//
// libs
import java.util.Scanner;
import java.io.PrintStream;

public class SendToMulticastLeader extends Thread {
	// attributes
	String msg;
	Scanner read;
	PrintStream out;

	public SendToMulticastLeader(PrintStream out) {
		this.read = new Scanner(System.in);
		this.out = out;
	}

	@Override
	public void run() {
		System.out.println("Pronto para enviar mensagens ao líder do Multicast...");
		while (true) {
			msg = read.nextLine();
			out.println(msg);
			System.out.println("Enviado: " + msg);
		}
	}
}	