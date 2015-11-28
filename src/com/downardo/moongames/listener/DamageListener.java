package com.downardo.moongames.listener;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import com.downardo.moongames.MoonGames;

public class DamageListener implements Listener{
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		if(event.getEntityType()==EntityType.PLAYER){
			Player p = (Player)event.getEntity();
			if(MoonGames.status == 0 || MoonGames.status == 2)
				event.setCancelled(true);
			
			if(MoonGames.peace)
				event.setCancelled(true);
			
			if(event.getCause()==DamageCause.FALL)
				event.setCancelled(true);
			
			if(MoonGames.spectating.contains(p.getUniqueId().toString())){
				event.setCancelled(true);
			}

		}
		
		
		
		
	}
	
	
	
	
	@EventHandler
	public void onDamageBy(EntityDamageByEntityEvent event){
		if(MoonGames.status == 1){
			
			if(event.getDamager() instanceof EnderDragon || event.getDamager() instanceof EnderDragonPart){
				if(event.getEntity() instanceof Player){
					Player p = (Player)event.getEntity();
					p.setVelocity(new Vector(0, 0, 0));
					Random rnd = new Random();
					int x = rnd.nextInt(MoonGames.living.size());
					Player t = Bukkit.getPlayer(UUID.fromString(MoonGames.living.get(x)));
					while((!MoonGames.living.get(x).equals(p.getUniqueId().toString())) || (t!=null)){
						x = rnd.nextInt(MoonGames.living.size());
						t = Bukkit.getPlayer(UUID.fromString(MoonGames.living.get(x)));
					}
					
						
					Location p_t = p.getLocation();
					@SuppressWarnings("null")
					Location t_t = t.getLocation();
					
					p.teleport(t_t);
					t.teleport(p_t);
					
					p.sendMessage(MoonGames.moonGames + "Du wurdest mit §e" + t.getName() + "§5 getauscht!");
					t.sendMessage(MoonGames.moonGames + "Du wurdest mit §e" + p.getName() + "§5 getauscht!");
					p_t.getWorld().playSound(p.getEyeLocation(), Sound.ENDERMAN_TELEPORT, 20.0F, 20.0F);
					t_t.getWorld().playSound(t.getEyeLocation(), Sound.ENDERMAN_TELEPORT, 20.0F, 20.0F);
				}
			}
			
			/*if(event.getDamager() instanceof Player){
				Player damager = (Player)event.getDamager();
				if(MoonGames.spectating.contains(damager.getUniqueId().toString())){
					event.setCancelled(true);
				}else if(MoonGames.living.contains(damager.getUniqueId().toString())){
					if(event.getEntity() instanceof Player){
						Player p = (Player)event.getEntity();
						if(damager.getName()=="DOWNARDO"){
							damager.sendMessage(damager.getHealthScale() + " Leben!");
						}
						if(Skills.getSkill(damager).getID() == 5 && damager.getHealthScale() < 10.0){
							p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*4, 1));
						}
					}
					
				}
			}*/
		}else{
			event.setCancelled(true);
		}
	}

}
