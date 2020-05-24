package br.com.syrxcraft.betterskyblock;

import br.com.syrxcraft.betterskyblock.integration.IntegrationManager;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.commands.CommandRegisterer;
import br.com.syrxcraft.betterskyblock.config.Config;
import br.com.syrxcraft.betterskyblock.data.DataStore;
import br.com.syrxcraft.betterskyblock.listeners.EventManager;
import br.com.syrxcraft.betterskyblock.utils.LoggerHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class BetterSkyBlock extends JavaPlugin {

	private static BetterSkyBlock instance;

	private Config config;
	private DataStore dataStore;
	private LoggerHelper loggerHelper;
	private IntegrationManager integrationManager;


	private EventManager eventManager;
	private World islandWorld;

	@Override
	public void onLoad() {
		instance = this;

		loggerHelper = new LoggerHelper(this);
	}

	@Override
	public void onEnable() {

		config = new Config(this);
		islandWorld = Bukkit.getWorld(config.getWorldName());
		integrationManager = new IntegrationManager(this);

		try {

			dataStore = new DataStore(this);
			eventManager = new EventManager(this);

			CommandRegisterer.registerCommands(this);

		} catch (Exception e) {
			e.printStackTrace();
		}

		dropInformation(loggerHelper);
	}

	public LoggerHelper getLoggerHelper() {
		return loggerHelper;
	}

	public static BetterSkyBlock getInstance() {
		return instance;
	}

	public Config config() {
		return config;
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public Location getSpawn() {
		return islandWorld.getSpawnLocation();
	}

	public IntegrationManager getIntegrationManager() {
		return integrationManager;
	}

	public Config getPluginConfig() {
		return config;
	}

	public Island getIsland(UUID playerId){
		return dataStore.getIsland(playerId);
	}

	public World getIslandWorld() {
		return islandWorld;
	}

	public void dropInformation(LoggerHelper loggerHelper){
		loggerHelper.info("\n");
	}
}
