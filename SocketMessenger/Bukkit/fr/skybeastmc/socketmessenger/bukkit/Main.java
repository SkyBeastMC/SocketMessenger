package fr.skybeastmc.socketmessenger.bukkit;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	private static JavaPlugin plugin;

	public void onEnable() {

		plugin = this;

		String address = null;
		int port = -1;
		try {
			if (!getDataFolder().exists()) {
				if (!getDataFolder().mkdir()) {
					throw new RuntimeException(
							"Could not create configuration folder!");
				}
			}
			File config = new File(getDataFolder(), "socketMessenger.yml");

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
				port = YamlConfiguration.loadConfiguration(config).getInt(
						"port");
				address = YamlConfiguration.loadConfiguration(config)
						.getString("address");
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

}
