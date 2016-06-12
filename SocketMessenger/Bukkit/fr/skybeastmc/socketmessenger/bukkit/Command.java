package fr.skybeastmc.socketmessenger.bukkit;

public enum Command {
	EXIT((byte) 0), IDENTIFY((byte) 127), RECONNECT((byte) 128), BROADCAST(
			(byte) 1), SEND_DATA((byte) 2), FORWARD_DATA((byte) 3), CONNECT(
			(byte) 4), PLAYER_COUNT((byte) 5), PLAYER_LIST((byte) 6), GET_SERVERS(
			(byte) 7), MESSAGE((byte) 8), GET_SERVER((byte) 9), KICK_PLAYER(
			(byte) 10);

	private byte id;

	Command(byte id) {
		this.id = (byte) id;
	}

	public static Command get(byte command) {
		for (Command c : Command.values())
			if (c.getByte() == command)
				return c;
		return null;
	}

	public byte getByte() {
		return id;
	}
}