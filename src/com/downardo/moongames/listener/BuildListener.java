package com.downardo.moongames.listener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.downardo.moongames.MoonGames;

public class BuildListener implements Listener{
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event){
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
	public void onPlace(BlockBreakEvent event){
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
