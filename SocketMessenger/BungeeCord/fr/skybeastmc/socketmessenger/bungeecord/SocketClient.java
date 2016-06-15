package fr.skybeastmc.socketmessenger.bungeecord;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.skybeastmc.socketmessenger.bungeecord.api.ReceivedDataEvent;

public class SocketClient {

	private final Socket socket;
	private String name = "";
	private final int id;
	private int serverPort;
	private boolean identified = false;
	private DataInputStream in;
	private DataOutputStream out;

	SocketClient(Socket socket) {
		this.socket = socket;
		this.id = SocketManager.getLastID();
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataReceiveListener();
	}

	private void dataReceiveListener() {
		while (!socket.isClosed()) {
			try {
				byte cmd;
				try {
					cmd = in.readByte();
				} catch (EOFException e) {
					Debug.info(fullName() + " disconnected. "
							+ e.getClass().getName() + ": " + e.getMessage());
					close();
					return;
				} catch (SocketException e) {
					if (!socket.isClosed()) {
						close();
					}
					return;
				}
				Command command = Command.get(cmd);

				switch (command) {
				case EXIT:
					if (!identified)
						continue;
					Debug.info(fullName() + " sent end command!");
					end(false);
					break;
				case IDENTIFY:
					this.serverPort = in.readInt();
					BungeeCord
							.getInstance()
							.getServers()
							.values()
							.stream()
							.filter(info -> info.getAddress().getPort() == serverPort)
							.forEach(info -> name = info.getName());
					if (name.equals("")) {
						name = "Undefined";
					}
					Debug.info("Socket " + id + "'s origin is '" + name + "'!");
					if (SocketManager.getConnectedSockets().containsKey(name)) {
						Debug.info("An open socket from " + name
								+ " already exists! Disconnecting "
								+ fullName() + "!");
						end(false);
						return;
					}
					identified = true;
					SocketManager.getConnectedSockets().put(name, this);
					break;
				case BROADCAST:
					if (!identified)
						continue;
					if (!identified)
						return;
					String message = in.readUTF();
					Debug.info("Incoming broadcast from " + fullName() + "! '"
							+ message + "'!");
					BungeeCord.getInstance().broadcast(message);
					break;
				case SEND_DATA:
					if (!identified)
						continue;
					if (!identified)
						return;
					String channel = in.readUTF();
					byte[] array = new byte[in.available()];
					in.read(array);
					Debug.info("Received data from " + fullName()
							+ "! Channel: '" + channel + "'!");
					BungeeCord
							.getInstance()
							.getPluginManager()
							.callEvent(
									new ReceivedDataEvent(ByteStreams
											.newDataInput(array), name, channel));
					break;
				case FORWARD_DATA:
					if (!identified)
						continue;
					String s = in.readUTF();
					byte[] ar = new byte[in.available()];
					in.read(ar);
					SocketClient client = SocketManager.getConnectedSockets()
							.get(s);
					if (client == null)
						return;
					client.sendCommand(Command.FORWARD_DATA, ar);
					break;
				case CONNECT:
					if (!identified)
						continue;
					String p = in.readUTF();
					String server = in.readUTF();
					if (BungeeCord.getInstance().getPlayer(p) == null)
						return;
					if (BungeeCord.getInstance().getServerInfo(server) == null)
						return;
					BungeeCord
							.getInstance()
							.getPlayer(p)
							.connect(
									BungeeCord.getInstance().getServerInfo(
											server));
					break;
				case PLAYER_COUNT:
					if (!identified)
						continue;
					break;
				case PLAYER_LIST:
					if (!identified)
						continue;
					break;
				case GET_SERVERS:
					if (!identified)
						continue;
					break;
				case MESSAGE:
					if (!identified)
						continue;
					String player = in.readUTF();
					String msg = in.readUTF();
					if (BungeeCord.getInstance().getPlayer(player) == null)
						return;
					BungeeCord.getInstance().getPlayer(player)
							.sendMessage(new TextComponent(msg));
					break;
				case GET_SERVER:
					if (!identified)
						continue;
					break;
				case KICK_PLAYER:
					if (!identified)
						continue;
					String pl = in.readUTF();
					String m = in.readUTF();
					if (BungeeCord.getInstance().getPlayer(pl) == null)
						return;
					BungeeCord.getInstance().getPlayer(pl)
							.disconnect(new TextComponent(m));
					break;
				case RECONNECT:
					if (!identified)
						continue;
					break;
				default:
					if (!identified)
						continue;
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void end(boolean reconnect) {
		try {
			if (reconnect) {
				sendCommand(Command.RECONNECT);
			} else {
				sendCommand(Command.EXIT);
			}
			close();
			Debug.info(fullName() + " disconnected!");
			SocketManager.getConnectedSockets().remove(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() throws IOException {
		if (!socket.isClosed())
			socket.close();
		SocketManager.decrementeID();
	}

	public void sendCommand(Command command, Object... data) {
		if (!identified)
			return;
		try {
			switch (command) {
			case EXIT:
				out.writeByte(command.getByte());
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
			case RECONNECT:
				break;
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String fullName() {
		return getFullName();
	}

	public String getFullName() {
		return id + ":" + name;
	}

	public boolean isIdentified() {
		return identified;
	}

	public void setIdentified(boolean identified) {
		this.identified = identified;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getName() {
		return name;
	}

	public String setName(String name) {
		this.name = name;
		return name;
	}

	public int getId() {
		return id;
	}

	public Socket getSocket() {
		return socket;
	}

}
