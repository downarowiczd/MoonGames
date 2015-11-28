package com.downardo.moongames.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.downardo.moongames.MoonGames;

public class ChatListener implements Listener{

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		Player p = event.getPlayer();
		
		String prefix = MoonGames.getUserPrefix(p);
		
		
		event.setCancelled(true);
		if(MoonGames.status == 0 || MoonGames.status == 2){
			
				Bukkit.broadcastMessage(prefix + p.getName() + "§7: " + event.getMessage());
			
		}else if(MoonGames.status == 1){
			if(MoonGames.living.contains(p.getUniqueId().toString())){
				Bukkit.broadcastMessage(prefix + p.getName() + "§7: " + event.getMessage());
			}else{
				for(String uuids : MoonGames.spectating){
					Player empf = Bukkit.getPlayer(UUID.fromString(uuids));
					if(empf != null){
							empf.sendMessage(prefix + p.getName() + "§7: " + event.getMessage());
					}
					
				}
			}
		}
	}
	
}
