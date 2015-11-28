package com.downardo.moongames.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.downardo.moongames.MoonGames;

public class PingListener implements Listener{
	
	 @EventHandler
	 public void onPing(ServerListPingEvent event){
		 if(MoonGames.status == 0){
			 event.setMotd("§aJoin");
		 }else if(MoonGames.status == 1){
			 event.setMotd("§cInGame");			
		 }else if(MoonGames.status == 2){
			 event.setMotd("§cOffline");			
		 }
	 }

}
