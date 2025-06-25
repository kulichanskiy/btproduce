package me.buildtoproduce;

import org.bukkit.plugin.java.JavaPlugin;

public class BuildToProduce extends JavaPlugin {
	
	private static BuildToProduce instance;
	
    @Override
    public void onEnable() {
        CommandHandler handler = new CommandHandler();
        getCommand("btproduce").setExecutor(handler);
        getCommand("btproduce").setTabCompleter(handler);
        getLogger().info("BuildToProduce включен.");
    }
    public static BuildToProduce getInstance() {
        return instance;
    }
}
