package com.draconequus.VillagerInteraction;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Command_vi implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String VI =  ChatColor.YELLOW + "[VI]" + ChatColor.RESET + " ";
		
		if (!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(VI + "Sorry, this command is only available to players!");
			return true;
		}

		Player player = (Player) sender;
		String playerName = player.getDisplayName();
		
		
		if (args.length == 0)  {
			if (ReputationFile.get().contains(playerName) && ReputationFile.get().get(playerName + ".active").equals(false)) {
				player.sendMessage(VI + "VillagerInteraction is currently disabled. Would you like to enable it?");
				TextComponent disable = new TextComponent(ChatColor.RED + "[Disable]");
				disable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi disable"));
				TextComponent enable = new TextComponent(ChatColor.GREEN + "[Enable]");
				enable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi enable"));
				TextComponent space = new TextComponent(" ");
				player.spigot().sendMessage(enable, space, disable);
			}
			else if (ReputationFile.get().get(playerName + ".active").equals(true)) {
				player.sendMessage(ChatColor.YELLOW + "[VI Menu]" + ChatColor.RESET);
				
				TextComponent plugin = new TextComponent(ChatColor.YELLOW + "Plugin: " + ChatColor.RESET);
				TextComponent pluginInfo = new TextComponent("[Info] ");
				pluginInfo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi plugininfo"));
				TextComponent pluginTips = new TextComponent("[Tips] ");
				pluginTips.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi plugintips"));
				TextComponent pluginDisable = new TextComponent(ChatColor.RED + "[Disable] ");
				pluginDisable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi disable"));
				TextComponent pluginEnable = new TextComponent(ChatColor.GREEN + "[Enable]");
				pluginEnable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi enable"));
				player.spigot().sendMessage(plugin, pluginInfo, pluginTips, pluginDisable, pluginEnable);
				
				TextComponent quest = new TextComponent(ChatColor.YELLOW + "Quest: " + ChatColor.RESET);
				TextComponent questInfo = new TextComponent("[Info] ");
				questInfo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi questinfo"));
				TextComponent questWindowoff = new TextComponent(ChatColor.YELLOW + "[Window-Toggle] ");
				questWindowoff.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi questwindowtoggle"));
				TextComponent questAbandon = new TextComponent(ChatColor.RED + "[Abandon] ");
				questAbandon.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi questabandon"));
				player.spigot().sendMessage(quest, questInfo, questWindowoff, questAbandon);
			}
		}

		else if (args[0].equals("enable")) {
			if (ReputationFile.get().getBoolean(playerName + ".active") == true) {
				player.sendMessage(VI + "VillagerInteraction is already enabled.");
			}
			else {
				ReputationFile.get().set(playerName + ".active", true);
				ReputationFile.save();
				player.sendMessage(VI + "VillagerInteraction has now been enabled.");
			}
		}
		
		else if (args[0].equals("disable")) {
			if (ReputationFile.get().getBoolean(playerName + ".active") == false) {
				player.sendMessage(VI + "VillagerInteraction is already disabled.");
			}
			else {
				ReputationFile.get().set(playerName + ".active", false);
				ReputationFile.save();
				player.sendMessage(VI + "VillagerInteraction has now been disabled.");
			}
		}
		
		else if (args[0].equals("plugininfo")) {
			player.sendMessage(VI + "The goal of VillagerInteraction as a Spigot plugin is to make the vanilla villages a bit livelier, and  to give Minecraft a more RPG like feel.\n" +
			"As of now you will find NPCs that send you on quests, give you rewards, and reputation, which provides discounts, and soon also access to other kinds of NPC actions such as gossip, shops, pets, companions, boss hunts.\n" +
			"If you have any suggestions or comments, the best way to reach out is to visit http://www.draconequus.com and write something on the contact page.\n" +
		    "Thank you for your curiosity, and I hope you'll enjoy the experience!" + ChatColor.BOLD + " - Moerkoet");
		}
		
		else if (args[0].equals("plugintips")) {
			player.sendMessage(ChatColor.YELLOW + "[VI Tips] \n1." + ChatColor.RESET + 
			"To prevent the trade window from getting opened when talking to a villager, hold a stick in either hand!");
		}
		
		else if (args[0].equals("queststart")) {
			String entityName = InteractionInProgressFile.get().getString(playerName + ".entityname");
			String entityProfession = InteractionInProgressFile.get().getString(playerName + ".entityprofession");
			String entityLocation = InteractionInProgressFile.get().getString(playerName + ".entitylocation");
			
			InteractionInProgressFile.get().set(playerName, null);
			InteractionInProgressFile.save();
			
			if (!QuestsInProgressFile.get().contains(playerName)) {
				Quests.questStart(player, entityName, entityProfession, entityLocation);
			}
			else if (QuestsInProgressFile.get().contains(playerName)) {
				if (entityName.equals(QuestsInProgressFile.get().getString(playerName + ".questgiver"))) {
					if (QuestsInProgressFile.get().getString(playerName + ".state").equals("active")) {
						Quests.questInProgressQuestGiver(player, entityName, entityProfession);
					}
				}
				else {
					Quests.questInProgressVillager(player, entityName, entityProfession);
				}
			}
		}
		
		else if (args[0].equals("questinfo")) {
			if (!QuestsInProgressFile.get().contains(playerName)) {
				player.sendMessage(VI + "You currently do not have an active quest.");
			}
			else {
				String id = QuestsInProgressFile.get().getString(playerName + ".id");
				String name = QuestsFile.get().getString(id + ".name");
				String objectiveAmount = QuestsInProgressFile.get().getString(playerName + ".objectivenumber");
				String objective = QuestsFile.get().getString(id + ".objective").replace("%amount%", objectiveAmount).replaceAll("&", "");
				String description = QuestsFile.get().getString(id + ".description");
				String npc = QuestsInProgressFile.get().getString(playerName + ".questgiver");
				String location = QuestsInProgressFile.get().getString(playerName + ".lastlocation");
				
				player.sendMessage(ChatColor.YELLOW + "[VI Current Quest Info]\n" + ChatColor.RESET +
				ChatColor.YELLOW + "Name:           " + ChatColor.RESET + name  + "\n" +
				ChatColor.YELLOW + "QuestGiver:   " + ChatColor.RESET + npc + "\n" +
				ChatColor.YELLOW + "LastLocation: " + ChatColor.RESET + location + "\n" +
				ChatColor.YELLOW + "Objective:     " + ChatColor.RESET + objective + "\n" +
				ChatColor.YELLOW + "Description:   " + ChatColor.RESET + description);
			}
		}
		
		else if (args[0].equals("questwindowtoggle")) {
			if (ReputationFile.get().getBoolean(playerName + ".questwindow") == true) {
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				ReputationFile.get().set(playerName + ".questwindow", false);	
			}
			else {
				Quests.setScoreBoard(player);
				ReputationFile.get().set(playerName + ".questwindow", true);
			}
			ReputationFile.save();
		}
		
		else if (args[0].equals("questabandon")) {
			if (QuestsInProgressFile.get().contains(playerName)) {
				QuestsInProgressFile.get().set(playerName, null);
				QuestsInProgressFile.save();
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				player.sendMessage(VI + "The quest has been abandoned.");
			}
			else {
				player.sendMessage(VI + "You currently do not have an active quest.");
			}

		}
		
		else if (args[0].equals("questaccept")) {
			String id = QuestsInProgressFile.get().getString(playerName + ".id");
			String name = QuestsFile.get().getString(id + ".name");
			
			if (name == null) {
				player.sendMessage(VI + "This quest has already been denied. Talk to a villager to start a new quest.");
			}
			else if (QuestsInProgressFile.get().getString(playerName + ".state").equals("active")) {
				player.sendMessage(VI + "This quest has already been accepted.");
			}
			else {
				QuestsInProgressFile.get().set(playerName + ".state", "active");
				QuestsInProgressFile.save();
			    player.sendMessage(VI + "Quest \"" + name + "\" has been accepted.");
				Quests.setScoreBoard(player);
			}
		}
		
		else if (args[0].equals("questdeny")) {
			if (QuestsInProgressFile.get().contains(playerName)) {
				QuestsInProgressFile.get().set(playerName, null);
				QuestsInProgressFile.save();
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				player.sendMessage(VI + "The quest has been denied.");
			}
			else {
				player.sendMessage(VI + "You currently do not have an active quest.");
			}
		}
		
		else if(args[0].equals("questcomplete")) {
			if (QuestsInProgressFile.get().getString(playerName) == null) {
				player.sendMessage(VI + "That quest has already been completed.");
				return true;
			}
			
			boolean isComplete = false;
			if (QuestsInProgressFile.get().getString(playerName + ".type").equals("fetch")) {
			  isComplete = Quests.questItems(player);
			}
			
			String entityName = QuestsInProgressFile.get().getString(playerName + ".questgiver");
			String entityProfession = QuestsInProgressFile.get().getString(playerName + ".profession");
			entityProfession = entityProfession.charAt(0) + entityProfession.substring(1).toLowerCase();
			
			int experience = Quests.calculateExperience(player);
			int reputationExperience = Quests.calculateReputationExperience(player);
					
			String itemReward = "";
			if (QuestsInProgressFile.get().getString(playerName + ".reward") != null) {
				Material materialReward = Material.matchMaterial(QuestsInProgressFile.get().getString(playerName + ".reward"));
				itemReward = materialReward.toString();
				ItemStack itemstackReward = new ItemStack(materialReward, 1);
				player.getInventory().addItem(itemstackReward);
			}
					
			if (isComplete) {
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				QuestsInProgressFile.get().set(playerName, null);
				player.sendMessage(ChatColor.YELLOW + "[" + entityName + " - " + entityProfession + " (QuestGiver)]\n" + ChatColor.RESET +
				"Thank you for the help, " + playerName + "! This will do for now.\n" + ChatColor.YELLOW +
				"Rewards: " + ChatColor.RESET + experience + " XP, " + reputationExperience + " RepEXP, " + itemReward);
			}
			else {
				player.sendMessage(ChatColor.YELLOW + "[" + entityName + " - " + entityProfession + " (QuestGiver)]\n" + ChatColor.RESET +
				"Sorry, but it seems you do not yet have all the items I asked for.");
			}
					
			ReputationFile.save();
			QuestsInProgressFile.save();
			return true;
		}
		return false;
	}
}
