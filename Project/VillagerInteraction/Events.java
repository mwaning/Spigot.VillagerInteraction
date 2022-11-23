package com.draconequus.VillagerInteraction;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;


public class Events implements Listener {
	private VillagerInteraction plugin;
	String VI =  ChatColor.YELLOW + "[VI]" + ChatColor.RESET + " ";
	
	public Events(VillagerInteraction plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getDisplayName();
		
		if (!ReputationFile.get().contains(playerName)) {
			ReputationFile.get().set(playerName + ".active", true);
			ReputationFile.get().set(playerName + ".questwindow", true);
			ReputationFile.get().set(playerName + ".level", "1");
			ReputationFile.get().set(playerName + ".experience", "0");
			ReputationFile.save();
			
			player.sendMessage(VI + "Welcome to VillagerInteraction, " + playerName + "!\n" +
			"A plugin that adds more life to villager interactions. Access the plugin at any time with " + ChatColor.YELLOW + "/vi" + ChatColor.RESET + "\n" +
			"Select one of the options below:");
			/*
			"VI for short, is a plugin that adds more life to villagers, including quests, gossip, rewards, and other unlockable NPCs. " +
			"You are free to Disable and Enable the plugin at any given time. Also, whenever you need help, " +
		    "just remember to type " + ChatColor.YELLOW + "/vi" + ChatColor.RESET + " and an interactive menu will appear. Enjoy!");
			*/
			TextComponent moreInfo = new TextComponent(ChatColor.WHITE + "[More Info] ");
			moreInfo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi plugininfo"));
			TextComponent disable = new TextComponent(ChatColor.RED + "[Disable] ");
			disable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi disable"));
			TextComponent enable = new TextComponent(ChatColor.GREEN + "[Enable]");
			enable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi enable"));
			player.spigot().sendMessage(moreInfo, disable, enable);
		}
		else if(ReputationFile.get().getBoolean(playerName + ".questwindow") == true) {
			Quests.setScoreBoard(player);
		}

	}
	
	@EventHandler
	public void onStartDialogue(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getDisplayName();
		String playerNameUse = playerName;
		String reputationLevel = ReputationFile.get().getString(playerName + ".level");
		
		if (ReputationFile.get().getString(playerName + ".level").equals("0")) {
			playerNameUse = "...fellow";
		}
		else if (ReputationFile.get().getString(playerName + ".level").equals("1")) {
			playerNameUse = "adventurerer";
		}

		if (event.getRightClicked() instanceof Villager) {
			if (ReputationFile.get().getBoolean(playerName + ".active") == false) {
				return;
			}

			if (player.getInventory().getItemInMainHand().getType().equals(Material.STICK) || player.getInventory().getItemInOffHand().getType().equals(Material.STICK)) {
				event.setCancelled(true);
			}
			
			Entity entity = event.getRightClicked();
			String entityName = "";
			
			NumberFormat format = new DecimalFormat("0");
			String entityLocation = "X: " + format.format(entity.getLocation().getX()) + " Y: " + format.format(entity.getLocation().getY()) + " Z: " + format.format(entity.getLocation().getZ());

			
			Villager villager = (Villager) entity;
			Profession professionObject = villager.getProfession();
			String profession = professionObject.toString();
			profession = profession.charAt(0) + profession.substring(1).toLowerCase();
					
			if (profession.equals("None") || profession.equals("Nitwit")) {
				profession = "Citizen";
			}
			
			if (entity.getCustomName() == null) {
				Random randName = new Random();
				int randNameNumber = randName.nextInt(RandomDataFile.load("names").size());
				entityName = RandomDataFile.load("names").get(randNameNumber);
				entity.setCustomName(entityName);
			}
			else {
				entityName = entity.getCustomName();
			}
			
			InteractionInProgressFile.get().set(playerName + ".entityname", entityName);
			InteractionInProgressFile.get().set(playerName + ".entityprofession", profession);
			InteractionInProgressFile.get().set(playerName + ".entitylocation", entityLocation);
			InteractionInProgressFile.save();
			
	    	String entityGreeting = RandomDataFile.load("greetings").get(Quests.randomize(RandomDataFile.load("greetings").size()))
	    	          .replace("%playername%", playerNameUse)
	    	          .replace("%villagername%", entityName)
	    	    	  .replace("%villagerprofession%", profession);
			
	    	if (reputationLevel.equals("0")) {
				player.sendMessage(ChatColor.YELLOW + "[ Villager ]" + ChatColor.RESET + "\n" + 
				"Hello, " + playerNameUse);
	    	}
	    	else {
				player.sendMessage(ChatColor.YELLOW + "[ " + entityName + " - " + profession + " ]" + ChatColor.RESET + "\n" + entityGreeting + "\n" +
				"What are you looking for?");
	    	}
			
			TextComponent menuQuest = new TextComponent("[Quest] ");
			menuQuest.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi queststart"));
			TextComponent menuGossip = new TextComponent(ChatColor.GREEN + "[Gossip] ");
			menuGossip.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi gossipstart"));
			TextComponent menuBank = new TextComponent(ChatColor.GREEN + "[Bank] ");
			menuBank.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi shopstart"));
			TextComponent menuShop = new TextComponent(ChatColor.GREEN + "[Shop] ");
			menuShop.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi shopstart"));
			TextComponent menuPet = new TextComponent(ChatColor.GREEN + "[Pet] ");
			menuPet.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi petstart"));
			TextComponent menuCompanion = new TextComponent(ChatColor.GREEN + "[Companion] ");
			menuCompanion.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi companionstart"));
			TextComponent menuBosshunt = new TextComponent(ChatColor.GREEN + "[Boss Hunt] ");
			menuBosshunt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi bosshuntstart"));
			
			if (reputationLevel.equals("1")) {
				player.spigot().sendMessage(menuQuest);
			}
			else if (reputationLevel.equals("2")) {
				player.spigot().sendMessage(menuQuest, menuGossip);
			}
			else if (reputationLevel.equals("3")) {
				player.spigot().sendMessage(menuQuest, menuGossip, menuBank, menuShop);
			}
			else if (reputationLevel.equals("4")) {
				player.spigot().sendMessage(menuQuest, menuGossip, menuBank, menuShop, menuPet);
			}
			else if (reputationLevel.equals("5")) {
				player.spigot().sendMessage(menuQuest, menuGossip, menuBank, menuShop, menuPet, menuCompanion, menuBosshunt);
			} 
		}
		
	}
}
