/**
 * bHelpful 1.2-SNAPSHOT
 * Copyright (C) 2013  CodingBadgers <plugins@mcbadgercraft.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.codingbadgers.bHelpful.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.codingbadgers.bHelpful.Config;
import uk.codingbadgers.bHelpful.Output;
import uk.codingbadgers.bHelpful.bHelpful;

public class MotdCommand extends ConfigCommand {

	private static List<String> message = null;

	public MotdCommand() {
		super(Config.MOTD_LABEL);
		message = new ArrayList<String>();
		HelpfulCommandHandler.registerCommand(this);
	}

	@Override
	protected void handleCommand(CommandSender sender, String label, String[] args) {
		displayMotd(sender);
	}
	
	public static void displayMotd(CommandSender sender) {
		// minimap perms
		String minimapPerms = "&0&0";
		if (sender instanceof Player && bHelpful.hasPermission((Player)sender, "bhelpful.minimap.cavemapping"))
			minimapPerms += "&1";
		if (sender instanceof Player && bHelpful.hasPermission((Player)sender, "bhelpful.minimap.player"))
			minimapPerms += "&2";
		if (sender instanceof Player && bHelpful.hasPermission((Player)sender, "bhelpful.minimap.animal"))
			minimapPerms += "&3";
		if (sender instanceof Player && bHelpful.hasPermission((Player)sender, "bhelpful.minimap.mob"))
			minimapPerms += "&4";
		if (sender instanceof Player && bHelpful.hasPermission((Player)sender, "bhelpful.minimap.slime"))
			minimapPerms += "&5";
		if (sender instanceof Player && bHelpful.hasPermission((Player)sender, "bhelpful.minimap.squid"))
			minimapPerms += "&6";
		if (sender instanceof Player && bHelpful.hasPermission((Player)sender, "bhelpful.minimap.other"))
			minimapPerms += "&7";
		minimapPerms += "&e&f";
		
		sender.sendMessage(replaceColors(minimapPerms));
		
		for (int i = 0; i < message.size(); i++) {
			sender.sendMessage(message.get(i));
		}
	}

	@Override
	protected void loadConfig() throws IOException {
		if (!m_file.exists()) {
			Output.log(Level.SEVERE, "Config file Motd.cfg does not exist");
			return;
		}

		Output.log(Level.INFO, "Loading config file 'Motd.cfg'");

		message.clear();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(m_file));

			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();

				// ignore comments and empty lines
				if (line.startsWith("#") || line.length() == 0) {
					continue;
				}

				message.add(replaceColors(line));
			}
			
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
