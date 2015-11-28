package com.downardo.moongames.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class CreatureSpawnListener implements Listener{
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event){
			if(event.getSpawnReason()!=SpawnReason.CUSTOM)
				event.setCancelled(true);
	}

}
