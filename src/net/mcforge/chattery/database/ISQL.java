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

public interface ISQL {
    
    void executeQuery(String command);
    
    void executeQuery(String[] commands);
    
    ResultSet fillData(String command);
    
    void init();
    
    void connect();
    
    void setPrefix(String prefix);
    
    String getPrefix();
    
    Connection getConnection();

}

