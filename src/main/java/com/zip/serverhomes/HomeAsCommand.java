package com.zip.serverhomes;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.zip.serverhomes.MessageUtils.Type;

public class HomeAsCommand implements CommandExecutor, TabCompleter {
	
	private final FileConfiguration config;
	public HomeAsCommand(FileConfiguration config) { this.config = config; }

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			MessageUtils.sendMessage(sender, Type.ERROR, "This command can only be used by a player.");
			return true;
		}
		
		Player player = (Player) sender;
		if(args.length == 0 || args[0].equalsIgnoreCase(player.getName())) {
			MessageUtils.sendMessage(player, Type.INFO, "Home aliasing removed.");
			config.set(player.getUniqueId().toString(), null);
		} else {
			OfflinePlayer[] seen_players = Bukkit.getOfflinePlayers();
			String alias_uuid = null;
			for(int i=0; i<seen_players.length; i++) {
				if(seen_players[i].getName().equalsIgnoreCase(args[0]))
					alias_uuid = seen_players[i].getUniqueId().toString();
			}
			if(alias_uuid == null) MessageUtils.sendMessage(player, Type.ERROR, "Player "+args[0]+" has never joined the server.");
			else {
				MessageUtils.sendMessage(player, Type.INFO, "Home aliasing set to player "+args[0]+".");
				config.set(player.getUniqueId().toString(), alias_uuid);
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return null;
		if(args.length == 1) {
			List<String> options = new LinkedList<String>();
			OfflinePlayer[] seen_players = Bukkit.getOfflinePlayers();
			for(OfflinePlayer p : seen_players) options.add(p.getName());
			return options;
		}
		return null;
	}
}
