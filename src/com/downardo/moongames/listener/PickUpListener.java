package com.downardo.moongames.listener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.downardo.moongames.MoonGames;

public class PickUpListener implements Listener{
	
	@EventHandler
	public void onPickUp(PlayerPickupItemEvent event){
		if(MoonGames.status == 0 || MoonGames.status == 2){
			if(event.getPlayer().getGameMode()!=GameMode.CREATIVE){
				event.setCancelled(true);
			}
		}
		
		if(MoonGames.status == 1){
			if(MoonGames.preparing == true){
				event.setCancelled(true);
			}
			
			if(!MoonGames.living.contains(event.getPlayer().getUniqueId().toString())){
				event.setCancelled(true);
			}
			
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event){
		if(MoonGames.status == 0 || MoonGames.status == 2){
			if(event.getPlayer().getGameMode()!=GameMode.CREATIVE){
				event.setCancelled(true);
			}
		}
		
		if(MoonGames.status == 1){
			if(MoonGames.preparing == true){
				event.setCancelled(true);
			}
			
			if(!MoonGames.living.contains(event.getPlayer().getUniqueId().toString())){
				event.setCancelled(true);
			}
			
		}
	}

}
