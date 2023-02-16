package com.zip.serverhomes;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class MessageUtils {

	public enum Type { 
		ERROR("&8[&c!&8] &7"),
		WARN("&8[&6¦&8] &7"),
		INFO("&8[&9i&8] &7");
		
		private final String prefix;
		private Type(String prefix) { this.prefix = prefix; }
		public String getPrefix() { return prefix; }
	}
	
	public static void sendMessage(CommandSender sender, Type type, String message) {
		message = type.getPrefix() + message;
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
}
