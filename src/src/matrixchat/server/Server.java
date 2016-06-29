/**
 * @author Morten Beuchert
 */

package matrixchat.server;

public class Server {
	public static void main(String[] args) {
		try {
			new ChatServer().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}