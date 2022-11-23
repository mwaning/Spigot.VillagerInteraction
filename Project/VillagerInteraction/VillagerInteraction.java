package com.draconequus.VillagerInteraction;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;


public class VillagerInteraction extends JavaPlugin {
  Plugin plugin = this;
  public ReputationFile reputationFile;
  public RandomDataFile randomDataFile;
  public QuestsFile questsFile;
  public QuestsInProgressFile questsInProgressFile;
  
  @Override
  public void onEnable() {
	  new Events(this);

	  this.getCommand("vi").setExecutor(new Command_vi());
	  this.reputationFile = new ReputationFile(this);
	  this.randomDataFile = new RandomDataFile(this);
	  this.questsFile = new QuestsFile(this);
	  this.questsInProgressFile = new QuestsInProgressFile(this);
  }
  
  @Override
  public void onDisable() {
  }
}
