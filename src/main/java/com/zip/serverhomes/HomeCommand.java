package com.zip.serverhomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor, TabCompleter {
	
	private final FileConfiguration config;
	
	public HomeCommand(FileConfiguration config) {
		this.config = config;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be used by a player.");
			return true;
		}
		final Player player = (Player) sender;
		String uuid_prefix = player.getUniqueId().toString() + "|";
		String home_name = args.length == 0 ? "home" : args[0];
		Location destination = config.getLocation(uuid_prefix + home_name);
		
		if(label.equalsIgnoreCase("home")) {
			if(destination == null) player.sendMessage("The home doesn't exist.");
			else {
				player.teleport(destination);
				player.sendMessage("Teleported to the home.");
			}
		} else if(label.equalsIgnoreCase("sethome")) {
			if(destination != null) player.sendMessage("The home has already been set.");
			else {
				config.set(uuid_prefix + home_name, player.getLocation());
				player.sendMessage("Home set to your position.");
			}
		} else if(label.equalsIgnoreCase("delhome")) {
			if(destination == null) player.sendMessage("The home doesn't exit.");
			else {
				config.set(uuid_prefix + home_name, null);
				player.sendMessage("Home deleted.");
			}
		} else return false;
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return null;
		if (label.equalsIgnoreCase("home") || label.equalsIgnoreCase("delhome")) {
			if (args.length == 1) {
				List<String> options = new ArrayList<>();
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
