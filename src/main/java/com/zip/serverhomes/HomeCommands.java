package com.zip.serverhomes;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.zip.serverhomes.MessageUtils.Type;

public class HomeCommands implements CommandExecutor, TabCompleter {
	
	private final FileConfiguration config;
	DBControl control;
	public HomeCommands(FileConfiguration config) {
		this.config = config;
		this.control = new DBControl();
	}
	public void reconnectDB() {
		try {
			if (control.connection.isClosed()) {
				control.connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1/Server","root","password");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			MessageUtils.sendMessage(sender, Type.ERROR, "This command can only be used by a player.");
			return true;
		}
		//reconnect to the database to avoid errors
		reconnectDB();

		//get home information
		final Player player = (Player) sender;
		String alias = config.getString(player.getUniqueId().toString(), "");
		String uuid_prefix = (alias.isEmpty() ? player.getUniqueId().toString() : alias);
		String home_name = args.length == 0 ? "home" : args[0];
		String sanitized = home_name.replaceAll("[^A-Za-z0-9_-]", "");
		//error checking
		if(sanitized.isEmpty()) {
			MessageUtils.sendMessage(player, Type.ERROR, "Name given contains no valid characters.");
			return true;
		}
		if(!home_name.equals(sanitized)) {
			MessageUtils.sendMessage(player, Type.WARN, "Effective name will differ from the one given.");
			home_name = sanitized;
		}

		//get homelist from database
		ResultSet rs = control.selectRaw("Homes", "uuid", "\""+uuid_prefix+"\"", "label", "\""+home_name+"\"");
		Location destination = null;
		try {
			//get x, y, and z coordinates to put into a Location object
			if (rs.next()) {
				destination = new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		//main home commands
		if(label.equalsIgnoreCase("home")) {
			if(destination == null) MessageUtils.sendMessage(player, Type.ERROR, "Name given is not associated with a home.");
			else {
				MessageUtils.sendMessage(player, Type.INFO, "Teleported to \"" + home_name + "\".");
				player.teleport(destination);
			}
		} else if(label.equalsIgnoreCase("sethome")) {
			if(destination != null) MessageUtils.sendMessage(player, Type.WARN, "Overwriting existing home associated with the given name.");
			MessageUtils.sendMessage(player, Type.INFO, "Home \"" + home_name + "\" set to your position.");

			//set the home in the database
			control.delete("Homes", "label","\""+home_name+"\"","uuid","\""+uuid_prefix+"\"");
			control.insert("Homes",new String[] {
					uuid_prefix,
					player.getName(),
					home_name,
					String.valueOf(player.getLocation().getBlockX()),
					String.valueOf(player.getLocation().getBlockY()),
					String.valueOf(player.getLocation().getBlockZ()),
					player.getLocation().getWorld().getName()
			});
		} else if(label.equalsIgnoreCase("delhome")) {
			if(destination == null) MessageUtils.sendMessage(player, Type.ERROR, "Name given is not associated with a home.");
			else {
				MessageUtils.sendMessage(player, Type.INFO, "Home named \"" + home_name + "\" has been deleted.");
				control.delete("Homes", "label","\""+home_name+"\"","uuid","\""+uuid_prefix+"\"");
			}
		} else return false;
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player player)) return null;
		//home or delhome commands
		if (label.equalsIgnoreCase("home") || label.equalsIgnoreCase("delhome")) {
			if (args.length == 1) {
				//reconnect to the database to avoid errors
				reconnectDB();

				//get strings
				String alias = config.getString(player.getUniqueId().toString(), "");
				String uuid_prefix = (alias.isEmpty() ? player.getUniqueId().toString() : alias);

				//store home results in a List
				List<String> options = new LinkedList<>();
				control.select("Homes", "uuid", "\""+uuid_prefix+"\"", "label").forEach((String s) -> {
					if(!s.toLowerCase().contains(args[0].toLowerCase())) return;
					options.add(s);
				});
				return options;
			}
		}
		return null;
	}
}
