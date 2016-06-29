/**
 * @author Morten Beuchert
 */
package matrixchat.client;

import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class ChatClient {
	private ClientGUI gui;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String username;
	private boolean running;
	private Thread receiver;
	
	public ChatClient(ClientGUI gui) {
		this.gui = gui;
		socket = null;
		in = null;	
		out = null;
		username = "Guest";
		receiver = null;
		running = true;
	}
	
	public void connect(String hostname) {
		int port = 7777;
		running = true;
		
		try {
			socket = new Socket(hostname, port);
			out = new PrintWriter( new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true );
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		receiver = new Thread(new Receiver());
		receiver.start();
	}
	
	public void disconnect() {
		try {
			running = false;
			socket.close();
			System.out.println("Disconnected from server");
			receiver = null;
		} catch (IOException e) {
		}
	}
	
	public void sendToServer(int type, String msg) {
		out.println(type + msg);
		out.flush();		
	}
	
	// Begin inner class
	public class Receiver implements Runnable {		
		public void run() {
			while (running) {
				String fromServer;
				try {
					// Read message from server (if there is one) and print it to console
					while ((fromServer = in.readLine()) != null) {
						// Check the cmd (0=chat, 1=update usernames)
						int cmd = Character.getNumericValue(fromServer.charAt(0));
						
						String message = fromServer.substring(1);
						switch (cmd) {
						case 0: gui.appendToChat(message);
						break;
						case 1: gui.updateUsersOnline(message);
						break;
						case 2: gui.restart(message);
						break;
					}
					}
				} catch (IOException e) {
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	} // End inner class
}