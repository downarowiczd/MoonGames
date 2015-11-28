package com.downardo.moongames.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.downardo.moongames.MoonGames;

public class TrackerListener implements Listener{
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer().getItemInHand().getType() == Material.COMPASS){
			if(MoonGames.living.contains(event.getPlayer().getUniqueId().toString())){
				if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
					if(MoonGames.status == 1){
							Player target = null;
							for(String uuid : MoonGames.living){
								Player players = Bukkit.getPlayer(UUID.fromString(uuid));
								
								if(players == null) continue;
								
								if(players == event.getPlayer()) continue;
								
								
								if((target != null) && (event.getPlayer().getLocation().distance(players.getLocation()) >= target.getLocation().distance(event.getPlayer().getLocation()))) continue;
								
								target = players;
							}
							
						if(target == null){
							event.getPlayer().sendMessage(MoonGames.moonGames + "§aEs ist kein Spieler in der Nähe!");
						}else{
							event.getPlayer().sendMessage(MoonGames.moonGames + "§aDer Spieler §e" + target.getName() + "§a ist " + Math.round(target.getLocation().distance(event.getPlayer().getLocation())) + "§a von dir entfernt!");
							event.getPlayer().setCompassTarget(target.getLocation());
						}
							
							
					}	
						}else{
							event.setCancelled(true);
						}
				}
				
			}
	}
}
