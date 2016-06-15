package fr.skybeastmc.socketmessenger.bungeecord;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Debug {
	private static boolean debug;

	public static void debug(Object... o) {
		if (debug) {
			for (Object object : o) {
				ProxyServer.getInstance().getLogger().info(valueOf(object));
			}
		}
	}

	public static void debug() {
		if (debug)
			ProxyServer.getInstance().getLogger().info("");
	}

	public static void bc(Object... o) {
		if (debug) {
			for (Object object : o) {
				ProxyServer.getInstance().broadcast(
						new TextComponent(valueOf(object)));
			}
		}
	}

	public static void bc() {
		if (debug)
			ProxyServer.getInstance().broadcast(new TextComponent(""));
	}

	public static void info(Object... o) {
		for (Object object : o) {
			ProxyServer
					.getInstance()
					.getLogger()
					.info("[" + Main.getPlugin().getDescription().getName() + "] "
							+ valueOf(object));
		}
	}

	public static void info() {
		ProxyServer.getInstance().getLogger()
				.info("[" + Main.getPlugin().getDescription().getName() + "] " + "");
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
		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			if (player.hasPermission("bungeecord.command.end")) {
				player.sendMessage(new TextComponent("§c["
						+ Main.getPlugin().getDescription().getName()
						+ "] Oops, there was an error while phase \"" + phase
						+ "\"!"));
				player.sendMessage(new TextComponent("§cStacktrace: "));
				if (debug)
					player.sendMessage(new TextComponent("§c"
							+ stackTraceToString(error)));
				else
					player.sendMessage(new TextComponent("§c[See console]"));
				if (disable) {
					player.sendMessage(new TextComponent(
							"§4The plugin can't work now! Disabling..."));
				}
			}
		}
		ProxyServer
				.getInstance()
				.getLogger()
				.severe("[" + Main.getPlugin().getDescription().getName()
						+ "] Oops, there was an error while phase \"" + phase
						+ "\"!");
		ProxyServer.getInstance().getLogger().severe("Stacktrace: ");
		ProxyServer.getInstance().getLogger().severe(stackTraceToString(error));
		if (disable) {
			ProxyServer.getInstance().getLogger()
					.severe("The plugin can't work now! Disabling...");
			Main.getPlugin().onDisable();
		}
	}

	private static String stackTraceToString(Throwable error) {
		StringWriter errors = new StringWriter();
		error.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
