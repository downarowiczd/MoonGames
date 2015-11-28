package com.downardo.moongames.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;

public class EntityCreatePortalListener implements Listener{
	
	@EventHandler
	public void onCreate(EntityCreatePortalEvent event){
		event.setCancelled(true);
	}

}
