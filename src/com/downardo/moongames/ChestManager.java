package com.downardo.moongames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestManager implements Listener{
	
	public static List<Location> loot = new ArrayList<Location>();
	public static List<Location> chests = new ArrayList<Location>();
	public static Map<Location, Inventory> saves = new HashMap<Location, Inventory>();

	
	public static List<Location> enchanter = new ArrayList<Location>();
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(MoonGames.status == 1 && MoonGames.preparing == false && MoonGames.living.contains(player.getUniqueId().toString())){
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.EMERALD_BLOCK){
				event.setCancelled(true);
				if(ChestManager.saves.containsKey(event.getClickedBlock().getLocation())){
					player.openInventory((Inventory)ChestManager.saves.get(event.getClickedBlock().getLocation()));
				}else{
					Random rnd = new Random();
					int n = 1;
					
					Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST);
					
					
					List<ItemStack> items = new ArrayList<ItemStack>();
					
					if(ChestManager.loot.contains(event.getClickedBlock().getLocation())){
						
						n = rnd.nextInt(7);
						items.add(new ItemStack(Material.DIAMOND));
						items.add(new ItemStack(Material.DIAMOND));
						items.add(new ItemStack(Material.GOLDEN_APPLE));
						items.add(new ItemStack(Material.GOLDEN_APPLE));
						items.add(new ItemStack(Material.DIAMOND_SWORD));
						items.add(new ItemStack(Material.DIAMOND_SWORD));
						
						items.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
						items.add(new ItemStack(Material.DIAMOND_HELMET));
						items.add(new ItemStack(Material.DIAMOND_LEGGINGS));
						items.add(new ItemStack(Material.DIAMOND_BOOTS));
					}else{
						n = rnd.nextInt(4);
						
						int x = 0;
						x = rnd.nextInt(4);
						x = x + 1;
						
						items.add(new ItemStack(Material.IRON_INGOT, x));
						items.add(new ItemStack(Material.IRON_INGOT, x));
						items.add(new ItemStack(Material.IRON_INGOT, x));
						items.add(new ItemStack(Material.IRON_INGOT, x));
						items.add(new ItemStack(Material.IRON_INGOT, x));
						items.add(new ItemStack(Material.GOLD_INGOT, x));
						items.add(new ItemStack(Material.DIAMOND, x));
						items.add(new ItemStack(Material.COBBLESTONE, x * 3));
						items.add(new ItemStack(Material.COBBLESTONE, x * 3));
						items.add(new ItemStack(Material.COBBLESTONE, x * 3));
						items.add(new ItemStack(Material.COBBLESTONE, x * 3));
						items.add(new ItemStack(Material.LOG, x));
						items.add(new ItemStack(Material.LOG, x));
						items.add(new ItemStack(Material.LOG, x));
						items.add(new ItemStack(Material.ENDER_PEARL, x));
						items.add(new ItemStack(Material.LAVA_BUCKET, x));
						items.add(new ItemStack(Material.WATER_BUCKET, x));
						
						items.add(new ItemStack(Material.LEATHER, x));
						items.add(new ItemStack(Material.LEATHER, x));

						items.add(new ItemStack(Material.FISHING_ROD));

						items.add(new ItemStack(Material.FISHING_ROD));
						items.add(new ItemStack(Material.WEB, x));
						items.add(new ItemStack(Material.BOW, x));
						items.add(new ItemStack(Material.ARROW, x));
						
						items.add(new ItemStack(Material.FLINT_AND_STEEL));
						
						items.add(new ItemStack(Material.TNT, x));
						
						items.add(new ItemStack(Material.SNOW_BALL, x*3));
						
						/*Food*/
						items.add(new ItemStack(Material.APPLE,x));
						items.add(new ItemStack(Material.PORK,x));
						items.add(new ItemStack(Material.POTATO_ITEM,x));
						items.add(new ItemStack(Material.POISONOUS_POTATO,x));
						items.add(new ItemStack(Material.CARROT_ITEM,x));
						items.add(new ItemStack(Material.BEACON,x));
						items.add(new ItemStack(Material.PUMPKIN_PIE,x));
						items.add(new ItemStack(Material.MELON, 4*x));
						items.add(new ItemStack(Material.MELON, 4*x));

						
						items.add(new ItemStack(Material.POTION, 1, (short)16457));
						items.add(new ItemStack(Material.POTION, 1, (short)16426));
						items.add(new ItemStack(Material.POTION, 1, (short)16424));
						items.add(new ItemStack(Material.POTION, 1, (short)16386));
						items.add(new ItemStack(Material.POTION, 1, (short)16418));
						items.add(new ItemStack(Material.POTION, 1, (short)16419));
						items.add(new ItemStack(Material.POTION, 1, (short)16417));
						items.add(new ItemStack(Material.POTION, 1, (short)16385));
						items.add(new ItemStack(Material.POTION, 1, (short)16388));

						
						/*Rüstung*/
						


						
					}

					n = n + 1;
					while(n != 0){

						n--;
						Random rnd2 = new Random();
						Random rnd3 = new Random();
						
						int n3 = rnd3.nextInt(27);
						int n2 = rnd2.nextInt(items.size());

						inv.setItem(n3, items.get(n2));
						
						
					}
					
					ChestManager.saves.put(event.getClickedBlock().getLocation(), inv);
					event.getPlayer().openInventory(inv);
					return;
				}
				
				
			}
		}else{
			event.setCancelled(true);
		}
		
	}
	
	
}
