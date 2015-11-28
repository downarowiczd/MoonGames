package com.downardo.moongames.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.downardo.moongames.MoonGames;
import com.downardo.moongames.Spectator;

public class DeathListener implements Listener{
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if(MoonGames.status == 1){
			event.setDeathMessage(null);
			Player death = event.getEntity().getPlayer();
			MoonGames.death.add(death.getUniqueId().toString());
			
			Spectator.setSpectator(death);
			
			if(event.getEntity().getKiller() instanceof Player){
				Player killer = event.getEntity().getKiller();
				if(killer.getUniqueId().toString().equals(death.getUniqueId().toString())){
					Bukkit.broadcastMessage(MoonGames.moonGames + "Der Spieler §e" + death.getName() + "§5 starb tragisch!");
				}else{
					MoonGames.kills.put(killer.getUniqueId().toString(), MoonGames.kills.get(killer.getUniqueId().toString()) + 1);
					Bukkit.broadcastMessage(MoonGames.moonGames + "Der Spieler §e" + death.getName() + "§5 wurde von §e" + killer.getName() + "§5 getötet!");
			}
				
				
				
				
				
			}else if(event.getEntity().getKiller() instanceof EnderDragon || event.getEntity().getKiller() instanceof EnderDragonPart){
				Bukkit.broadcastMessage(MoonGames.moonGames + "Der MoonDragon verschluckte §e " + death.getName() +  "§5!");	
			}else{
				Bukkit.broadcastMessage(MoonGames.moonGames + "Der Spieler §e" + death.getName() + "§5 starb tragisch!");	
			}
			
			Bukkit.broadcastMessage(MoonGames.moonGames + "Verbleibende Spieler: §e" + MoonGames.living.size());
			
					/*CraftWorld handle = (CraftWorld) loc.getWorld();
					Meteor m = new Meteor(handle.getHandle());
					handle.getHandle().addEntity(m, SpawnReason.CUSTOM);
					Fireball meteor = (Fireball)m.getBukkitEntity();
					meteor.teleport(loc);
					meteor.setBounce(false);
					meteor.setIsIncendiary(false);
					
					meteor.setVelocity(new Vector(1, -1, 0).normalize().multiply(1));
					meteor.setDirection(new Vector(1, -1, 0).normalize().multiply(1));

					meteor.setYield(2 + new Random().nextInt(14));*/
					
		}
		
	}
	
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		event.setRespawnLocation(MoonGames.playLocation.clone().add(0, 5, 0));
		event.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
	}
	

}
