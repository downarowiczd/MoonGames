package com.downardo.moongames.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.downardo.moongames.MoonGames;

public class MoveListener implements Listener{
	
	@EventHandler
	public void onMove(PlayerMoveEvent event){		
         if (MoonGames.preparing) {
             Location from = event.getFrom();
             Location to = event.getTo();
             double x = Math.floor(from.getX());
             double z = Math.floor(from.getZ());
             if(Math.floor(to.getX())!=x||Math.floor(to.getZ())!=z){
                 x+=.5;
                 z+=.5;
                 event.getPlayer().teleport(new Location(from.getWorld(),x,from.getY(),z,from.getYaw(),from.getPitch()));
             }
		
		
         }
         
         if(MoonGames.status == 0){
        	 if(event.getTo().getY()<0){
        		 event.getPlayer().teleport(MoonGames.lobby);
        	 }
         }
         
         if(MoonGames.status == 2){
        	 if(event.getTo().getY()<0){
        		 event.getPlayer().teleport(MoonGames.playLocation);
        	 }
         }
         
         if(MoonGames.status == 1){
        	 if(event.getTo().getY()<0){
        		 if(MoonGames.peace){
        			 event.getPlayer().teleport(MoonGames.playLocation);
        		 }
        		 
        		 if(MoonGames.spectating.contains(event.getPlayer().getUniqueId().toString())){
        			 event.getPlayer().teleport(MoonGames.playLocation);
        		 }
        		 
        	 }
         }
         
         
	}

}
