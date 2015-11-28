package com.downardo.moongames;

import org.bukkit.Bukkit;

public class PeacePeriode implements Runnable{

	
	private int taskID = -1;
	private int time = 65;
	
	@Override
	public void run() {
		if(time != 0){
			if((time == 60 || time == 30 || time == 20 || time == 10 || time <= 5) && time > 0){
				Bukkit.broadcastMessage(MoonGames.moonGames + "§a§lDie Friedensphase endet in §e" + time + "§a Sekunden");
			}
			
		}else{
			Bukkit.broadcastMessage(MoonGames.moonGames + "§c§lDie Friedensphase hat ihr Ende erreicht!");
			MoonGames.peace = false;
			Bukkit.getWorld("playMap").createExplosion(MoonGames.playLocation, 20F, true);
			this.cancel();
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
