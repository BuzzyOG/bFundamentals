package uk.codingbadgers.banimalcare;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import uk.codingbadgers.bFundamentals.module.Module;

public class bAnimalCare extends Module implements Listener {

	public static WorldGuardPlugin WORLDGUARD = null;
	
	/**
	 * Called when the module is disabled.
	 */
	public void onDisable() {
		WORLDGUARD = null;
	}

	/**
	 * Called when the module is loaded.
	 */
	public void onEnable() {
		WORLDGUARD = (WorldGuardPlugin)m_plugin.getServer().getPluginManager().getPlugin("WorldGuard");
	}
	
	/**
	 * Called when a player attacks something
	 */	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		
		// If the damager isnt a player, return
		if (!(event.getDamager() instanceof Player))
			return;
		
		// If the entity isnt a mob, return
		if (event.getEntity() instanceof Player)
			return;
		
		final Player attacker = (Player)event.getDamager();
		final Entity mob = event.getEntity();
		
		if (mob.getType() != EntityType.CHICKEN)
			return;
		
		if (mob.getType() != EntityType.COW)
			return;
		
		if (mob.getType() != EntityType.MUSHROOM_COW)
			return;
		
		if (mob.getType() != EntityType.OCELOT)
			return;
		
		if (mob.getType() != EntityType.PIG)
			return;
		
		if (mob.getType() != EntityType.SHEEP)
			return;
		
		if (mob.getType() != EntityType.VILLAGER)
			return;
		
		if (mob.getType() != EntityType.WOLF)
			return;
		
		// get the region from the mobs location
		final ProtectedRegion region = getChildRegionFromLocation(attacker, mob.getLocation());
		if (region == null)
			return;
		
		// if they are a owner, then continue with the event
		Iterator<String> owners = region.getOwners().getPlayers().iterator();
        while (owners.hasNext())
        {
        	if (owners.next().equalsIgnoreCase(attacker.getName()))
        		return;
        }
        
        // if they are a member, then continue with the event
        Iterator<String> members = region.getMembers().getPlayers().iterator();
        while (members.hasNext())
        {
        	if (members.next().equalsIgnoreCase(attacker.getName()))
        		return;
        }
		
        attacker.sendMessage("&b[bAnimalCare] &fYou can not kill animals in this safezone.");
        attacker.sendMessage("&b[bAnimalCare] &fYou must own or be a member of the safezone first.");
        event.setCancelled(true);        
	}
	
	/**
	 * Get the lowest child region from a give location
	 */	
	public static ProtectedRegion getChildRegionFromLocation(Player player, Location location) {

        ArrayList<ProtectedRegion> possibleRegions = new ArrayList<ProtectedRegion>();
        
        final World world = player.getWorld();

        // loop through every region
        for(String regionName : WORLDGUARD.getRegionManager(world).getRegions().keySet()){

            // get the region from its name
            ProtectedRegion currentRegion = WORLDGUARD.getRegionManager(player.getWorld()).getRegion(regionName);

            // create a world edit vector for the signs position
            com.sk89q.worldedit.Vector v = new com.sk89q.worldedit.Vector(
                    location.getX(),
                    location.getY(),
                    location.getZ()
            );

            // if the current region contains the sign, add it to the list of possible regions
            if (currentRegion.contains(v)) {
                possibleRegions.add(currentRegion);
            }
        }

        // if we didnt get any regions, bail.
        if (possibleRegions.size() == 0)
            return null;

        // if we only got one region, it has to be the region we use
        if (possibleRegions.size() == 1)
            return possibleRegions.get(0);

        // work out the lowest child of the regions
        int childLevel = 0;
        ProtectedRegion lowestChild = null;
        for (int i = 0; i < possibleRegions.size(); ++i)
        {
            if (possibleRegions.get(i).getParent() != null)
            {
                ProtectedRegion tempRegion = possibleRegions.get(i);
                int tempChildLevel = 0;
                while(tempRegion.getParent() != null)
                {
                    tempRegion = tempRegion.getParent();
                    tempChildLevel++;
                }

                if (tempChildLevel > childLevel)
                {
                    childLevel = tempChildLevel;
                    lowestChild = possibleRegions.get(i);
                }
            }
        }

        // if we found the lowest child return that
        if (lowestChild != null)
            return lowestChild;

        // if we didn't find the lowest child, but found some regions, return the first region
        if (possibleRegions.size() > 0)
            return possibleRegions.get(0);

        // something went very wrong
        return null;
    }

}
