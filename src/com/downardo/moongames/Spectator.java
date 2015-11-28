package com.downardo.moongames;


import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Spectator implements Listener{
	
	public static void setSpectator(final Player p){
		if(MoonGames.living.contains(p.getUniqueId().toString())){
			MoonGames.living.remove(p.getUniqueId().toString());
		}
		
		MoonGames.spectating.add(p.getUniqueId().toString());
		p.getInventory().clear();
		p.setLevel(0);
		
		
		
		for(Player players : Bukkit.getOnlinePlayers()){
			players.hidePlayer(p);
		}
		
		p.setAllowFlight(true);
		p.setFlying(true);	
	}
	
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		if(MoonGames.spectating.contains(event.getPlayer().getUniqueId().toString())){
			if(event.getAction()==Action.LEFT_CLICK_AIR || event.getAction()==Action.RIGHT_CLICK_AIR){
				if(event.getPlayer().getItemInHand().getType() == Material.COMPASS){
					Inventory inv = Bukkit.createInventory(null, 9*4, "Teleporter");
					for(String uuid : MoonGames.living){
						Player player = Bukkit.getPlayer(UUID.fromString(uuid));
						if(player != null){
							ItemStack stack = new ItemStack(Material.NAME_TAG);
							ItemMeta meta = stack.getItemMeta();
							meta.setDisplayName(player.getName());
							meta.setLore(Arrays.asList(uuid));
							stack.setItemMeta(meta);
							inv.addItem(stack);
						}
					}
					
					event.getPlayer().openInventory(inv);
					
				}
			}
		}
	}
	
	@EventHandler
	public void onTeleport(InventoryClickEvent event){
		if(event.getInventory().getTitle()=="Teleporter"){
			
			if(event.getWhoClicked() instanceof Player){
				Player p = (Player)event.getWhoClicked();
				if(MoonGames.spectating.contains(p.getUniqueId().toString())){
					if(event.getCurrentItem().getType()==Material.NAME_TAG){
						ItemMeta meta = event.getCurrentItem().getItemMeta();
						String playerName = meta.getDisplayName();
						Player target = Bukkit.getPlayer(playerName);
						if(target != null){
							p.teleport(target);
						}
						
						p.closeInventory();
					}
				}
			}
			
			event.setCancelled(true);
			
			
		}
	}

	
}
