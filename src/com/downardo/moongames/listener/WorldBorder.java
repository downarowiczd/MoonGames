package com.downardo.moongames.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.downardo.moongames.MoonGames;

public class WorldBorder implements Listener{
	
	private static List<String> list = new ArrayList<String>();
	private MoonGames plugin;
	
	
	public WorldBorder(MoonGames plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onWallMove(PlayerMoveEvent event){
		if(MoonGames.status != 0){
			if(event.getPlayer().getLocation().getWorld().getName() == "playMap"){
				final Player p = event.getPlayer();
				
				Location loc = p.getLocation();
				
				
				
				if(MoonGames.playLocation.distance(loc) > MoonGames.border){
					
					if(MoonGames.playLocation.distance(loc) > MoonGames.border + 15){
						p.sendMessage(MoonGames.moonGames + "ß5Du bist zu weit drauﬂen!");
						p.teleport(MoonGames.playLocation);
						return;
					}
					
					int aX = MoonGames.playLocation.getBlockX();
					int aY = MoonGames.playLocation.getBlockY();
					int aZ = MoonGames.playLocation.getBlockZ();
					
					int bX = loc.getBlockX();
					int bY = loc.getBlockY();
					int bZ = loc.getBlockZ();
					
					
					int x = aX - bX;
					int y = aY - bY;
					int z = aZ - bZ;
					
					
					Vector vector = new Vector(x, y, z).normalize();
					vector.multiply(0.8D);
					p.setVelocity(vector);
					p.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 3);
					
					
					
					
					
					if(!list.contains(p.getName())){
						p.sendMessage(MoonGames.moonGames + "ß5Du hast das ßeEnde ß5der Welt erreicht!");
						list.add(p.getName());
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							
							@Override
							public void run() {
								list.remove(p.getName());
							}
						}, 20L);
						
					}
					
					
					
					
				}
				
				
			}
		}
	}

}
