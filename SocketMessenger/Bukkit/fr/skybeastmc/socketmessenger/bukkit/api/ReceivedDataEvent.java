package fr.skybeastmc.socketmessenger.bukkit.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.google.common.io.ByteArrayDataInput;

/**
 * Called when Data is received on a specific channel trough the socket.
 */
public class ReceivedDataEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final ByteArrayDataInput data;
	private final String channel;

	public ReceivedDataEvent(ByteArrayDataInput data, String channel) {
		this.data = data;
		this.channel = channel;
	}

	/**
	 * Gets the Data received.
	 *
	 * @return The Data received.
	 */
	public ByteArrayDataInput getData() {
		return data;
	}

	/**
	 * Gets the channel trough which Data was received.
	 * 
	 * @return The channel which Data was received trough.
	 */
	public String getChannel() {
		return channel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
