/**
 * @author Morten Beuchert
 */

package matrixchat.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ChatServer {
	private ArrayList<Socket> connections = new ArrayList<>();
	private ArrayList<String> users = new ArrayList<>();;

	public synchronized boolean addUser(String user) {
		for (String u : users) {
			if (u.equals(user)) {
				return false;
			}
		}
		users.add(user);
		return true;
	}
	
	public synchronized void removeUser(String user) {
		for (int i=0; i<users.size(); i++) {
			if (users.get(i).equals(user)) {
				users.remove(i);				
			}
		}
	}
	
	public synchronized void removeSocket(Socket s) {
		for (int i=0; i<connections.size(); i++) {
			if (connections.get(i) == s) {
				connections.remove(i);				
			}
		}
	}
	
	public synchronized ArrayList<String> getUsers() {
		return users;
	}
	
	public void tellAll(String me, String s) {
		for (int i=0; i<connections.size(); i++) {
			Socket socket = connections.get(i);
			try {
				PrintWriter out = new PrintWriter( new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true );
				out.println(s);
				boolean connectionError = out.checkError();
				if (connectionError) {
					System.out.println("Connection error. Disconnected: " + me);
					out.close();
					socket.close();
					removeUser(me);
					continue;
				}
				out.flush();
			} catch (IOException e) {
				//e.printStackTrace();
			}	
		}
	}

	public void run() throws Exception {
		System.out.println("Server listening on port 7777");
		ServerSocket welcomingSocket = new ServerSocket(7777);

		while (true) {
			Socket socket = welcomingSocket.accept();
			// Add the new connection to ArrayList
			connections.add(socket);
			
			// Create a new ClientProcess and run it
			Thread process = new Thread(new ClientProcess(socket, this));
			process.start();			
		}
	}
}