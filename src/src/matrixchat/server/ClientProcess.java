/**
 * @author Morten Beuchert
 */

package matrixchat.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;

public class ClientProcess extends Thread {
	private Socket clientSocket;
	private ChatServer cs;
	private boolean running;
	private String username;

	public ClientProcess(Socket socket, ChatServer cs) {
		clientSocket = socket;
		this.cs = cs;
		running = true;
		username = "Guest";
	}

	public void run() {
		System.out.println("Connection from: " + clientSocket.getInetAddress().getHostName());
		
		try {
			// Create Buffered
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

			// Create Printwriter
			PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			
			while (true) {
				String fromClient = in.readLine();
				
				if (fromClient == null || !clientSocket.isConnected()) {
					System.out.println("Lost connection to client");
					break;
				}

				int clientCmd = Character.getNumericValue(fromClient.charAt(0));
				String clientMsg = fromClient.substring(1);

				// client cmd 0=chat, 1=add username, 2=username too short
				switch (clientCmd) {
				case 0:
					System.out.println(username + " says: " + clientMsg);
					cs.tellAll(username, "0" + username + ": " + clientMsg);
					break;
				case 1:
					username = clientMsg;
					
					if (username.length() > 15) {
						out.println("2" + "Maximum length of username is 15 characters. Disconnecting...");
						out.flush();
						clientSocket.close();
						cs.removeSocket(clientSocket);	
					} else {
						boolean added = cs.addUser(clientMsg);
						if (!added) {
							out.println("2"	+ "Username already in use. Disconnecting...");
							out.flush();
							clientSocket.close();
							cs.removeSocket(clientSocket);
						} else {
							cs.tellAll(username, "0" + username + " entered the matrix.");
							cs.tellAll(username, "1" + cs.getUsers().toString());
						}
						break;
					}
				}

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					System.out.println("Client timed out...");
					out.close();
					in.close();
					
					cs.removeUser(username);
					cs.tellAll(username, "1" + cs.getUsers().toString());
					cs.tellAll(username, "0" + username + " escaped the matrix.");
					clientSocket.close();
					cs.removeSocket(clientSocket);					
				}
			}

			out.close();
			in.close();
			
			cs.removeUser(username);
			cs.tellAll(username, "1" + cs.getUsers().toString());
			cs.tellAll(username, "0" + username + " escaped the matrix.");
			clientSocket.close();
			cs.removeSocket(clientSocket);
		} catch (IOException e) {
		}
	}
}