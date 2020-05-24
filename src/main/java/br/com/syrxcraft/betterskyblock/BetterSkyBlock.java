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

	}

	@Override
	public void onEnable() {

		instance = this;

		loggerHelper = new LoggerHelper(this);
		config = new Config(this);
		integrationManager = new IntegrationManager(this);

		islandWorld = Bukkit.getWorld(config.getWorldName());

		try {

			dataStore = new DataStore(this);
			eventManager = new EventManager(this);

			CommandRegisterer.registerCommands(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
}
