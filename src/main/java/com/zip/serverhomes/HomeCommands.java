package com.zip.serverhomes;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.zip.serverhomes.MessageUtils.Type;

public class HomeCommands implements CommandExecutor, TabCompleter {
	
	private final FileConfiguration config;
	public HomeCommands(FileConfiguration config) { this.config = config; }

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			MessageUtils.sendMessage(sender, Type.ERROR, "This command can only be used by a player.");
			return true;
		}
		
		final Player player = (Player) sender;
		String alias = config.getString(player.getUniqueId().toString(), "");
		String uuid_prefix = (alias.isEmpty() ? player.getUniqueId().toString() : alias);
		
		String home_name = args.length == 0 ? "home" : args[0];
		String sanitized = home_name.replaceAll("[^A-Za-z0-9_-]", "");
		if(sanitized.isEmpty()) {
			MessageUtils.sendMessage(player, Type.ERROR, "Name given contains no valid characters.");
			return true;
		}
		if(!home_name.equals(sanitized)) {
			MessageUtils.sendMessage(player, Type.WARN, "Effective name will differ from the one given.");
			home_name = sanitized;
		}
		
		Location destination = config.getLocation(uuid_prefix + "|" + home_name);
		if(label.equalsIgnoreCase("home")) {
			if(destination == null) MessageUtils.sendMessage(player, Type.ERROR, "Name given is not associated with a home.");
			else {
				MessageUtils.sendMessage(player, Type.INFO, "Teleported to \"" + home_name + "\".");
				player.teleport(destination);
			}
		} else if(label.equalsIgnoreCase("sethome")) {
			if(destination != null) MessageUtils.sendMessage(player, Type.WARN, "Overwriting existing home associated with the given name.");
			MessageUtils.sendMessage(player, Type.INFO, "Home \"" + home_name + "\" set to your position.");
			config.set(uuid_prefix + "|" + home_name, player.getLocation());
		} else if(label.equalsIgnoreCase("delhome")) {
			if(destination == null) MessageUtils.sendMessage(player, Type.ERROR, "Name given is not associated with a home.");
			else {
				MessageUtils.sendMessage(player, Type.INFO, "Home named \"" + home_name + "\" has been deleted.");
				config.set(uuid_prefix + "|" + home_name, null);
			}
		} else return false;
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return null;
		if (label.equalsIgnoreCase("home") || label.equalsIgnoreCase("delhome")) {
			if (args.length == 1) {
				Player player = (Player) sender;
				String alias = config.getString(player.getUniqueId().toString(), "");
				String uuid_prefix = (alias.isEmpty() ? player.getUniqueId().toString() : alias);
				
				List<String> options = new LinkedList<String>();
				config.getKeys(false).forEach((String s) -> {
					if(s.length() == 36) return;
					if(!s.replaceAll("\\|.+$", "").equals(uuid_prefix)) return;
					String name = s.replaceAll("^.+?\\|", "");
					if(!name.contains(args[0])) return;
					options.add(name);
				});
				return options;
			}
		}
		return null;
	}
}
