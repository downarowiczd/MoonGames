package com.downardo.moongames.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.downardo.moongames.MoonGames;

public class FoodListener implements Listener{
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event){
		if(MoonGames.status == 0 || MoonGames.status == 2){
			event.setCancelled(true);
		}
		
		if(MoonGames.preparing == true){
			event.setCancelled(true);
		}
		
		
		if(event.getEntity() instanceof Player){
			Player p = (Player)event.getEntity();
			if(MoonGames.spectating.contains(p.getUniqueId().toString())){
				event.setCancelled(true);
			}
		}
		
	}

}
