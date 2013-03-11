/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.chattery.commands;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import net.mcforge.chattery.system.Chattery;
import net.mcforge.chattery.system.WebUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandHandler implements CommandExecutor {
	
	private Chattery plugin;
	
	public CommandHandler(Chattery plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("global")) {
			return cmdGlobal(sender, args);
		}
		else if (cmd.getName().equals("globalrules")) {
			return cmdGlobalRules(sender);
		}
		else if (cmd.getName().equals("globalagree")) {
			return cmdGlobalAgree(sender);
		}
		else if (cmd.getName().equals("globalinfo")) {
			return cmdGlobalInfo(sender);
		}
		return false;
	}
	
	private boolean cmdGlobalAgree(CommandSender sender) {
		plugin.getPlayerHandler().addPlayer(sender.getName());
		try {
			if (plugin.getPlayerHandler().agreed(sender.getName())) {
				sender.sendMessage("You have already agreed to the Global Chat rules!");
				return true;
			}
			if (!plugin.getPlayerHandler().readRules(sender.getName())) {
				sender.sendMessage("You need to read the Global Chat rules first!");
				sender.sendMessage("Use /globalrules to read them!");
				return true;
			}
		}
		catch (SQLException e) {
			sender.sendMessage("An error occured!");
			e.printStackTrace();
			return true;
		}
		plugin.getPlayerHandler().setAgreed(sender.getName(), true);
		sender.sendMessage("You have agreed to the Global Chat rules and you can use the chat!");
		return true;
	}
	
	private boolean cmdGlobalRules(CommandSender sender) {
		try {
			String[] rules = WebUtils.readContentsToArray(new URL("http://server.mcforge.net/gcrules.txt"));

			for (int i = 0; i < rules.length; i++) {
				sender.sendMessage(rules[i].replace("&", "§"));
			}
			rules = null;
		}
		catch (IOException e) {
			sender.sendMessage("An error occured!");
			e.printStackTrace();
			return true;
		}
		plugin.getPlayerHandler().addPlayer(sender.getName());
		plugin.getPlayerHandler().setReadRules(sender.getName(), true);
		return true;
	}
	
	private boolean cmdGlobal(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("You need to specify a message!");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("rules")) {
			if (sender instanceof Player) {
				((Player)sender).performCommand("globalrules");
				return true;
			}
			sender.sendMessage("The command to read the Global Chat rules is /globalrules");
		}
		else if (args[0].equalsIgnoreCase("agree")) {
			if (sender instanceof Player) {
				((Player)sender).performCommand("globalagree");
				return true;
			}
			sender.sendMessage("The command to agree to the Global Chat rules is /globalagree");
		}
		else if (args[0].equalsIgnoreCase("info")) {
			if (sender instanceof Player) {
				((Player)sender).performCommand("globalinfo");
				return true;
			}
			sender.sendMessage("The command to view the Global Chat information is /globalinfo");
		}
		
		try {
			if (!plugin.getPlayerHandler().readRules(sender.getName())) {
				sender.sendMessage("You need to read and agree to the Global Chat rules before you can use the Global Chat!");
				return true;
			}
		}
		catch (SQLException e) {
			sender.sendMessage("An error occured!");
			e.printStackTrace();
			return true;
		}
		
		String message = join(args, " ");	
		String format = plugin.getBot().getOutgoingFormat().replace("%username%", plugin.getBot().getNick());
		format = format.replace("%message%", message).replace("%playername%", sender.getName());
		
		plugin.getBot().getIRCHandler().sendMessage(message);
		Bukkit.getServer().broadcastMessage(format);
		return true;
	}
	
	private boolean cmdGlobalInfo(CommandSender sender) {
		try {
			String[] info = WebUtils.readContentsToArray(new URL("http://server.mcforge.net/gcinfo.txt"));
			
			for (int i = 0; i < info.length; i++) {
				sender.sendMessage(info[i].replace("&", "§"));
			}
			info = null;
		}
		catch (IOException e) {
			sender.sendMessage("An error occured!");
			e.printStackTrace();
			return true;
		}
		return true;
	}
	
    private String join(String[] array, String separator) {
    	if (array.length == 0) {
    		return "";
    	}
        String ret = "";

        for (int i = 0; i < array.length; i++) {
            ret += array[i] + separator;
        }
        return ret.substring(0, ret.length() - separator.length());
    }
}