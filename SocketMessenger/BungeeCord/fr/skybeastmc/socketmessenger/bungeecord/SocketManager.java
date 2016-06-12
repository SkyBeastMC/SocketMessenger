package fr.skybeastmc.socketmessenger.bungeecord;

import net.md_5.bungee.BungeeCord;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import fr.skybeastmc.socketmessenger.Command;

class SocketManager {

    static final HashMap<String, SocketClient> connectedSockets = new HashMap<>();
    static int ID = 0;
    private static ServerSocket serverSocket;

    static void init(int port){
        try {
            Util.log("Listening for socket connections on port "+port+"!");
            serverSocket = new ServerSocket(port);
            @SuppressWarnings("deprecation")
            ExecutorService service = BungeeCord.getInstance().getPluginManager().getPlugin("SocketMessenger").getExecutorService();
            service.submit(() -> {
                while(!serverSocket.isClosed()){
                    try {
                        Socket socket = serverSocket.accept();
                        service.submit(() -> initSocket(socket));
                    } catch (IOException e) {
                        if(e.getMessage().toLowerCase().contains("socket closed")) return;
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void end(){
        try {
            if(!serverSocket.isClosed()) serverSocket.close();
            connectedSockets.values().forEach(s -> s.sendCommand(Command.EXIT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initSocket(Socket socket){
        ID++;
        Util.log("Socket connected! ID: "+ID);
        new SocketClient(socket);
    }
}
