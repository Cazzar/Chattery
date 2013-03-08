/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.chattery.system;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Database {
	private final static String DATADIR = "plugins" + File.separator + "Chattery" + File.separator;
	private final static String DATAFILE = DATADIR + "Chattery.db";

	private static Connection connection;
	private static String prefix;
	private static String file;
	private final static String PATH = "jdbc:sqlite:";
	
	public static void init() {
		new File(DATADIR).mkdirs();
		setFile(DATAFILE);
		connect();
		executeQuery("CREATE TABLE IF NOT EXISTS Player (Name VARCHAR(20), ReadRules BOOLEAN, Agreed BOOLEAN, Ignoring BOOLEAN);");
	}

	public static void executeQuery(String command) {
		try {
			if (connection.isClosed()) connect();
		}
		catch (SQLException e) {
			connect();
		}
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(command);
			statement.close();
		}
		catch (SQLException e) {
		}

	}

	public static void setFile(String file) {
		Database.file = file;
	}

	public static String getFile() {
		return file;
	}

	public static void executeQuery(String[] commands) {
		try {
			if (connection.isClosed()) connect();
		}
		catch (SQLException e) {
			connect();
		}
		try {
			Statement statement = connection.createStatement();
			for (String s : commands) {
				try {
					statement.executeUpdate(s);
				}
				catch (SQLException e) {
				}
			}
			statement.close();
		}
		catch (SQLException e) {
		}
	}

	public static ResultSet fillData(String command) {
		try {
			if (connection.isClosed()) connect();
		}
		catch (SQLException e) {
			connect();
		}
		try {
			return connection.prepareStatement(command).executeQuery();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void connect() {
		try {
			if (!new File(getFile()).exists()) new File(getFile()).createNewFile();
			DriverManager.registerDriver(new org.sqlite.JDBC());
			connection = DriverManager.getConnection(PATH + getFile());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		return connection;
	}

	public static void setPrefix(String prefix) {
		Database.prefix = prefix;
	}

	public static String getPrefix() {
		return prefix;
	}
}
