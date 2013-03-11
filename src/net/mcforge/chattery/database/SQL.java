/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.chattery.database;

import java.sql.Connection;
import java.sql.ResultSet;

import net.mcforge.chattery.system.Chattery;

public abstract class SQL {
	protected Chattery plugin;
	
	public SQL(Chattery plugin) {
		this.plugin = plugin;
	}
    
    public abstract void executeQuery(String command);
    
    public abstract void executeQuery(String[] commands);
    
    public abstract ResultSet fillData(String command);
    
    public abstract void init();
    
    public abstract void connect();
    
    public abstract void setPrefix(String prefix);
    
    public abstract String getPrefix();
    
    public abstract Connection getConnection();

}
