package fr.skybeastmc.socketmessenger.bungeecord;

import java.io.ByteArrayInputStream;

import net.md_5.bungee.api.config.ServerInfo;

/**
 * Api for sending/requesting data trough sockets.
 */
public class SocketAPI {

    /**
     * Sends a Data object to the specified server on a specified channel.
     *
     * Fails silently if server is offline.
     *
     * @param server - The server to send the Data to
     * @param channel - The channel to send the Data to
     * @param data - The Data to send
     */
    public static void sendDataToClient(ServerInfo server, String channel, ByteArrayInputStream data){
        SocketManager.getConnectedSockets().get(server.getName()).sendCommand(Command.SEND_DATA, channel, data);
    }
    
    /**
     * Sends a Data object to the specified server on a specified channel.
     *
     * Fails silently if server is offline.
     *
     * @param server - The server to send the Data to
     * @param channel - The channel to send the Data to
     * @param data - The Data to send
     */
    public static void sendDataToClient(ServerInfo server, String channel, byte[] data){
        SocketManager.getConnectedSockets().get(server.getName()).sendCommand(Command.SEND_DATA, channel, data);
    }

}
