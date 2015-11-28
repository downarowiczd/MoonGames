package com.downardo.moongames;

import org.bukkit.scheduler.BukkitRunnable;

public class GravityTask extends BukkitRunnable{

	
	private MoonGames plugin;

	public GravityTask(MoonGames plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		this.plugin.UpdateVelocities();
		new GravityTask(this.plugin).runTaskLater(this.plugin, 1L);
	}
	
	

}
