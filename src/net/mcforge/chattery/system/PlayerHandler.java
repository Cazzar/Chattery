package net.mcforge.chattery.system;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerHandler {
	private Chattery plugin;
	
	public PlayerHandler(Chattery plugin) {
		this.plugin = plugin;
	}
	
	public boolean readRules(String playerName) throws SQLException {
		ResultSet rs = plugin.getData().fillData("SELECT `ReadRules` FROM `Player` WHERE Name = \'" + playerName + "\';");
		
		if (rs.next()) {
			boolean readRules = (rs.getInt(1) == 1);
			rs.close();
			return readRules;
		}
		
		rs.close();
		return false;
	}
	
	public boolean agreed(String playerName) throws SQLException {
		ResultSet rs = plugin.getData().fillData("SELECT `Agreed` FROM `Player` WHERE Name = \'" + playerName + "\';");
		
		if (rs.next()) {
			boolean agreed = (rs.getInt(1) == 1);
			rs.close();
			return agreed;
		}
		
		rs.close();
		return false;
	}
	
	public boolean ignoring(String playerName) throws SQLException {
		ResultSet rs = plugin.getData().fillData("SELECT `Ignoring` FROM `Player` WHERE Name = \'" + playerName + "\';");
		
		if (rs.next()) {
			boolean ignoring = (rs.getInt(1) == 1);
			rs.close();
			return ignoring;
		}
		
		rs.close();
		return false;
	}
}
