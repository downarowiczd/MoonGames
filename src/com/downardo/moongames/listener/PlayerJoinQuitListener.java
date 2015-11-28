package com.downardo.moongames.listener;

import java.sql.ResultSet;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.downardo.moongames.MoonGames;
import com.downardo.moongames.MySQL;
import com.downardo.moongames.Spectator;

public class PlayerJoinQuitListener implements Listener{

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		final Player p = event.getPlayer();
		event.setJoinMessage(MoonGames.moonGames + "§e" + p.getName() + " §aist dem Spiel beigetreten!");
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.setLevel(0);
		p.setFoodLevel(20);
		p.setGameMode(GameMode.SURVIVAL);
		if(MoonGames.isInReg(p.getName())){
			String dusername = p.getName();
			
			try{
				ResultSet rs = MySQL.Query("SELECT username FROM moongames_user WHERE uuid='" + p.getUniqueId().toString() + "' LIMIT 1");
				while(rs.next()){
					dusername = rs.getString(1);
				}
				rs.close();
			}catch(Exception e){
				System.err.println(e);
			}
			
			if(!p.getName().equalsIgnoreCase(dusername)){
				MySQL.Update("UPDATE moongames_user SET username='" + p.getName() + "' WHERE uuid='" + p.getUniqueId().toString() + "'");
			}
			
		}else{
			MySQL.Update("INSERT INTO moongames_user (uuid, username, kills, deaths, wins, points) VALUES ('" + p.getUniqueId().toString() + "', '" + p.getName() + "', '0', '0', '0', '100')");
		}
		
		
		
		if(MoonGames.status == 0){
			  Set<String> groups = MoonGames.getGroupsForPlayer(p);
			  if(groups.contains("vip") || groups.contains("youtuber") || groups.contains("scout") || groups.contains("supporter") || groups.contains("developer") || groups.contains("admin") || groups.contains("owner")){
				  MoonGames.premium.add(p.getUniqueId().toString());
			  }

			if(MoonGames.lobby != null){
				p.teleport(MoonGames.lobby);
				ItemStack skills = new ItemStack(Material.QUARTZ);
				ItemMeta skills_meta = skills.getItemMeta();
				skills_meta.setDisplayName("Skills");
				skills.setItemMeta(skills_meta);
				p.getInventory().addItem(skills);
				ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
				BookMeta bm = (BookMeta)book.getItemMeta();
				bm.setAuthor("Mondopia Server Team");
				bm.setTitle("Anleitung");
				bm.setDisplayName("§7 >>> §3Anleitung §7<<<");
				bm.addPage("Willkommen\nIn MoonGames geht es darum alle Gegner auszuschalten, um zu gewinnen. In bestimmten Abständen ändert sich die Gravitation(Anziehungskraft), diese kann das Spielgeschehen drastisch ändern.\n Es spawnen zufälligen EnderChests, welcher dir Items liefern.\n Bei 3 oder weniger Spielern spawnt ein MoonDragon, welcher euch das Überleben schwerer Macht!");
				book.setItemMeta(bm);
				p.getInventory().addItem(book);
			}
//			
//			if(p.getName().equalsIgnoreCase("DOWNARDO")){
//				MoonGames.setPoints(p.getUniqueId().toString(), 10000);
//			}
			MoonGames.setScoreboard(p);
			
		}
		
		String prefix = MoonGames.getUserColor(p);
		
		String newname = prefix + p.getName();
		if(newname.length() > 16){
			newname = newname.substring(0, 16);
		}
		
		p.setPlayerListName(newname);
		
		if(MoonGames.status == 1){
			Spectator.setSpectator(p);
			p.teleport(MoonGames.playLocation.clone().add(0, 4, 0));
			p.getInventory().addItem(new ItemStack(Material.COMPASS));
		}
		
		
		//MoonGames.sendTabList(p, "§aMoonGames", "§aProdukt von Mondopia.de");		
		
		
		
		
		
	}
	
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player p = event.getPlayer();
		event.setQuitMessage(MoonGames.moonGames + "§e" + p.getName() + " §ahat das Spiel verlassen!");
		
		
		if(MoonGames.living.contains(p.getUniqueId().toString())){
			MoonGames.living.remove(p.getUniqueId().toString());
		}
		
	}
	
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event){
		Player p = event.getPlayer();		
		
		if(MoonGames.living.contains(p.getUniqueId().toString())){
			MoonGames.living.remove(p.getUniqueId().toString());
		}
		
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event){
		if(MoonGames.status == 2){
			event.setKickMessage("Das Spiel läuft schon!");
			event.setResult(Result.KICK_OTHER);
			event.disallow(event.getResult(), event.getKickMessage());
		}
	}
	
	
}
