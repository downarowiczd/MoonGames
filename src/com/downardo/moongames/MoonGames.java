package com.downardo.moongames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import com.downardo.moongames.listener.BuildListener;
import com.downardo.moongames.listener.ChatListener;
import com.downardo.moongames.listener.ChunkLoadListener;
import com.downardo.moongames.listener.CreatureSpawnListener;
import com.downardo.moongames.listener.DamageListener;
import com.downardo.moongames.listener.DeathListener;
import com.downardo.moongames.listener.FoodListener;
import com.downardo.moongames.listener.MoveListener;
import com.downardo.moongames.listener.PickUpListener;
import com.downardo.moongames.listener.PingListener;
import com.downardo.moongames.listener.PlayerJoinQuitListener;
import com.downardo.moongames.listener.TrackerListener;
import com.downardo.moongames.listener.WeatherChangeListener;
import com.downardo.moongames.listener.WorldBorder;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class MoonGames extends JavaPlugin{
	
		
	  public static String moonGames = "§a[§9MoonGames§a] ";
	
	  public static MoonGames instance;
	
	  protected HashMap<UUID, Vector> velocities;
	  protected HashMap<UUID, Location> positions;
	  protected HashMap<UUID, Boolean> onGround;
	  
	  public static Location playLocation = null;
	  
	  public static int timing = 0;
	  
	  public static int status = 0;
	  /*
	   * 0 ... Lobby 
	   * 1 ... InGame
	   * 2 ... Restarting
	   */
	  
	  public static boolean peace = true, preparing = false;
	  
	 public static HashMap<String, Integer> points = new HashMap<String, Integer>();

	  
	  protected static String host, user, pass, db, mapPath;
	  
	  public static Location lobby = null;
	  
	  public static List<String> living = new ArrayList<String>();
	  public static List<String> spectating = new ArrayList<String>();
	  
	  
	  public static Map<String, Integer> kills = new HashMap<String, Integer>();
	  public static List<String> death = new ArrayList<String>();

	  public static boolean startingGame = false;
	  
	
	  public static String lobbyID = "lobby";
	  
	  
	  public static int border = 650;
	  
	  
	  public static double gravitation = 1D;
	  
	  public static boolean ending = false;
	  
	  public static String playingMapName = "Map";
	  public static int playingMapId = 0;
	  public static List<Location> playingMapSpawns = null;
	  
	  public static boolean spawnedEnderDragon = false;
	  
	  public static List<String> premium = new ArrayList<String>();
	  
	@Override
	public void onDisable() {
		
		deleteFolder(new File("playMap/region"));
		deleteFolder(new File("playMap/playerdata"));
		deleteFolder(new File("playMap"));
		
		Bukkit.getScheduler().cancelTasks(this);
		status = 2;
		MySQL.close();
	}
	
	
	@Override
	public void onEnable() {
		PluginManager pm = Bukkit.getPluginManager();

		instance = this;
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		status = 2;
		pm.registerEvents(new PingListener(), this);
		//new GravityTask(this).runTaskLater(this, 1L);
		this.velocities = new HashMap<UUID, Vector>();
		this.onGround = new HashMap<UUID, Boolean>();
		this.positions = new HashMap<UUID, Location>();
		
		this.getConfig().addDefault("MySQL.Host", "localhost");
		this.getConfig().addDefault("MySQL.User", "username");
		this.getConfig().addDefault("MySQL.Pass", "password");
		this.getConfig().addDefault("MySQL.Database", "database");
		this.getConfig().addDefault("Maps.Path", "/home/minecraft/maps/");
		this.getConfig().addDefault("Lobby.ID", "lobby");
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		
		host = this.getConfig().getString("MySQL.Host");
		user = this.getConfig().getString("MySQL.User");
		pass = this.getConfig().getString("MySQL.Pass");
		db = this.getConfig().getString("MySQL.Database");
		mapPath = this.getConfig().getString("Maps.Path");
		lobbyID = this.getConfig().getString("Lobby.ID");
		
		MySQL.connect();
		MySQL.Update("CREATE TABLE IF NOT EXISTS moongames_user (id INT AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(255), username VARCHAR(30), kills INT(255), deaths INT(255), wins INT(255), points INT(255))");
		MySQL.Update("CREATE TABLE IF NOT EXISTS moongames_lobby (id INT(10), world VARCHAR(30), x DOUBLE, y DOUBLE, z DOUBLE)");
		MySQL.Update("CREATE TABLE IF NOT EXISTS moongames_maps (id INT AUTO_INCREMENT PRIMARY KEY, world VARCHAR(30), x DOUBLE, y DOUBLE, z DOUBLE)");
		MySQL.Update("CREATE TABLE IF NOT EXISTS moongames_maps_spawns (id INT AUTO_INCREMENT PRIMARY KEY, map_id INT(30), x DOUBLE, y DOUBLE, z DOUBLE)");
		
		
		/*Load Lobby*/
		try{
			ResultSet rs = MySQL.Query("SELECT world, x, y, z FROM moongames_lobby WHERE id='1' LIMIT 1");
			while(rs.next()){
				Bukkit.unloadWorld(rs.getString(1), false);
				deleteFolder(new File(rs.getString(1)));
				copyDirectory(new File(mapPath + rs.getString(1)), new File(rs.getString(1)));
				Bukkit.createWorld(new WorldCreator(rs.getString(1)));
				lobby = new Location(Bukkit.getWorld(rs.getString(1)), rs.getDouble(2), rs.getDouble(3), rs.getDouble(4));
			}
			rs.close();
		}catch(Exception e){
			System.err.println(e);
		}
		Bukkit.unloadWorld("playMap", false);
		deleteFolder(new File("playMap/region"));
		deleteFolder(new File("playMap/playerdata"));
		deleteFolder(new File("playMap"));
		
		/*
		/*generate map
		WorldCreator creator = new WorldCreator("playMap");
		creator.environment(Environment.THE_END);
		Bukkit.createWorld(creator);
		
		//playLocation = Bukkit.getWorld("playMap").getSpawnLocation();
		
		*/
		
		
		try{
			ResultSet rs = MySQL.Query("SELECT world, x ,y, z, id FROM moongames_maps ORDER BY RAND() LIMIT 1");
			while(rs.next()){
				String mapname = rs.getString(1);
				double x = rs.getDouble(2);
				double y = rs.getDouble(3);
				double z = rs.getDouble(4);
				playingMapId = rs.getInt(5);
				copyDirectory(new File(mapPath + mapname), new File("playMap"));
				WorldCreator wc = new WorldCreator("playMap");
				wc.environment(Environment.THE_END);
				Bukkit.createWorld(wc);
				
				playLocation = new Location(Bukkit.getWorld("playMap"), x, y, z);
				
				
				
				
			}
			rs.close();
		}catch(Exception e){
			
		}
		
		
		//Load Spawns
		
		try {
			playingMapSpawns = getMapSpawns();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		

		pm.registerEvents(new WorldBorder(this), this);
		pm.registerEvents(new PlayerJoinQuitListener(), this);
		pm.registerEvents(new DamageListener(), this);
		pm.registerEvents(new FoodListener(), this);
		pm.registerEvents(new MoveListener(), this);
		pm.registerEvents(new ChunkLoadListener(), this);
		pm.registerEvents(new BuildListener(), this);
		pm.registerEvents(new WeatherChangeListener(), this);
		pm.registerEvents(new CreatureSpawnListener(), this);
		pm.registerEvents(new TrackerListener(), this);
		pm.registerEvents(new ChestManager(), this);
		pm.registerEvents(new DeathListener(), this);
		pm.registerEvents(new Spectator(), this);
		pm.registerEvents(new ChatListener(), this);
		
		pm.registerEvents(new PickUpListener(), this);
		pm.registerEvents(new Spectator(), this);
		status = 0;
		mainLoop();
		
		
		
		playLocation.getWorld().loadChunk(playLocation.getChunk());
		playLocation.getWorld().getChunkAt(playLocation).load();
		//MoonGames.spawnPlaySpawn(playLocation);
		playLocation.getWorld().loadChunk(playLocation.getChunk());
		playLocation.getWorld().getChunkAt(playLocation).load(); 
		
		
		
		for(int i = 0; i < 1000; i++){
			MoonGames.spawnChest();
		}
		
		for(int i = 0; i < 100; i++){
			MoonGames.spawnEnchanter();
		}
		
		
		
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player)sender;
			if(p.isOp()){
				if(command.getName().equalsIgnoreCase("setlobby")){
					Location lobbyLocation = p.getLocation();
					if(lobby != null){
						MySQL.Update("DELETE FROM moongames_lobby WHERE id='1'");
					}
					
					MySQL.Update("INSERT INTO moongames_lobby (id, world, x, y, z) VALUES ('1', '" + lobbyLocation.getWorld().getName() + "', '" + lobbyLocation.getX() + "', '" + lobbyLocation.getY() + "', '" + lobbyLocation.getZ() + "')");
					p.sendMessage(MoonGames.moonGames + "Die Lobby wurde erfolgreich gesetzt!");
					lobby = lobbyLocation;
				}
				
				if(command.getName().equalsIgnoreCase("setmap")){
					if(args.length == 1){
						boolean isInData = false;
						try{
							ResultSet rs = MySQL.Query("SELECT id FROM moongames_maps WHERE world='" + args[0] + "'");
							while(rs.next()){
								isInData = true;
							}
							rs.close();
						}catch(Exception e){
							System.err.println(e);
						}
						
						
						if(isInData){
							p.sendMessage("§cDie Map gibt es schon!");
							return true;
						}
						MySQL.Update("INSERT INTO moongames_maps (world, x, y, z) VALUES ('" + args[0] + "', '" + p.getLocation().getX() + "', '" + p.getLocation().getY() + "', '" + p.getLocation().getZ() + "')");
						
						p.sendMessage(MoonGames.moonGames + "Die Map wurde erfolgreich restetet!");
						
					}else{
						p.sendMessage("§cVerwendung: /setmap [MapName]");
					}
				}
				
				if(command.getName().equalsIgnoreCase("statsMoon")){
					int x = 0;
					try{
						ResultSet rs = MySQL.Query("SELECT id FROM moongames_user");
						while(rs.next()){
							x++;
						}
						rs.close();
					}catch(Exception e){
						System.err.println(e);
					}
					p.sendMessage("Es spielten schon " + x + " User MoonGames!");
				}
				
				
			}
		}
		return true;
	}

	
	public static boolean isInReg(String username){
		boolean is = false;
		
		try{
			ResultSet rs = MySQL.Query("SELECT id FROM moongames_user WHERE username='" + username + "' LIMIT 1");
			while(rs.next()){
				is = true;
			}
			rs.close();
		}catch(Exception e){
			System.err.println(e);
		}
		
		return is;
		
	}
	
	public static UUID getUUIDbyUsername(String username){
		UUID uuid = null;
		try{
			ResultSet rs = MySQL.Query("SELECT uuid FROM moongames_user WHERE username='" + username + "' LIMIT 1");
			while(rs.next()){
				uuid = UUID.fromString(rs.getString(1));
			}
			rs.close();
		}catch(Exception e){
			System.err.println(e);
		}
		
		return uuid;
	}
	
	
	public void UpdateVelocities() {
			if(MoonGames.status == 1 && MoonGames.gravitation != 1.0D && MoonGames.preparing == false){
				
				World world = Bukkit.getWorld("playMap");
				for (Iterator<Entity> j = world.getEntities().iterator(); j.hasNext();) {
					Entity e = (Entity) j.next();
					Vector newv = e.getVelocity().clone();
					UUID uuid = e.getUniqueId();

					if ((this.velocities.containsKey(uuid))
							&& (this.onGround.containsKey(uuid))
							&& (!e.isOnGround()) && (!e.isInsideVehicle())) {
						Vector oldv = (Vector) this.velocities.get(uuid);
						if (!((Boolean) this.onGround.get(uuid)).booleanValue()) {
							Vector d = oldv.clone();
							d.subtract(newv);
							double dy = d.getY();
							if ((dy > 0.0D)
									&& ((newv.getY() < -0.01D) || (newv.getY() > 0.01D))) {
								double gravity = MoonGames.gravitation;

								newv.setY(oldv.getY() - dy * gravity);
								boolean newxchanged = (newv.getX() < -0.001D)
										|| (newv.getX() > 0.001D);
								boolean oldxchanged = (oldv.getX() < -0.001D)
										|| (oldv.getX() > 0.001D);
								if ((newxchanged) && (oldxchanged)) {
									newv.setX(oldv.getX());
								}

								boolean newzchanged = (newv.getZ() < -0.001D)
										|| (newv.getZ() > 0.001D);
								boolean oldzchanged = (oldv.getZ() < -0.001D)
										|| (oldv.getZ() > 0.001D);
								if ((newzchanged) && (oldzchanged)) {
									newv.setZ(oldv.getZ());
								}
								e.setVelocity(newv.clone());
							}
						} else if (((e instanceof Player))
								&& (this.positions.containsKey(uuid))) {
							
							Player pe = (Player)e;
							if(pe.getGameMode()==GameMode.CREATIVE){
								return;
							}
							
							if(MoonGames.spectating.contains(pe.getUniqueId().toString())){
								return;
							}
							
							
							Vector pos = e.getLocation().toVector();
							Vector oldpos = ((Location) this.positions.get(uuid))
									.toVector();
							Vector velocity = pos.subtract(oldpos);
							newv.setX(velocity.getX());
							newv.setZ(velocity.getZ());
						}

						e.setVelocity(newv.clone());
					}

					this.velocities.put(uuid, newv.clone());
					this.onGround.put(uuid, Boolean.valueOf(e.isOnGround()));
					this.positions.put(uuid, e.getLocation());
				}
				
				
				
				
				
				
				
			}
	}
	
	
	private void mainLoop(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			
			@Override
			public void run() {
				if(status == 0){
					if(Bukkit.getOnlinePlayers().size() >= 1){
						if(!startingGame){
							startingGame = true;
							new StartingGameTask().go();
						}
					}
					
				}
				
				
				if(status == 1 && preparing == false){
					
					
					if(MoonGames.living.size() == 1){
						status = 2;
						ending = true;
						final String winner = MoonGames.living.get(0);
						Player winner_player = Bukkit.getPlayer(UUID.fromString(winner));
						if(winner_player != null){
							Bukkit.broadcastMessage(MoonGames.moonGames + "§e" + winner_player.getName() + " §ahat das Spiel gewonnen!");
						}else{
							Bukkit.broadcastMessage(MoonGames.moonGames + "Das Spiel ist zu ende!");
						}
						
						restart(winner);
						
						return;
						
					}
					
					if(MoonGames.living.size() == 0){
						status = 2;
						ending = true;
						Bukkit.broadcastMessage(MoonGames.moonGames + "Das Spiel ist zu ende!");
						
						restart(null);
						
						return;

					}
					
					if(MoonGames.living.size() <= 3 && !MoonGames.spawnedEnderDragon){
						MoonGames.spawnedEnderDragon = true;
						Bukkit.broadcastMessage(MoonGames.moonGames + "§cEin MoonDragon wurde gespawnt!");
						Entity e = Bukkit.getWorld("playMap").spawnEntity(MoonGames.playLocation, EntityType.ENDER_DRAGON);
						EnderDragon d = (EnderDragon)e;
						d.setCustomName("MoonDragon");
						d.setCustomNameVisible(true);
						
						Bukkit.broadcastMessage(MoonGames.moonGames + "§a§lAlle Kisten wurden neu aufgefüllt!");
						ChestManager.saves.clear();
						
					}
					
					
					if(timing == 35 ||timing == 60 || timing == 90 || timing == 100 || timing == 120){
						for(int x = 0; x < 8; x++){
							MoonGames.removeChest(ChestManager.chests.remove(x));
							MoonGames.spawnChest();
						}
						
						Collections.shuffle(ChestManager.chests);
						
						for(int x = 0; x < 4; x++){
							MoonGames.removeEnchanter(ChestManager.enchanter.remove(x));
							MoonGames.spawnEnchanter();
						}
						
						Collections.shuffle(ChestManager.enchanter);
						
					}
					
					
					if(timing == 60){
						/*Change Gravity*/
						double gravity = 1.0D;
						gravity = 0.2D + (Math.random()*1.0D);
						MoonGames.gravitation = gravity;
						Bukkit.broadcastMessage(MoonGames.moonGames + "§c§lDie Gravitation wurde beeinflusst!");
					}
					
					
					if(timing >= 120){
						timing = 0;
					}
					
					timing++;
					
				}
				
			}
		}, 20L, 20L);
		
		
		
	}
	
	
	
	  public static void deleteFolder(File folder) {
		    File[] files = folder.listFiles();
		    if (files != null) {
		      for (File f : files) {
		        if (f.isDirectory())
		          deleteFolder(f);
		        else {
		          f.delete();
		        }
		      }
		    }
		    folder.delete();
		  }

		  public static void copyDirectory(File sourceLocation, File targetLocation)throws IOException{
		    if (sourceLocation.isDirectory()) {
		      if (!targetLocation.exists()) {
		        targetLocation.mkdir();
		      }

		      String[] children = sourceLocation.list();
		      for (int i = 0; i < children.length; i++)
		        copyDirectory(new File(sourceLocation, children[i]), 
		          new File(targetLocation, children[i]));
		    }else{
		      InputStream in = new FileInputStream(sourceLocation);
		      OutputStream out = new FileOutputStream(targetLocation);

		      byte[] buf = new byte[1024];
		      int len;
		      while ((len = in.read(buf)) > 0){
		        out.write(buf, 0, len);
		      }
		      in.close();
		      out.close();
		    }
		  }
	
	
	
		  public static void sendToLobby(Player p){
			  ByteArrayDataOutput out = ByteStreams.newDataOutput();
			  out.writeUTF("Connect");
			  out.writeUTF(MoonGames.lobbyID);
			  
			  p.sendPluginMessage(Bukkit.getPluginManager().getPlugin("MoonGames"), "BungeeCord", out.toByteArray());
		  }
		  
		  
		  
		  @Deprecated
		  public static void spawnPlaySpawn(Location loc){
			  
			  YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new InputStreamReader(Bukkit.getPluginManager().getPlugin("MoonGames").getResource("spawn.build")));
				    World w = loc.getWorld();
				    for (String key : cfg.getKeys(false)) {
				      String[] s = key.split(",");
				      int x = Integer.parseInt(s[0]) + loc.getBlockX();
				      int y = Integer.parseInt(s[1]) + loc.getBlockY();
				      int z = Integer.parseInt(s[2]) + loc.getBlockZ();
				      int type = Integer.parseInt(s[3]);
				      byte data = Byte.parseByte(s[4]);
				      w.getBlockAt(x, y, z).setTypeIdAndData(type, data, false);
				    }
		  }
		  @Deprecated
		  public static void teleportMapOld(Player p, int i){
			  List<Location> spawns = new ArrayList<Location>();
			  Location loc = MoonGames.playLocation;
			  spawns.add(loc.clone().add(-1.0D, 0, -9.0D));
			  spawns.add(loc.clone().add(1.0D, 0, 9.0D));
			  spawns.add(loc.clone().add(3.0D, 0, -8.0D));
			  spawns.add(loc.clone().add(5.0D, 0, -6.0D));
			  spawns.add(loc.clone().add(7.0D, 0, -4.0D));
			  spawns.add(loc.clone().add(8.0D, 0, -2.0D));
			  spawns.add(loc.clone().add(9.0D, 0, -1.0D));
			  spawns.add(loc.clone().add(9.0D, 0, 1.0D));
			  spawns.add(loc.clone().add(8.0D, 0, 3.0D));
			  spawns.add(loc.clone().add(7.0D, 0, 5.0D));
			  spawns.add(loc.clone().add(5.0D, 0, 7.0D));
			  spawns.add(loc.clone().add(3.0D, 0, 8.0D));
			  spawns.add(loc.clone().add(-1.0D, 0, 9.0D));
			  spawns.add(loc.clone().add(-2.0D, 0, 8.0D));
			  spawns.add(loc.clone().add(-5.0D, 0, 7.0D));
			  spawns.add(loc.clone().add(-7.0D, 0, 5.0D));
			  spawns.add(loc.clone().add(-8.0D, 0, 3.0D));
			  spawns.add(loc.clone().add(-9.0D, 0, 1.0D));
			  spawns.add(loc.clone().add(-9.0D, 0, -1.0D));
			  spawns.add(loc.clone().add(-8.0D, 0, -3.0D));
			  spawns.add(loc.clone().add(-7.0D, 0, -5.0D));
			  spawns.add(loc.clone().add(-7.0D, 0, -7.0D));
			  spawns.add(loc.clone().add(-3.0D, 0, -8.0D));

			  p.teleport(spawns.get(i));
		  }
		  
		  public static void teleportMap(){
			  List<Location> spawns = playingMapSpawns;
			  int tp = 0;
			  List<Player> players = new ArrayList<Player>();
			  players.addAll(Bukkit.getOnlinePlayers());
			  for(int i = 0; i < players.size(); i++){
				  Player p = players.get(i);
				  if(p.isInsideVehicle()){
				  	p.leaveVehicle();
				  	if(p.getVehicle() instanceof Horse){
				  		Horse h = (Horse)p.getVehicle();
				  		h.setHealth(0.0);
				  		h.remove();
				  	}
				  }
				  
				  if(tp >= spawns.size()){
					  tp = 0;
				  }
				  
				  Location loc = spawns.get(tp);
				  p.teleport(loc.add(0.0, 0.5,0.0));
				  
				  if(!(tp >= spawns.size())){
					  tp++;
				  }
				  
				  if(tp >= spawns.size()){
					  tp = 0;
				  }
				  
				  
			  }
			  
		  }
		  
		  public static List<Location> getMapSpawns() throws SQLException{
			  List<Location> spawns = new ArrayList<Location>();
			  
			  ResultSet rs = MySQL.Query("SELECT x, y, z FROM moongames_maps_spawns WHERE map_id='" + playingMapId + "'");
			  while(rs.next()){
				spawns.add(new Location(playLocation.getWorld(), rs.getDouble(1), rs.getDouble(2), rs.getDouble(3)));  
			  }
			  rs.close();
			  return spawns;
		  }
		  
		  public static void setChest(Location loc, boolean special){
			  loc.getWorld().getBlockAt(loc).setType(Material.EMERALD_BLOCK);
			  loc.getWorld().playSound(loc, Sound.ENDERMAN_TELEPORT, 20.0F, 20.0F);
			  if(special){
				  ChestManager.loot.add(loc);
			  }else{
				  ChestManager.chests.add(loc);
			  }
			  
		  }
		  
		  public static void removeChest(Location loc){
			  loc.getWorld().getBlockAt(loc).setType(Material.AIR);
			  loc.getWorld().playSound(loc, Sound.ENDERMAN_TELEPORT, 20.0F, 20.0F);
			  if(ChestManager.chests.contains(loc)){
				  ChestManager.chests.remove(loc);
			  }
			  
			  if(ChestManager.saves.containsKey(loc)){
				  ChestManager.saves.remove(loc);
			  }
		  }
		  
		  
		  public static void spawnChest(){
			  Random rnd = new Random();
			  
			  Location loc = MoonGames.playLocation.clone();
				  int x = rnd.nextInt(800) - rnd.nextInt(200);
				  int z = rnd.nextInt(800) - rnd.nextInt(200);
				  loc.setX(loc.getBlockX() + x);
				  loc.setZ(loc.getBlockZ() + z);
				  loc.setY(loc.getWorld().getHighestBlockYAt(loc));
					  			  
			  setChest(loc, false);
			  
		  }
		  
		  
		  
		  
		  
		  
		  
		  public static void setEnchanter(Location loc){
			  loc.getWorld().getBlockAt(loc).setType(Material.ENCHANTMENT_TABLE);
			  loc.getWorld().playSound(loc, Sound.ENDERMAN_TELEPORT, 20.0F, 20.0F);
			  ChestManager.enchanter.add(loc);
		  }
		  
		  public static void removeEnchanter(Location loc){
			  loc.getWorld().getBlockAt(loc).setType(Material.AIR);
			  loc.getWorld().playSound(loc, Sound.ENDERMAN_TELEPORT, 20.0F, 20.0F);
			  if(ChestManager.enchanter.contains(loc)){
				  ChestManager.enchanter.remove(loc);
			  }
		  }
		  
		  
		  public static void spawnEnchanter(){
			  Random rnd = new Random();
			  
			  Location loc = MoonGames.playLocation.clone();
				  int x = rnd.nextInt(500);
				  int z = rnd.nextInt(500);
				  loc.setX(loc.getBlockX() + x);
				  loc.setZ(loc.getBlockZ() + z);
				  loc.setY(loc.getWorld().getHighestBlockYAt(loc));	  
			  
			  setEnchanter(loc);
			  
		  }
		  
		  private void restart(final String winner){
			  
			  Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("MoonGames"), new Runnable() {
					int timer = 12;
					@Override
					public void run() {
						if(timer != 0){
							if((timer == 20 || timer == 15 || timer == 10) || (timer <= 5 && timer > 0)){
								Bukkit.broadcastMessage(MoonGames.moonGames + "§cDer Server startet in " + timer + " Sekunden neu!");
							}
							
							if(timer == 12){
								
								
								for(String uuid : MoonGames.death){
									int deaths = 0;
									try{
										ResultSet rs = MySQL.Query("SELECT deaths FROM moongames_user WHERE uuid='" + uuid + "'");
										while(rs.next()){
											deaths = rs.getInt(1);
										}
										rs.close();
									}catch(Exception e){
										System.err.println(e);
									}
									
									deaths = deaths + 1;
									MySQL.Update("UPDATE moongames_user SET deaths='" + deaths + "' WHERE uuid='" + uuid + "'");
									
								}
								
								
								for(String uuid : MoonGames.kills.keySet()){
									int kills = 0;
									try{
										ResultSet rs = MySQL.Query("SELECT kills FROM moongames_user WHERE uuid='" + uuid + "'");
										while(rs.next()){
											kills = rs.getInt(1);
										}
										rs.close();
									}catch(Exception e){
										System.err.println(e);
									}
									
									kills = kills + MoonGames.kills.get(uuid);
									MySQL.Update("UPDATE moongames_user SET kills='" + kills + "' WHERE uuid='" + uuid + "'");
									
									points.put(uuid, 0);
									
									if(MoonGames.premium.contains(uuid)){
										points.put(uuid, points.get(uuid) + MoonGames.kills.get(uuid)*4);
									}else{
										points.put(uuid, points.get(uuid) + MoonGames.kills.get(uuid)*2);
									}
									
								}
								
								
								if(winner != null){
									
									int wins = 0;
									try{
										ResultSet rs = MySQL.Query("SELECT wins FROM moongames_user WHERE uuid='" + winner + "'");
										while(rs.next()){
											wins = rs.getInt(1);
										}
										rs.close();
									}catch(Exception e){
										System.err.println(e);
									}
									
									wins = wins + 1;
									
									MySQL.Update("UPDATE moongames_user SET wins='" + wins + "' WHERE uuid='" + winner + "'");
									
									if(MoonGames.premium.contains(winner)){
										points.put(winner, points.get(winner) + 25*2);
									}else{
										points.put(winner, points.get(winner) + 25);
									}
									
								}
								
								
								for(String uuid : points.keySet()){
									int point = 0;
									try{
										ResultSet rs = MySQL.Query("SELECT points FROM moongames_user WHERE uuid='" + uuid + "'");
										while(rs.next()){
											point = rs.getInt(1);
										}
										rs.close();
									}catch(Exception e){
										System.err.println(e);
									}
									
									
									point = point + points.get(uuid);
									
									
									MySQL.Update("UPDATE moongames_user SET points='" + point + "' WHERE uuid='" + uuid + "'");
								}

								
								
							}
							
						}else{
							
							
							Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("MoonGames"), new Runnable() {
								
								@Override
								public void run() {
									Bukkit.shutdown();
								}
							}, 20L*1);
							
							
							
						}
						
						timer--;
						
					}
				}, 20L, 20L);
			  
			  
			  
			  
			  
		  }
		  
		  
		  
		  
		  /*public static void sendTabList(Player p, String header, String footer){
			  if(header == null) header = "";
			  if(footer == null) footer = "";
			  
			  PlayerConnection con = ((CraftPlayer)p).getHandle().playerConnection;
			  
			  IChatBaseComponent tabheader = ChatSerializer.a("{\"text\": \"" + header + "\"}");
			  IChatBaseComponent tabfooter = ChatSerializer.a("{\"text\": \"" + footer + "\"}");

			  PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(tabheader);
			  
			  try{
				  Field f = packet.getClass().getDeclaredField("b");
				  f.setAccessible(true);
				  f.set(packet, tabfooter);
			  }catch(Exception e){
				  System.err.println(e);
			  }finally{
				  con.sendPacket(packet);
			  }
		  }*/
		  
		/*  public static void sendActionBar(Player p, String message){
			  if(message == null) message = "";
			  
			  PlayerConnection con = ((CraftPlayer)p).getHandle().playerConnection;
			  
			  IChatBaseComponent chat = ChatSerializer.a("{\"text\": \"" + message + "\"}");
			  PacketPlayOutChat packet = new PacketPlayOutChat(chat, (byte)2);
			  
			  con.sendPacket(packet);
			  
		  }*/
		  
		  /*public static void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut){
			  if(title == null) title = "";
			  if(subtitle == null) subtitle = "";

			  
			  PlayerConnection con = ((CraftPlayer)p).getHandle().playerConnection;
			  
			  IChatBaseComponent titleChat = ChatSerializer.a("{\"text\": \"" + title + "\"}");
			  IChatBaseComponent subtitleChat = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");

			  PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleChat, fadeIn, stay, fadeOut);
			  PacketPlayOutTitle packetSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleChat, fadeIn, stay, fadeOut);

			  con.sendPacket(packetTitle);
			  con.sendPacket(packetSubTitle);
			  
		  }*/
		  
		  
		  
		  
		  public static void setScoreboard(Player p){
			  
			  org.bukkit.scoreboard.Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			  
			  Objective obj = board.registerNewObjective("stats", "dummy");
			  
			  obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			  obj.setDisplayName("§5§lMoonGames");
			  
			  int kills = 0;
			  int deaths = 0;
			  int wins = 0;
			  int points = 0;
			  
			  
			  try{
				  ResultSet rs = MySQL.Query("SELECT kills, deaths, wins, points FROM moongames_user WHERE uuid='" + p.getUniqueId().toString() + "'");
				  while(rs.next()){
					  kills = rs.getInt(1);
					  deaths = rs.getInt(2);
					  wins = rs.getInt(3);
					  points = rs.getInt(4);
				  }
				  rs.close();
			  }catch(Exception e){
				  System.err.println(e);
			  }
			  
			  
			  obj.getScore("§aKills: §e").setScore(kills);
			  obj.getScore("§aTode: §e").setScore(deaths);
			  obj.getScore("§aWins: §e").setScore(wins);
			  obj.getScore("§aPunkte: §e").setScore(points);

			  
			  p.setScoreboard(board);
			  
//			  Scoreboard sboard = Bukkit.getScoreboardManager().getNewScoreboard();
//
//			  Objective obj = sboard.registerNewObjective("lobbyScoreboard", "dummy");
//
//			  obj.setDisplaySlot(DisplaySlot.SIDEBAR);
//			  obj.setDisplayName("§3Willkommen!");
//
//			  obj.getScore(" ").setScore(13);
//			  obj.getScore("§b§lSpieler online:").setScore(12);
//			  obj.getScore("§e0").setScore(11);
//			  obj.getScore(" ").setScore(0);
//
//			  pl.setScoreboard(sboard);
			  
			  
			  
		  }
		  
		  private static final String GROUP_PREFIX = "group.";
		  
		  public static Set<String> getGroupsForPlayer(Player player) {
			  Set<String> groups = new HashSet<String>();
			  for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
			        if (!pai.getPermission().startsWith(GROUP_PREFIX) || !pai.getValue())
			            continue;
			        groups.add(pai.getPermission().substring(GROUP_PREFIX.length()));
			    }
			    return groups;
			}
		  
		  
		  public static String getUserPrefix(Player player){
			  Set<String> groups = MoonGames.getGroupsForPlayer(player);
				
				String prefix = "§8";
				
				if(groups.contains("vip")){
					prefix = "§6VIP ";
				}
				
				if(groups.contains("youtuber")){
					prefix ="§5YouTuber ";
				}
				
				if(groups.contains("scout")){
					prefix ="§9Scout ";
				}
				
				if(groups.contains("supporter")){
					prefix ="§2Supporter ";
				}
				
				if(groups.contains("developer")){
					prefix ="§7Developer ";
				}
				
				if(groups.contains("admin")){
					prefix ="§cAdmin ";
				}
				
				if(groups.contains("owner")){
					prefix ="§4Owner ";
				}
				
				return prefix;
		  }
		  
		  
		  public static String getUserColor(Player player){
			  Set<String> groups = MoonGames.getGroupsForPlayer(player);
				
				String prefix = "§8";
				
				if(groups.contains("vip")){
					prefix = "§6";
				}
				
				if(groups.contains("youtuber")){
					prefix ="§5";
				}
				
				if(groups.contains("scout")){
					prefix ="§9";
				}
				
				if(groups.contains("supporter")){
					prefix ="§2";
				}
				
				if(groups.contains("developer")){
					prefix ="§7";
				}
				
				if(groups.contains("admin")){
					prefix ="§c";
				}
				
				if(groups.contains("owner")){
					prefix ="§4";
				}
				
				return prefix;
		  }
		  
		  
		  public static Integer getPoints(String uuid){
			  int point = 0;
			  try{
					ResultSet rs = MySQL.Query("SELECT points FROM moongames_user WHERE uuid='" + uuid + "'");
					while(rs.next()){
						point = rs.getInt(1);
					}
					rs.close();
				}catch(Exception e){
					System.err.println(e);
				}
			  
			  return point;
		 }
		  
		 
		 public static void setPoints(String uuid, int point){
				MySQL.Update("UPDATE moongames_user SET points='" + point + "' WHERE uuid='" + uuid + "'");
		 }
		  
	
}
