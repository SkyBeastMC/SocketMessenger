package fr.skybeastmc.socketmessenger.bukkit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Debug {
	private static boolean debug;

	public static void debug(Object... o) {
		if (debug) {
			for (Object object : o) {
				Bukkit.getLogger().info(valueOf(object));
			}
		}
	}

	public static void debug() {
		if (debug)
			Bukkit.getLogger().info("");
	}

	public static void bc(Object... o) {
		if (debug) {
			for (Object object : o) {
				Bukkit.broadcastMessage(valueOf(object));
			}
		}
	}

	public static void bc() {
		if (debug)
			Bukkit.broadcastMessage("");
	}

	public static void info(Object... o) {
		for (Object object : o) {
			Bukkit.getLogger().info(valueOf(object));
		}
	}

	public static void info() {
		Bukkit.getLogger().info("");
	}

	private static String valueOf(Object o) {
		return o instanceof Object[] ? Arrays.toString((Object[]) o) : String
				.valueOf(o);
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Debug.debug = debug;
	}

	public static void error(Throwable error, String phase, boolean disable) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.isOp()) {
				player.sendMessage("§c[" + Main.getPlugin().getName()
						+ "] Oops, there was an error while phase \"" + phase
						+ "\"!");
				player.sendMessage("§cStacktrace: ");
				if (debug)
					player.sendMessage("§c" + stackTraceToString(error));
				else
					player.sendMessage("§c[See console]");
				if (disable) {
					player.sendMessage("§4The plugin can't work now! Disabling...");
					Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
				}
			}
		}
		Bukkit.getLogger().severe(
				"[" + Main.getPlugin().getName()
						+ "] Oops, there was an error while phase \"" + phase
						+ "\"!");
		Bukkit.getLogger().severe("Stacktrace: ");
		Bukkit.getLogger().severe(stackTraceToString(error));
		if (disable) {
			Bukkit.getLogger()
					.severe("The plugin can't work now! Disabling...");
			Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
		}
	}

	private static String stackTraceToString(Throwable error) {
		StringWriter errors = new StringWriter();
		error.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
