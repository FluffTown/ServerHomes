package com.zip.serverhomes;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player) sender;
            Location destination = this.getConfig().getLocation(p.getUniqueId()+ "|" + ((args.length == 0)? "home":args[0]));

            if (label.equalsIgnoreCase("home")) {
                if (destination == null) {
                    p.sendMessage("Home Does not Exist!");
                    return true;
                }
                p.teleport(destination);
                p.sendMessage("Teleported To Home: " + ((args.length == 0)? "home":args[0]));
                return true;
            }

            if (label.equalsIgnoreCase("sethome")) {
                if (destination == null) {
                    p.sendMessage("Home Does not Exist!");
                    return true;
                }
                this.getConfig().set(p.getUniqueId() + "|"+ ((args.length == 0)? "home":args[0]), p.getLocation());
                p.sendMessage("Set Home As: "+((args.length == 0)? "home":args[0]));
                this.saveConfig();
                return true;
            }

            if (label.equalsIgnoreCase("delhome")) {
                if (args.length == 0) {
                    p.sendMessage("Please specify home name");
                } else {
                    this.getConfig().set(p.getUniqueId() + "|" + args[0], null);
                    p.sendMessage("Deleted Home: " + args[0]);
                    this.saveConfig();
                }
                return true;
            }
        } else {
            System.out.println("[Error] Please only run command as a player.");
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (alias.equalsIgnoreCase("home") || alias.equalsIgnoreCase("delhome")) {
            final Player p = (Player) sender;
            if (args.length == 1) {
                List<String> list = new ArrayList<>();
                Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                Bukkit.getServer().getOnlinePlayers().toArray(players);
                Set<String> homes = this.getConfig().getKeys(false);
                for (String x : homes) {
                    if (x.contains(p.getUniqueId().toString())) {
                        String[] parts = x.split("\\|");
                        list.add(parts[1]);
                    }
                }
                return list;
            }
        }
        return null;
    }
}