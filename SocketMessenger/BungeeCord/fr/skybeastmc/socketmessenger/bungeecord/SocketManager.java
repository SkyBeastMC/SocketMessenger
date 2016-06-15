package fr.skybeastmc.socketmessenger.bungeecord;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.md_5.bungee.api.ProxyServer;

public class SocketManager {

	private static final Map<String, SocketClient> connectedSockets = new HashMap<>();
	private static int id = -1;
	private static ServerSocket serverSocket;

	public static void init(int port) {
		try {
			Debug.info("Listening for socket connections on port " + port + "!");
			serverSocket = new ServerSocket(port);
			ProxyServer.getInstance().getScheduler()
					.runAsync(Main.getPlugin(), new Runnable() {

						public void run() {

							while (!serverSocket.isClosed()) {
								try {
									Socket socket = serverSocket.accept();
									ProxyServer
											.getInstance()
											.getScheduler()
											.runAsync(Main.getPlugin(),
													new Runnable() {

														public void run() {

															initSocket(socket);
														}
													});
								} catch (IOException e) {
									if (e.getMessage().toLowerCase()
											.contains("socket closed"))
										return;
									e.printStackTrace();
								}
							}
						}

					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void end(boolean reconnect) {
		try {

			Iterator<SocketClient> iter = connectedSockets.values().iterator();

			while (iter.hasNext()) {
				SocketClient next = iter.next();
				next.end(reconnect);
			}

			if (!serverSocket.isClosed())
				serverSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initSocket(Socket socket) {
		id++;
		Debug.info("Socket connected! ID: " + id);
		new SocketClient(socket);
	}

	public static int getLastID() {
		return id;
	}

	public static void decrementeID() {
		id--;
	}

	public static Map<String, SocketClient> getConnectedSockets() {
		return connectedSockets;
	}
}
