package com.downardo.moongames.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.downardo.moongames.MoonGames;

public class ChunkLoadListener implements Listener{
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event){
		if(MoonGames.playLocation!=null){
			if(event.getChunk()==MoonGames.playLocation.getChunk()){
				event.getChunk().load();
				event.setCancelled(true);
			}
		}
	}

}
