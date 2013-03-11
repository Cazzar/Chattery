/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.chattery.system;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Formatter;
import java.util.Locale;

import net.mcforge.chattery.commands.CommandHandler;
import net.mcforge.chattery.commands.EventHandler;
import net.mcforge.chattery.database.MySQL;
import net.mcforge.chattery.database.SQL;
import net.mcforge.chattery.database.SQLite;
import net.mcforge.chattery.irc.ForgeIRCBot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Chattery extends JavaPlugin {
	private final String CONFIGDIR = "plugins/Chattery/";
	private final String CONFIGPATH = CONFIGDIR + "config.yml";
	public final String VERSION = "0.0.1";
	
	private ForgeIRCBot bot;
	private SQL database;
	private PlayerHandler playerHandler;
	private CommandHandler commandHandler;
	private EventHandler eventHandler;
	
	private volatile boolean dataDisabled = false;
	
	@Override
	public void onEnable() {
		new File(CONFIGDIR).mkdirs();
		if (!new File(CONFIGPATH).exists()) {
			saveDefaultConfig();
		}
		
		String dataType = getConfig().getString("database.type").toLowerCase(Locale.ENGLISH);
		
		switch(dataType) {
			case "mysql":
				database = new MySQL(this);
				break;
			case "sqlite":
				database = new SQLite(this);
				break;
			default:
				getLogger().warning(dataType + " is an invalid database type! Defaulting to SQLite...");
				break;
		}

		database.init();
		
		if (dataDisabled) {
			return;
		}
		
		playerHandler = new PlayerHandler(this);
		commandHandler = new CommandHandler(this);
		eventHandler = new EventHandler(this);
		
		getCommand("global").setExecutor(commandHandler);
		getCommand("globalrules").setExecutor(commandHandler);
		getCommand("globalagree").setExecutor(commandHandler);
		getCommand("globalignore").setExecutor(commandHandler);
		getCommand("globalinfo").setExecutor(commandHandler);
		Bukkit.getPluginManager().registerEvents(eventHandler, this);
		
		bot = new ForgeIRCBot(this);
		bot.loadData();
		try {
			readFormat(bot);
			bot.startBot();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		getLogger().info("MCForge Chattery, version " + VERSION + ", successfully loaded!");
	}

	@Override
	public void onDisable() {
		getLogger().info("MCForge Chattery unloaded!");
	}
	
	public ForgeIRCBot getBot() {
		return bot;
	}
	
	public SQL getSQL() {
		return database;
	}
	
	public PlayerHandler getPlayerHandler() {
		return playerHandler;
	}
	
	public void readFormat(ForgeIRCBot bot) throws IOException {
		File config = new File("plugins/Chattery/format.yml");
		if (!config.exists()) {
			config.createNewFile();
			Formatter form = new Formatter(config);
			form.out().append("# incoming: The format for incoming Global Chat messages.\r\n");
			form.out().append("# outgoing: The format for outgoing Global Chat messages.\r\n");
			form.out().append("# %username% will be replaced with the name of the user who sent the message\r\n");
			form.out().append("# %playername% will be replaced with the name of the player sending the message\r\n");
			form.out().append("# Note that %playername% only works for outgoing messages!");
			form.out().append("# %message will be replaced by the specified message\r\n");
			form.out().append("# Your format cannot contain the equals (=) character!\r\n");
			form.out().append("incoming = §6>[Global] %username%:§f %message%\r\n");
			form.out().append("outgoing = §6<[Global] %playername%:§f %message%\r\n");
			form.close();
			return;
		}
		
		LineNumberReader lnr = new LineNumberReader(new FileReader(config));
		String line;
		
		while ((line = lnr.readLine()) != null) {
			if (line.startsWith("#")) {
				continue;
			}
			String[] split = line.split("=");
			if (split.length != 2) {
				getLogger().warning("Invalid format for line: \"" + line + "\"");
				continue;
			}
			if (split[1].startsWith(" ")) {
				split[1] = split[1].substring(1);
			}
			
			if (split[0].startsWith("incoming")) {
				bot.setIncomingFormat(split[1]);
			}
			else if (split[0].startsWith("outgoing")) {
				bot.setOutgoingFormat(split[1]);
			}			
		}
		lnr.close();
	}
	
	public void disable() {
		dataDisabled = true;
	}
}
