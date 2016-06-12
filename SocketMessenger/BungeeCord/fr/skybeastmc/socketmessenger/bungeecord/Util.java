package fr.skybeastmc.socketmessenger.bungeecord;

import net.md_5.bungee.BungeeCord;

import java.util.logging.Level;

class Util {

    static void log(String message){
        BungeeCord.getInstance().getLogger().log(Level.INFO, message);
    }

}
