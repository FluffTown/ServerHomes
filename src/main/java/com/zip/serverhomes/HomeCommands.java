package com.zip.serverhomes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
		String uuid_prefix = player.getUniqueId().toString() + "|";
		String home_name = args.length == 0 ? "home" : args[0];
		Location destination = config.getLocation(uuid_prefix + home_name);
		
		if(label.equalsIgnoreCase("home")) {
			if(destination == null) MessageUtils.sendMessage(player, Type.ERROR, "The home doesn't exist.");
			else {
				MessageUtils.sendMessage(player, Type.INFO, "Teleported to the home.");
				player.teleport(destination);
			}
		} else if(label.equalsIgnoreCase("sethome")) {
			if(destination != null) MessageUtils.sendMessage(player, Type.WARN, "The home was already set. Overwriting.");
			else MessageUtils.sendMessage(player, Type.INFO, "Home set to your position.");
			config.set(uuid_prefix + home_name, player.getLocation());
		} else if(label.equalsIgnoreCase("delhome")) {
			if(destination == null) MessageUtils.sendMessage(player, Type.ERROR, "The home doesn't exist.");
			else {
				MessageUtils.sendMessage(player, Type.INFO, "Home deleted.");
				config.set(uuid_prefix + home_name, null);
			}
		} else return false;
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return null;
		if (label.equalsIgnoreCase("home") || label.equalsIgnoreCase("delhome")) {
			if (args.length == 1) {
				List<String> options = new LinkedList<String>();
				Player player = (Player) sender;
				String uuid_prefix = player.getUniqueId().toString();
				Set<String> homes = config.getKeys(false);
				for(String home : homes) {
					String[] parts = home.split("\\|");
					if(parts[0].equals(uuid_prefix)) options.add(parts[1]);
				}
				return options;
			}
		}
		return null;
	}
}
