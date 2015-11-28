package com.downardo.moongames;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class StartingGameTask implements Runnable{

	private int taskID = -1;
	private int time = 120;
	
	@Override
	public void run() {
		if(time != 0){
			for(Player players : Bukkit.getOnlinePlayers()){
				players.setLevel(time);
			}
			if((time == 90 || time == 60 || time == 30 || time == 20 || time == 10 || time <= 5) && time > 0){
				Bukkit.broadcastMessage(MoonGames.moonGames + "§aDie Lobby wird in §e" + time + "§a Sekunden geschlossen!");
				if(Bukkit.getOnlinePlayers().size() > 5){
					time = 25;
				}
				
				for(Player player : Bukkit.getOnlinePlayers()){
					player.playSound(player.getEyeLocation(), Sound.CLICK, 20, 20);
				}
			}
		}else{
			if(Bukkit.getOnlinePlayers().size() >= 4){
				
				for(Entity e : Bukkit.getWorld("playMap").getEntities()){
					if(e.getType() == EntityType.ENDER_DRAGON){
						Damageable d = (Damageable)e;
						d.setHealth(0.0D);
					}
				}
				
				MoonGames.preparing = true;
				MoonGames.status = 1;
				Bukkit.broadcastMessage(MoonGames.moonGames + "§a§lDie Lobby wurde nun geschlossen.");
				//int i = 0;
				for(Player players : Bukkit.getOnlinePlayers()){
					//MoonGames.teleportMap(players, i);
					MoonGames.teleportMap();
					MoonGames.living.add(players.getUniqueId().toString());
					MoonGames.kills.put(players.getUniqueId().toString(), 0);
					players.setLevel(0);
					players.getInventory().clear();
					players.getInventory().setArmorContents(null);
					players.getInventory().setItem(0, new ItemStack(Material.COMPASS));
					//i++;
					if(players.getScoreboard()!=null){
						players.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
					}
				}
				
				new PrepareTask().go();
				this.cancel();
			}else{
				Bukkit.broadcastMessage(MoonGames.moonGames + "§c§lEs sind zu wenige Spieler online.");
				time = 90;
				
			}
			
			
			
		}
		
		time--;
		
		
		
	}
	
	public void go(){
		taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("MoonGames"), this, 0L, 20L);
	}
	
	public void cancel(){
		if(taskID == -1)
			return;
		
		try{
			Bukkit.getServer().getScheduler().cancelTask(taskID);
		}finally{
			taskID = -1;
		}
		
	}
	

}
