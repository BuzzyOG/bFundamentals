package uk.codingbadgers.bHelpful;

import java.util.ArrayList;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 *
 * @author James
 */
public class Help {
    
	/**
	 * Display help.
	 *
	 * @param player the player
	 */
	public static void displayHelp(Player player) {
		
		Permission permission = bHelpful.MODULE.getPermissions();
		
		if (permission == null) {
			player.sendMessage("Help is based upon player rank.");
			player.sendMessage("This server has no ranks.");
			return;
		}
				
		ArrayList<String> help = Configuration.getHelp(permission.getPrimaryGroup(player));
		
		if (help == null) {
			player.sendMessage("No help exists for your rank.");
			return;
		}
		
		
		String rank = permission.getPrimaryGroup(player);
		rank = rank.substring(0, 1).toUpperCase() + rank.substring(1);
		
		if (bHelpful.spoutEnabled) {
			SpoutPlayer sPlayer = (SpoutPlayer)player;
			if (sPlayer.isSpoutCraftEnabled()) {
				sPlayer.sendNotification(ChatColor.RED + Configuration.m_servername + " Help", "You are rank [" + rank + "]", Material.DIAMOND_PICKAXE);
			} else {
				player.sendMessage(ChatColor.RED + "You are rank " + ChatColor.GRAY + "[" + rank + "]");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You are rank " + ChatColor.GRAY + "[" + rank + "]");
		}
		
		for (int i = 0; i < help.size(); ++i ) {
			player.sendMessage(help.get(i));
		}		
	}
      
}