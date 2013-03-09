/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.chattery.system;

import java.io.File;
import java.util.Locale;

import net.mcforge.chattery.commands.CmdGlobalAgree;
import net.mcforge.chattery.commands.CmdGlobalChat;
import net.mcforge.chattery.commands.CmdGlobalRules;
import net.mcforge.chattery.database.MySQL;
import net.mcforge.chattery.database.SQL;
import net.mcforge.chattery.database.SQLite;

import org.bukkit.plugin.java.JavaPlugin;

public final class Chattery extends JavaPlugin {
	private final String CONFIGDIR = "plugins/Chattery/";
	private final String CONFIGPATH = CONFIGDIR + "config.yml";
	private final String VERSION = "0.0.1";
	
	private SQL database;
	private PlayerHandler handler;
	
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
		handler = new PlayerHandler(this);
		
		getCommand("global").setExecutor(new CmdGlobalChat());
		getCommand("globalrules").setExecutor(new CmdGlobalRules());
		getCommand("globalagree").setExecutor(new CmdGlobalAgree());
		
		
		handler.addPlayer("Lonesface");
		handler.setAgreed("Lonesface", true);
		
		getLogger().info("MCForge Chattery, version " + VERSION + ", successfully loaded!");
	}

	@Override
	public void onDisable() {
		getLogger().info("MCForge Chattery unloaded!");
	}
	
	public SQL getSQL() {
		return database;
	}
	
	public PlayerHandler getPlayerHandler() {
		return handler;
	}
}
