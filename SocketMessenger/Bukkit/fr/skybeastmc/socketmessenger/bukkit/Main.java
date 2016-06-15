package fr.skybeastmc.socketmessenger.bukkit;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.skybeastmc.socketmessenger.bukkit.api.ReceivedDataEvent;

public class Main extends JavaPlugin implements Listener {
	private static JavaPlugin plugin;

	public void onEnable() {
		plugin = this;

		Bukkit.getPluginManager().registerEvents(this, this);

		String address = null;
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
				YamlConfiguration conf = YamlConfiguration
						.loadConfiguration(config);
				conf.set("port", 55555);
				conf.set("address", "localhost");
				conf.save(config);
			} else {
				YamlConfiguration conf = YamlConfiguration
						.loadConfiguration(config);
				port = conf.getInt("port");
				address = conf.getString("address");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (port == -1 || address == null) {
			port = 55555;
			address = "localhost";
		}
		SocketManager.init(address, port);
	}

	public void onDisable() {
		SocketManager.end(false, false);
	}

	public static JavaPlugin getPlugin() {
		return plugin;
	}
	
	@EventHandler
	public void test(ReceivedDataEvent event) {
		
	}
	
	@EventHandler
	public void test(WorldSaveEvent event) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("save");
		
		SocketAPI.sendDataToServer("chan1", out);
	}

}
