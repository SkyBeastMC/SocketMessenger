package fr.skybeastmc.socketmessenger.bukkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.SocketException;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.skybeastmc.socketmessenger.bukkit.api.ReceivedDataEvent;

public class SocketManager {

	private static Socket socket;
	private static boolean valid = false;
	private static DataOutputStream out;
	private static DataInputStream in;
	private static int port = -1;
	private static String address = "localhost";
	private static int tried = 0;

	public static void init(String a, int p) {
		address = a;
		port = p;

		if (tried == 10) {
			Debug.info("Reconnection attempts stopped after " + tried
					+ " fails.");
			return;
		}

		try {
			Debug.info("Trying to connect on port " + port + "! (" + tried
					+ " fails)");
			Socket socket = new Socket(Inet4Address.getByName(address), port);
			Thread thread = new Thread(() -> {
				initSocket(socket);
			});
			thread.start();
			tried = 0;
			Debug.info("Connected.");
		} catch (Exception e) {
			Debug.info("Cannot connect to " + address + ":" + port + ".", e
					.getClass().getName() + ": " + e.getMessage());
			Debug.info("Trying to reconnect in 5 seconds!");
			new BukkitRunnable() {
				public void run() {
					init(address, port);
				}
			}.runTaskLater(Main.getPlugin(), 5 * 20);
			tried++;
		}
	}

	public static void end(boolean shutdown, boolean reconnect) {
		sendCommand(Command.EXIT);
		try {
			if (!socket.isClosed())
				socket.close();
			valid = false;
			Debug.info("Socket closed!");
			if (!shutdown && reconnect) {
				Debug.info("Trying to reconnect in 5 seconds!");
				new BukkitRunnable() {
					public void run() {
						init(address, port);
					}
				}.runTaskLater(Main.getPlugin(), 5 * 20);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initSocket(Socket s) {
		socket = s;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		valid = true;
		sendCommand(Command.IDENTIFY);
		while (!socket.isClosed() && valid) {
			try {
				Command command = Command.get(in.readByte());

				Debug.info("Server: " + command.name());

				switch (command) {
				case EXIT:
					Debug.info("Server sent end command!");
					end(false, false);
					break;
				case SEND_DATA:
					String channel = in.readUTF();
					Debug.info("Received data from Server! Channel: '"
							+ channel + "'!");
					byte[] array = new byte[in.available()];
					in.readFully(array);
					ByteArrayDataInput byteArrayIn = ByteStreams
							.newDataInput(array);

					Bukkit.getPluginManager().callEvent(
							new ReceivedDataEvent(byteArrayIn, channel));
					break;
				case RECONNECT:
					Debug.info("Server sent reconnect command! Reconnecting in 5 second!");
					new BukkitRunnable() {
						public void run() {
							end(false, true);
						}
					}.runTaskLater(Main.getPlugin(), 20 * 5);
					break;
				case BROADCAST:
					break;
				case CONNECT:
					break;
				case FORWARD_DATA:
					break;
				case GET_SERVER:
					break;
				case GET_SERVERS:
					break;
				case IDENTIFY:
					break;
				case KICK_PLAYER:
					break;
				case MESSAGE:
					break;
				case PLAYER_COUNT:
					break;
				case PLAYER_LIST:
					break;
				default:
					break;
				}
			} catch (SocketException | EOFException e) {
				if (socket.isClosed())
					return;
				Debug.info("Disconnected from the server. Reconnecting in 5 seconds. "
						+ e.getClass().getName() + ": " + e.getMessage());
				new BukkitRunnable() {
					public void run() {
						init(address, port);
					}
				}.runTaskLater(Main.getPlugin(), 5 * 20);
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendCommand(Command command, Object... data) {
		Debug.info(command.name());
		if (!valid)
			return;
		try {
			switch (command) {
			case EXIT:
				out.writeByte(command.getByte());
				out.flush();
				break;
			case IDENTIFY:
				Debug.info("Identifying to server with port "
						+ Bukkit.getPort() + "!");
				out.writeByte(command.getByte());
				out.writeInt(Bukkit.getPort());
				out.flush();
				break;
			case BROADCAST:
				if (data.length < 1)
					throw new RuntimeException(
							"Not enough data provided for BROADCAST command!");
				if (!(data[0] instanceof String))
					throw new RuntimeException(
							"1st object for BROADCAST is not of type String!");

				String msg = ((String) data[0]);
				out.writeByte(command.getByte());
				out.writeUTF(msg);
				out.flush();
				break;
			case SEND_DATA:
				if (data.length < 2)
					throw new RuntimeException(
							"Not enough data provided for SEND_DATA command!");
				if (!(data[0] instanceof String))
					throw new RuntimeException(
							"1st object for SEND_DATA is not of type String!");
				byte[] array;
				if (data[1] instanceof ByteArrayDataOutput) {
					array = ((ByteArrayDataOutput) data[1]).toByteArray();
				} else if (data[1] instanceof byte[]) {
					array = (byte[]) data[1];
				} else {
					throw new RuntimeException(
							"2st object for SEND_DATA is neither type of ByteArrayDataOutput nor byte array!");
				}

				String channel = ((String) data[0]);
				out.writeByte(command.getByte());
				out.writeUTF(channel);
				out.write(array);
				out.flush();
				break;
			case FORWARD_DATA:
				if (data.length < 2)
					throw new RuntimeException(
							"Not enough data provided for FORWARD_DATA command!");
				if (!(data[0] instanceof String))
					throw new RuntimeException(
							"1st object for FORWARD_DATA is not of type String!");
				byte[] a;
				if (data[2] instanceof ByteArrayDataOutput) {
					a = ((ByteArrayDataOutput) data[1]).toByteArray();
				} else if (data[2] instanceof byte[]) {
					a = (byte[]) data[2];
				} else {
					throw new RuntimeException(
							"2st object for SEND_DATA is neither type of ByteArrayDataOutput nor byte array!");
				}

				String serv = ((String) data[0]);
				out.writeByte(command.getByte());
				out.writeUTF(serv);
				out.write(a);
				out.flush();
				break;
			case CONNECT:
				if (data.length < 1)
					throw new RuntimeException(
							"Not enough data provided for CONNECT command!");
				if (!(data[0] instanceof String))
					throw new RuntimeException(
							"1st object for CONNECT is not of type String!");
				if (!(data[1] instanceof String))
					throw new RuntimeException(
							"2nd object for CONNECT is not of type String!");

				String p = ((String) data[0]);
				String server = ((String) data[1]);
				out.writeByte(command.getByte());
				out.writeUTF(p);
				out.writeUTF(server);
				out.flush();
				break;
			case MESSAGE:
				if (data.length < 1)
					throw new RuntimeException(
							"Not enough data provided for MESSAGE command!");
				if (!(data[0] instanceof String))
					throw new RuntimeException(
							"1st object for MESSAGE is not of type String!");
				if (!(data[1] instanceof String))
					throw new RuntimeException(
							"2nd object for MESSAGE is not of type String!");

				String pl = ((String) data[0]);
				String m = ((String) data[1]);
				out.writeByte(command.getByte());
				out.writeUTF(pl);
				out.writeUTF(m);
				out.flush();
				break;
			case KICK_PLAYER:
				if (data.length < 1)
					throw new RuntimeException(
							"Not enough data provided for KICK_PLAYER command!");
				if (!(data[0] instanceof String))
					throw new RuntimeException(
							"1st object for KICK_PLAYER is not of type String!");
				if (!(data[1] instanceof String))
					throw new RuntimeException(
							"2nd object for KICK_PLAYER is not of type String!");

				String player = ((String) data[0]);
				String message = ((String) data[1]);
				out.writeByte(command.getByte());
				out.writeUTF(player);
				out.writeUTF(message);
				out.flush();
				break;
			case GET_SERVER:
			case GET_SERVERS:
			case PLAYER_COUNT:
			case PLAYER_LIST:
				out.writeByte(command.getByte());
				out.flush();
				break;
			default:
				throw new RuntimeException("Unknown command!");
			}
		} catch (IOException e) {
			Debug.info("Cannot send message to " + address + ":" + port + ".",
					e.getClass().getName() + ": " + e.getMessage());
			Debug.info("Trying to disconnect/reconnect in 5 seconds.");

			new BukkitRunnable() {
				public void run() {
					end(false, true);
				}
			}.runTaskLater(Main.getPlugin(), 5 * 20);
		}
	}
}
