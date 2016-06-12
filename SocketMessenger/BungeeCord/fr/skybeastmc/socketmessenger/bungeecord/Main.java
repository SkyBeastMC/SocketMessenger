package fr.skybeastmc.socketmessenger.bungeecord;

import java.io.File;
import java.io.IOException;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Main extends Plugin {
	private static Plugin plugin;
	
    public void onEnable() {
    	plugin = this;
    	
        int port = -1;
        try {
            if(!getDataFolder().exists()) {
                if(!getDataFolder().mkdir()){
                    throw new RuntimeException("Could not create configuration folder!");
                }
            }
            File config = new File(getDataFolder(), "socketMessenger.yml");

            if(!config.exists()){
                if(!config.createNewFile()){
                    throw new RuntimeException("Could not create configuration file!");
                }
                Configuration conf = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
                conf.set("port", 55555);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(conf, config);
                port = 55555;
            }
            else{
                port = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config).getInt("port");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        SocketManager.init(port);
    }

    public void onDisable() {
        SocketManager.end(true);
    }

	public static Plugin getPlugin() {
		return plugin;
	}
}
