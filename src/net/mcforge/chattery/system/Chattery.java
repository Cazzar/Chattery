/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.chattery.system;

import net.mcforge.chattery.commands.CmdGlobalChat;
import net.mcforge.chattery.commands.CmdGlobalRules;
import net.mcforge.chattery.database.ISQL;
import net.mcforge.chattery.database.MySQL;

import org.bukkit.plugin.java.JavaPlugin;

public final class Chattery extends JavaPlugin {
	private ISQL database;
	private PlayerHandler handler;
	private final String VERSION = "0.0.1";
	
	@Override
	public void onEnable() {
		database = new MySQL();
		database.init();
		handler = new PlayerHandler(this);
		
		getCommand("global").setExecutor(new CmdGlobalChat());
		getCommand("globalrules").setExecutor(new CmdGlobalRules());
		
		getLogger().info("MCForge Chattery, version " + VERSION + ", successfully loaded!");
	}

	@Override
	public void onDisable() {
		getLogger().info("MCForge Chattery unloaded!");
	}
	
	public ISQL getData() {
		return database;
	}
	
	public PlayerHandler getPlayerHandler() {
		return handler;
	}
}
