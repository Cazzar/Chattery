/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.chattery.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Locale;
import java.util.Random;

import net.mcforge.chattery.system.Chattery;
import net.mcforge.chattery.system.WebUtils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class ForgeIRCBot {
	protected final String CONSTNAME = "SMPBot";
	protected final String REALNAME = "MCForge SMP Bot";
	private Chattery plugin;
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private Thread readerThread;
	private IRCHandler handler;
	
	protected String line;
	protected String[] colonSplit;
	protected String[] spaceSplit;
	private volatile boolean isRunning;
	private volatile boolean connected;
	
	private String server;
	private String channel;
	
	private String username;
	
	private String incomingFormat = "§6>[Global] %username%:§f %message%"; 
	private String outgoingFormat = "§6<[Global] %playername%:§f %message%";
	
	public ForgeIRCBot(Chattery plugin) {
		this.plugin = plugin;
		
		URL url;
		String gcData;
		try {
			url = new URL("http://server.mcforge.net/gcdata");
			gcData = WebUtils.readContentsToArray(url)[0];
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		String[] info = gcData.split("&");
		server = info[0];
		channel = info[1];
	}
	
	public void startBot() throws IOException {
		socket = new Socket(server, 6667);
		
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		isRunning = true;
		
		handler = new IRCHandler(this);
		
		if (username.startsWith("SMP")) {
			plugin.getLogger().info("You're currently using the default Global Chat bot nickname!");
			plugin.getLogger().info("Consider changing your bot's nickname in the server properties!");
			username += new Random().nextInt(1000000000);
		}
		else {
			username = "[SMP]" + username;
		}
		readerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				read();
			}
		});
		readerThread.start();
	}
	
	public void loadData() {
		FileConfiguration config = plugin.getConfig();
		
		username = config.getString("bot.username");
	}

	
	private void read() {
        try  {
            isRunning = true;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            handler.setNick(username);
            handler.sendUserMsg(true);

            while (isRunning) {
                line = reader.readLine();
                if (line == null) {
                	continue;
                }
                colonSplit = line.split(":");
                spaceSplit = line.split(" ");
                if (handler.hasCode("004")) {
                    handler.joinChannel(channel);
                    plugin.getLogger().info("Bot joined the Global Chat!");
                    break;
                }
                else if (handler.hasCode("433")) {
                	plugin.getLogger().info("Nickname already in use! Randomizing..");
                    username = "SMP" + new Random(System.currentTimeMillis()).nextInt(1000000000);
                    handler.setNick(username);
                    plugin.getLogger().info("New Global Chat nickname: " + username);
                }
                else if (line.startsWith("PING ")) {
                    handler.pong(line);
                }
            }
            connected = true;

            while (isRunning) {
                line = reader.readLine();
                if (line == null) {
                    continue;
                }
                colonSplit = line.split(":");
                spaceSplit = line.split(" ");
                if (line.startsWith("PING ")) {
                    handler.pong(line);
                }
                else if (line.toLowerCase(Locale.ENGLISH).contains("privmsg " + channel.toLowerCase(Locale.ENGLISH)) && 
                        !handler.hasCode("005")) {
                    String message = handler.getMessage();
                    if (message.startsWith("\u0001") && message.endsWith("\u0001")) {
                        continue;
                    }
                    if (message.startsWith("^")) {
                        if (message.startsWith("^UGCS")) {
                            //GCCPBanService.updateBanList(); TODO update bans           	
                        }
                        else if (message.startsWith("^GETINFO ")) {
                            if (message.split(" ").length > 1 && message.split(" ")[1].equals(username)) {
                                handler.sendMessage("^Name: " + Bukkit.getServerName());
                                handler.sendMessage("^MoTD: " + Bukkit.getMotd());
                                handler.sendMessage("^Server version: Bukkit" + Bukkit.getVersion());
                                handler.sendMessage("^Plugin version: " + plugin.VERSION);
                                URL ipCheck = new URL("http://server.mcforge.net/ip.php");
                                BufferedReader br = new BufferedReader(new InputStreamReader(ipCheck.openStream()));
                                String ip = br.readLine();
                                handler.sendMessage("^IP: " + ip + ":" + Bukkit.getPort());
                                handler.sendMessage("^Players: " + Bukkit.getOnlinePlayers().length + "/" + Bukkit.getMaxPlayers());
                                br.close();
                            }
                        }
                        else if (message.startsWith("^SENDRULES ")) {
                        	Player who = Bukkit.getPlayer(message.split(" ")[1]);
                            if (who != null) {
                            	who.performCommand("globalrules");
                            }
                        }
                        else if (message.startsWith("^ISONLINE")) {
                        	Player who = Bukkit.getPlayer(message.split(" ")[1]);
                            if (who != null) {
                            	handler.sendMessage("^" + who.getName() + " is online on " + Bukkit.getServerName());
                            }
                        }
                        continue;
                        
                    }
                    String toSend = incomingFormat;
                    toSend = toSend.replace("%username%", handler.getSender()).replace("%message%", message);
                    handler.messagePlayers(toSend);
                    toSend = null;
                }
                else if (colonSplit[1].contains("PRIVMSG " + username)) {
                    if (handler.getMessage().equals("\u0001" + "VERSION" + "\u0001")) {
                        handler.sendNotice(handler.getSender(), "\u0001" + "VERSION MCForge Chattery " + plugin.VERSION + " : " + System.getProperty("os.name") + "\u0001");
                    }
                }
                else if (handler.hasCode("474")) {
                    String providedReason = handler.getMessage();
                    String banReason = providedReason.equals("Cannot join channel (+b)") ? "You're banned" : providedReason;
                    plugin.getLogger().info("You're banned from the Global Chat! Reason: " + banReason);
                    //disposeBot(); TODO: disposebot
                    return;
                }
            }
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
	}
	
	public String getNick() {
		return username;
	} 
	
	public IRCHandler getIRCHandler() {
		return handler;
	}
	
	public BufferedReader getReader() {
		return reader;
	}
	
	public BufferedWriter getWriter() {
		return writer;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public Chattery getPlugin() {
		return plugin;
	}
	
	public String getIncomingFormat() {
		return incomingFormat;
	}
	
	public void setIncomingFormat(String incomingFormat) {
		this.incomingFormat = incomingFormat;
	}
	
	public String getOutgoingFormat() {
		return outgoingFormat;
	}
	
	public void setOutgoingFormat(String outgoingFormat) {
		this.outgoingFormat = outgoingFormat;
	}
}
