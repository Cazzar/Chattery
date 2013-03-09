/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.chattery.system;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerHandler {
	private Chattery plugin;
	
	public PlayerHandler(Chattery plugin) {
		this.plugin = plugin;
	}
	
	public boolean readRules(String playerName) throws SQLException {
		ResultSet rs = plugin.getSQL().fillData("SELECT `ReadRules` FROM `Player` WHERE `Name` = \'" + playerName + "\';");
		
		if (rs.next()) {
			boolean readRules = (rs.getInt(1) == 1);
			rs.close();
			return readRules;
		}
		
		rs.close();
		return false;
	}
	
	public void setReadRules(String playerName, boolean readRules) {
		String query = "UPDATE `Player` SET `ReadRules`=" + (readRules ? 1 : 0) + " WHERE `Name` = \'" + playerName + "\';";
		plugin.getSQL().executeQuery(query);
	}
	
	public boolean agreed(String playerName) throws SQLException {
		ResultSet rs = plugin.getSQL().fillData("SELECT `Agreed` FROM `Player` WHERE `Name` = \'" + playerName + "\';");
		
		if (rs.next()) {
			boolean agreed = (rs.getInt(1) == 1);
			rs.close();
			return agreed;
		}
		
		rs.close();
		return false;
	}
	
	public void setAgreed(String playerName, boolean agreed) {
		String query = "UPDATE `Player` SET `Agreed`=" + (agreed ? 1 : 0) + " WHERE `Name` = \'" + playerName + "\';";
		plugin.getSQL().executeQuery(query);
	}
	
	public boolean ignoring(String playerName) throws SQLException {
		ResultSet rs = plugin.getSQL().fillData("SELECT `Ignoring` FROM `Player` WHERE `Name` = \'" + playerName + "\';");
		
		if (rs.next()) {
			boolean ignoring = (rs.getInt(1) == 1);
			rs.close();
			return ignoring;
		}
		
		rs.close();
		return false;
	}
	
	public void setIgnoring(String playerName, boolean ignoring) {
		String query = "UPDATE `Player` SET `Ignoring`=" + (ignoring ? 1 : 0) + " WHERE `Name` = \'" + playerName + "\';";
		plugin.getSQL().executeQuery(query);
	}

	public void addPlayer(String playerName) {
		String query = "INSERT INTO `Player`(`Name`, `ReadRules`, `Agreed`, `Ignoring`) VALUES (\'" + playerName + "\', 0, 0, 0);";
		plugin.getSQL().executeQuery(query);
	}
}