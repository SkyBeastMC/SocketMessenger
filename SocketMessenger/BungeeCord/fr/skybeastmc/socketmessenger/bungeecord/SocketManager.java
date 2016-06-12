package fr.skybeastmc.socketmessenger.bungeecord;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import net.md_5.bungee.BungeeCord;

public class SocketManager {

	private static final Map<String, SocketClient> connectedSockets = new HashMap<>();
	private static int id = -1;
	private static ServerSocket serverSocket;

	static void init(int port) {
		try {
			Debug.info("Listening for socket connections on port " + port + "!");
			serverSocket = new ServerSocket(port);
			@SuppressWarnings("deprecation")
			ExecutorService service = BungeeCord.getInstance()
					.getPluginManager().getPlugin("SocketMessenger")
					.getExecutorService();
			service.submit(() -> {
				while (!serverSocket.isClosed()) {
					try {
						Socket socket = serverSocket.accept();
						service.submit(() -> initSocket(socket));
					} catch (IOException e) {
						if (e.getMessage().toLowerCase()
								.contains("socket closed"))
							return;
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void end(boolean reconnect) {
		try {
			if (!serverSocket.isClosed())
				serverSocket.close();
			
			Iterator<SocketClient> iter = connectedSockets.values().iterator();
			
			while(iter.hasNext()) {
				iter.next().end(reconnect);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initSocket(Socket socket) {
		id++;
		Debug.info("Socket connected! ID: " + id);
		new SocketClient(socket);
	}
	
	public static int getID() {
		return id;
	}
	
	public static Map<String, SocketClient> getConnectedSockets() {
		return connectedSockets;
	}
}
