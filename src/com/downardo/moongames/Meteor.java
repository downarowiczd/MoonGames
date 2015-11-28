package com.downardo.moongames;




import net.minecraft.server.v1_8_R3.EntityFireball;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Location;


public class Meteor extends EntityFireball{

	
	public Meteor(World world){
		super(world);
	}
	
	  private float speedMod = 0.1F;
	  private float explosionRadius = 5F;
	  private float trailPower = 10F;
	 // private float brightness = 10F;
	  
	  
	  
	@Override
	  public void h(){
	    world.createExplosion(this, locX, locY, locZ, trailPower, true, true);
	 
	    motX *= speedMod;
	    motY *= speedMod;
	    motZ *= speedMod;
	 
	    
	    
	    super.h();
	  }
	 
	  @Override
	  public void a(MovingObjectPosition movingobjectposition){
	    world.createExplosion(this, locX, locY, locZ, explosionRadius, true, true);
	 //   world.getWorld().getBlockAt(new Location(world.getWorld(), locX, locY, locZ)).setType(Material.ENDERCHEST);
	    MoonGames.setChest(new Location(world.getWorld(), locX, locY, locZ), true);
	    die();
	  }
	  
	
	 
//	  @Override
//	  public float c(float f){
//	    return this.brightness;
//	  }

	
	
}
