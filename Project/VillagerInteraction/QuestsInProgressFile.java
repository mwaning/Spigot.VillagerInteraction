package com.draconequus.VillagerInteraction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class QuestsInProgressFile {
	private VillagerInteraction plugin;
	private static File file = null;
	private static FileConfiguration config = null;
	
	public QuestsInProgressFile(VillagerInteraction plugin) {
		this.plugin = plugin;
		saveDefault();
		//set(0, null, null, null, null, null);
	}
	
	public static void reload() {
		if (file == null) {
			file = new File(Bukkit.getServer().getPluginManager().getPlugin("VillagerInteraction").getDataFolder(), "questsinprogress.yml");
		}
		
		config = YamlConfiguration.loadConfiguration(file);
		
		InputStream defaultStream = (Bukkit.getServer().getPluginManager().getPlugin("VillagerInteraction").getResource("questsinprogress.yml"));
		if (defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			config.setDefaults(defaultConfig);
		}
	}
	
	public static FileConfiguration get() {
		if (config == null) {
			reload();
		}
		return config;
	}

	public static void save() {
		if (config == null || file == null) {
			return;
		}
			
		try {
			get().save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveDefault() {
		if (file == null) {
			file = new File(this.plugin.getDataFolder(), "questsinprogress.yml");
		}
		
		if (!file.exists()) {
			this.plugin.saveResource("questsinprogress.yml", false);
		}
	}
	
	public static ArrayList<String> load(String keyName) {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(get().getStringList(keyName));
		return list;
	}
	
	public static void set(int questId, String playerName, String questState, String questGiver, String profession, String lastLocation) {
		String reputationLevel = ReputationFile.get().getString(playerName + ".level");

		get().set(playerName + ".id", questId);
		get().set(playerName + ".state", questState);
		get().set(playerName + ".level", reputationLevel);
		get().set(playerName + ".questgiver", questGiver);
		get().set(playerName + ".profession", profession);
		get().set(playerName + ".lastlocation", lastLocation);
		get().set(playerName + ".objectivenumber", Quests.randomize(10) + 1);
		get().set(playerName + ".reputationexperience", Quests.randomize(5) + 1);

		// for quest specific rewards
		if (QuestsFile.get().getString(questId + ".reward") != null) {
			get().set(playerName + ".reward", QuestsFile.get().getString(questId + ".reward"));
		}
				
		save();
		
		if (get().getString(playerName + ".level").equals("1")) {
			get().set(playerName + ".experience", Quests.randomize(5) + 1);
		}
		else if (get().getString(playerName + ".level").equals("2")) {
			get().set(playerName + ".experience", Quests.randomize(5) + 6);
			//get().set(playerName + ".reward", Quests.rewardsTable("2", Quests.randomize(RandomDataFile.load("rewardstable").size())));
		}
		else if (get().getString(playerName + ".level").equals("3")) {
			get().set(playerName + ".experience", Quests.randomize(5) + 6);
		}
		else if (get().getString(playerName + ".level").equals("4")) {
			get().set(playerName + ".experience", Quests.randomize(10) + 6);	
		}
		else if (get().getString(playerName + ".level").equals("5")) {
			get().set(playerName + ".experience", Quests.randomize(10) + 11);			
		}
		
		save();
	}
}