// class that starts the external process, connects with the leader and starts the communication threads
//
// libs
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ExternalProcess {
	public static void main(String[] args) {
		String hostName = "localhost";
		int portNumber = 7000;

		if (args.length == 1)
			hostName = args[0];

		try {
			// connects to the leader server socket
			Socket echoSocket = new Socket(hostName, portNumber);

			PrintStream out = new PrintStream(echoSocket.getOutputStream()); // send obj
			Scanner in = new Scanner(echoSocket.getInputStream()); // receive obj

			SendToMulticastLeader stml = new SendToMulticastLeader(out);
			ReceiveFromMulticastLeader rfml = new ReceiveFromMulticastLeader(in);

			stml.start();
			rfml.start();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + hostName);
		} 
	}
}
