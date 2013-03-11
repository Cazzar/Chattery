package net.mcforge.chattery.commands;

import net.mcforge.chattery.system.Chattery;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventHandler implements Listener {
	private Chattery plugin;
	
	public EventHandler(Chattery plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerJoin(PlayerJoinEvent e) {
		plugin.getPlayerHandler().addPlayer(e.getPlayer().getName());
	}
}
