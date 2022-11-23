package com.draconequus.VillagerInteraction;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;


public class Quests {
	
	public static void setScoreBoard(Player player) {
		String playerName = player.getDisplayName();
		
		if (ReputationFile.get().getBoolean(playerName + ".active") == false) {
			return;
		}
		else if (!QuestsInProgressFile.get().contains(playerName)) {
			player.sendMessage(ChatColor.YELLOW + "[VI]" + ChatColor.RESET + " " + "You currently do not have an active quest.");
			return;
		}
		
		String questId = QuestsInProgressFile.get().getString(playerName + ".id");
		String questName = QuestsFile.get().getString(questId + ".name");
		String questGiver = QuestsInProgressFile.get().getString(playerName + ".questgiver");
		String questObjectiveAmount = QuestsInProgressFile.get().getString(playerName + ".objectivenumber");
		String questObjective = QuestsFile.get().getString(questId + ".objective").replace("%amount%", questObjectiveAmount).replaceAll("&", "");
		String lastLocation = QuestsInProgressFile.get().getString(playerName + ".lastlocation");
		String reputationLookUp = ReputationFile.get().getString(playerName + ".level");
		String reputationLevel = RandomDataFile.get().getString("reputation." + reputationLookUp);
		String reputationExperience = ReputationFile.get().getString(playerName + ".experience");
				
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = board.registerNewObjective("a", "b", ChatColor.YELLOW + "" + ChatColor.BOLD + "Quest");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score name = obj.getScore(ChatColor.YELLOW + "Name");
		Score nameVar = obj.getScore(questName);
		Score objectives = obj.getScore(ChatColor.YELLOW + "Objective");
		Score objectivesVar = obj.getScore(questObjective);
		Score npc = obj.getScore(ChatColor.YELLOW + "QuestGiver");
		Score npcVar = obj.getScore(questGiver);
		Score location = obj.getScore(ChatColor.YELLOW + "LastLocation");
		Score locationVar = obj.getScore(lastLocation);
		Score space = obj.getScore("");
		Score reputation1 = obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Reputation");
		Score reputation2 = obj.getScore(reputationLevel);
		Score reputation3 = obj.getScore(reputationExperience + "/100");
		name.setScore(11);
		nameVar.setScore(10);
		objectives.setScore(9);
		objectivesVar.setScore(8);
		npc.setScore(7);
		npcVar.setScore(6);
        location.setScore(5);
        locationVar.setScore(4);
		space.setScore(3);
		reputation1.setScore(2);
		reputation2.setScore(1);
		reputation3.setScore(0);
		player.setScoreboard(board);
	}

	public static int randomize(int nInt) {
		Random random = new Random();
		return random.nextInt(nInt);
	}
	
	public static String rewardsTable(String reputationLevel, int number) {
		return RandomDataFile.load("rewardnames." + reputationLevel).get(number);
	}
	
	public static String playerNameUse(String playerNameUse) {
		if (ReputationFile.get().getString(playerNameUse + ".level").equals("1")) {
			playerNameUse = "adventurer";
		}
		return playerNameUse;
	}
	
	public static void questStart(Player player, String entityName, String entityProfession, String entityLocation) {
		String playerName = player.getDisplayName();
    	
    	int questNumber = 0;
    	for (int i=1; i<QuestsFile.get().getKeys(false).size() + 1; i++) {
    		questNumber = i;
    		if (QuestsFile.get().getString(questNumber + ".class").equals(entityProfession)) {
    			break;
    		}
    	}
    	String questDescription = QuestsFile.get().getString(questNumber + ".description");
    			
		TextComponent questAccept = new TextComponent(ChatColor.GREEN + "[Accept] ");
		questAccept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi questaccept"));
		TextComponent questDeny = new TextComponent(ChatColor.RED + "[Deny]");
		questDeny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi questdeny"));

		QuestsInProgressFile.set(questNumber, playerName, "pending", entityName, entityProfession, entityLocation);
		
    	String questObjectiveNumber = QuestsInProgressFile.get().getString(playerName + ".objectivenumber");
		String questObjective = QuestsFile.get().getString(questNumber + ".objective").replace("%amount%", questObjectiveNumber).replaceAll("&", "");
		
		player.sendMessage(ChatColor.YELLOW + "[" + entityName + " - " + entityProfession + " (QuestGiver)]\n" + ChatColor.RESET + "\n" + questDescription + "\n" + ChatColor.YELLOW + "Objective: " + ChatColor.RESET + questObjective);
		player.spigot().sendMessage(questAccept, questDeny);
	}
	
	public static void questInProgressQuestGiver(Player player, String entityName, String profession) {
		String playerNameUse = playerNameUse(player.getDisplayName());
		player.sendMessage(ChatColor.YELLOW + "[" + entityName + " - " + profession + " (QuestGiver)]\n" + ChatColor.RESET + 
		"Hi, " + playerNameUse + ". How is that quest getting along?");
		
		TextComponent turnIn = new TextComponent(ChatColor.GREEN + "[Complete Quest]");
		turnIn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vi questcomplete"));
		player.spigot().sendMessage(turnIn);
	}
	
	public static void questInProgressVillager(Player player, String entityName, String profession) {
		String playerNameUse = playerNameUse(player.getDisplayName());
		player.sendMessage(ChatColor.YELLOW + "[" + entityName + " - " + profession + "]\n" + ChatColor.RESET +
		"Hi, " + playerNameUse + ". I'd ask you for help but it seems you are already busy.");
	}
	
	public static boolean questItems(Player player) {
		String playerName = player.getDisplayName();
		String objectiveAmountString = QuestsInProgressFile.get().getString(playerName + ".objectivenumber");
		int objectiveAmount = Integer.parseInt(objectiveAmountString);
		String questId = QuestsInProgressFile.get().getString(playerName + ".id");
		String objectiveString = QuestsFile.get().getString(questId + ".objective");
		String questMaterialArray[] = objectiveString.split(" ");
		String questMaterial = questMaterialArray[questMaterialArray.length - 1].replaceAll("&", "");
		Material objectiveMaterial = Material.matchMaterial(questMaterial);
		ItemStack items = new ItemStack(objectiveMaterial, objectiveAmount);
		
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) { continue; }
			if (item.isSimilar(items)) {
				player.getInventory().removeItem(items);
				return true;
			}
		
		}
		return false;
	}

	public static int calculateExperience(Player player) {
		String playerName = player.getDisplayName();
		float playerExperienceF = player.getExp();
		int playerExperience = (int) (playerExperienceF / 10 + 1);
		int experience = QuestsInProgressFile.get().getInt(playerName + ".experience");
		int giveExperience = experience * playerExperience;
		if (experience < 1) { giveExperience = 1; } 
		player.giveExp(giveExperience);
		QuestsInProgressFile.save();
		return giveExperience;
	}

	public static int calculateReputationExperience(Player player) {
		String playerName = player.getDisplayName();
		int playerReputationExperience = ReputationFile.get().getInt(playerName + ".experience");
		int playerReputationLevel = ReputationFile.get().getInt(playerName + ".level");
		int reputationExperience = QuestsInProgressFile.get().getInt(playerName + ".reputationexperience");
		int giveReputationExperience = playerReputationExperience + reputationExperience;
		
		if (giveReputationExperience >= 100) {
			if (!(playerReputationLevel == 5)) {
			  int nextReputationLevel = ReputationFile.get().getInt(playerName + ".level") + 1;
			  ReputationFile.get().set(playerName + ".level", nextReputationLevel);
			  giveReputationExperience = giveReputationExperience % 100;
			  ReputationFile.get().set(playerName + ".experience", giveReputationExperience);
			}
		}
		else {
			ReputationFile.get().set(playerName + ".experience", giveReputationExperience);
		}
		ReputationFile.save();
		QuestsInProgressFile.save();
		return reputationExperience;
	}
	
	
}
