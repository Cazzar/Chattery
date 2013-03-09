/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.chattery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.mcforge.chattery.system.Chattery;

import com.mysql.jdbc.Driver;

public class MySQL extends SQL {
	protected Connection connection;
    protected String IP;
    protected int port;
    protected String DB;
    protected String prefix;
    protected String username;
    protected String pass;
    
    public MySQL(Chattery plugin) {
		super(plugin);
	}

    @Override
    public void executeQuery(String command) {
        try {
            if (connection.isClosed())
                connect();
        } catch (SQLException e) {
            connect();
        }
        try {
            PreparedStatement pstm = connection.prepareStatement(command);
            pstm.executeUpdate();
            pstm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeQuery(String[] commands) {
        try {
            if (connection.isClosed())
                connect();
        } catch (SQLException e) {
            connect();
        }
        try {
            Statement statement = connection.createStatement();
            for (String s : commands) {
                try {
                    statement.executeUpdate(s);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet fillData(String command) {
        try {
            if (connection.isClosed())
                connect();
        } catch (SQLException e) {
            connect();
        }
        try {
            return connection.prepareStatement(command).executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void connect() {
        try {
            DriverManager.registerDriver(new Driver());
            connection = DriverManager.getConnection(getURL() + DB + getProperties(), username, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
    
    public String getURL() {
        return "jdbc:mysql://" + IP + ":" + port + "/";
    }
    
    public String getProperties() {
        return "?autoDeserialize=true";
    }
    
    public void setIP(String IP) {
        this.IP = IP;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getIP() {
        return IP;
    }
    
    public int getPort() {
        return port;
    }

    public String getFullURL() {
        return getURL() + DB;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    @Override
    public String getPrefix() {
        return prefix;
    }
    
    public void setUsername(String user) {
        this.username = user;
    }
    
    public void setPassword(String pass) {
        this.pass = pass;
    }
    
    public void setDatabase(String DB) {
        this.DB = DB;
    }

	@Override
	public void init() {
		setUsername(plugin.getConfig().getString("database.mysql.username"));
		setPassword(plugin.getConfig().getString("database.mysql.password"));
		setDatabase(plugin.getConfig().getString("database.mysql.database"));
		setIP(plugin.getConfig().getString("database.mysql.ip"));
		setPort(plugin.getConfig().getInt("database.mysql.port"));
		connect();
		executeQuery("CREATE TABLE IF NOT EXISTS Player (Name VARCHAR(20), ReadRules TINYINT(1), Agreed TINYINT(1), Ignoring TINYINT(1));");
	}

}
