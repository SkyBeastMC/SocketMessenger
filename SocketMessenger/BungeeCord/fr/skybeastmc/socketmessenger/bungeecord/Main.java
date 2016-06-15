package fr.skybeastmc.socketmessenger.bungeecord;

import java.io.File;
import java.io.IOException;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import fr.skybeastmc.socketmessenger.bungeecord.api.ReceivedDataEvent;

public class Main extends Plugin implements Listener {
	private static Plugin plugin;
	
	public void onEnable() {
		plugin = this;
		Debug.setDebug(true);
		
		BungeeCord.getInstance().getPluginManager().registerListener(this, this);

		int port = -1;
		try {
			if (!getDataFolder().exists()) {
				if (!getDataFolder().mkdirs()) {
					throw new RuntimeException(
							"Could not create configuration folder!");
				}
			}
			File config = new File(getDataFolder(), "SocketMessenger.yml");

			if (!config.exists()) {
				if (!config.createNewFile()) {
					throw new RuntimeException(
							"Could not create configuration file!");
				}
				Configuration conf = ConfigurationProvider.getProvider(
						YamlConfiguration.class).load(config);
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
	
	@EventHandler
	public void test(ReceivedDataEvent event) {
		Debug.info(event.getChannel());
		Debug.info(event.getData().readUTF());
	}
}
