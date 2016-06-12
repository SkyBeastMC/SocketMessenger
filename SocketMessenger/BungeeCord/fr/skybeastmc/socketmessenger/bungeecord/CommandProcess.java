package fr.skybeastmc.socketmessenger.bungeecord;

import java.io.IOException;
import java.io.ObjectInputStream;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import fr.skybeastmc.socketmessenger.Command;
import fr.skybeastmc.socketmessenger.Data;

public class CommandProcess {
	
	CommandProcess(ObjectInputStream in, SocketClient client) throws IOException {
    	byte a = in.readByte();
        Command command = Command.get(a);
		
        switch(command) {
		case EXIT:
            Util.log(client.getFullName()+" sent end command!");
            client.end();
			break;
		case IDENTIFY:
            client.setServerPort(in.readInt());
            for(ServerInfo info : BungeeCord.getInstance().getServers().values()) {
            	if(info.getAddress().getPort() == client.getServerPort()) {
            		client.setName(info.getName());
            		break;
            	}
            }
            if(client.getName().equals("")){
            	client.setName("Undefined");
            }
            Util.log("Socket "+client.getId()+"'s origin is '"+client.getName()+"'!");
            if(SocketManager.connectedSockets.containsKey(client.getName())){
                Util.log("An open socket from "+client.getName()+" already exists! Disconnecting "+client.getFullName()+"!");
                client.end();
                return;
            }
            client.setIdentified(true);
            SocketManager.connectedSockets.put(client.getName(), client);
			break;
		case BROADCAST:
            if(!client.isIdentified()) return;
            String message = in.readUTF();
            Util.log("Incoming broadcast from "+client.getFullName()+"! '"+message+"'!");
            BungeeCord.getInstance().broadcast(message);
			break;
		case CONNECT:
            String p = in.readUTF();
            String server = in.readUTF();
            if(BungeeCord.getInstance().getPlayer(p) == null) return;
            if(BungeeCord.getInstance().getServerInfo(server) == null) return;
            BungeeCord.getInstance().getPlayer(p).connect(BungeeCord.getInstance().getServerInfo(server));
            break;
		case FORWARD_DATA:
			break;
		case GET_SERVER:
			break;
		case GET_SERVERS:
			break;
		case KICK_PLAYER:
			break;
		case MESSAGE:
			break;
		case PLAYER_COUNT:
            Data player_count = new Data();
            player_count.addInt("playerCount", BungeeCord.getInstance().getOnlineCount());
            client.sendCommand(Command.SEND_DATA, "PlayerCount", player_count);
			break;
		case PLAYER_LIST:
			break;
		case SEND_DATA:
			break;
		case STOP_ALL:
			break;
		case STOP_SERVERS:
			break;
        }
	}
	
}
