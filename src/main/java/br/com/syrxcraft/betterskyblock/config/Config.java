package br.com.syrxcraft.betterskyblock.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.border.IslandBorder;
import br.com.syrxcraft.betterskyblock.data.providers.Providers;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	private FileConfiguration config;

	//Database
	private String dbHostname;
	private String dbUsername;
	private String dbPassword;
	private String dbDatabase;

	//World
	private String worldName;
	private int radius;
	private int yLevel;

	private int nextRegion;

	private Biome defaultBiome;
	private List<Biome> allowedBiomes;

	//Schematic
	private String schematic;

	//Options
	private boolean deleteRegion;
	private int tpCountdown;
	private Providers dataProvider;
	private boolean useBossShopForMenu = false;

	private IslandBorder.BorderMode borderMode = IslandBorder.BorderMode.GREEN;


	private final BetterSkyBlock instance;

	public Config(BetterSkyBlock instance) {

		this.instance = instance;

		instance.saveDefaultConfig();

		loadConfig();

		// Data
		FileConfiguration data;

		try {
			data = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "data.yml"));
		}catch (Exception ignored) { data = null; }

		if (data==null) {

			instance.getLoggerHelper().error("There was an error while loading data.yml!");

		} else {

			int nextRegionX = data.getInt("NextRegion.X", -1);

			if (nextRegionX != -1) {
				int nextRegionZ = data.getInt("NextRegion.Z", -1);

				nextRegion = nextRegionCalc(nextRegionX, nextRegionZ) + 1;
				saveData();
			} else {
				this.nextRegion=data.getInt("NextRegion", 0);
			}
		}
	}

	private void loadConfig(){

		instance.reloadConfig();

		config = instance.getConfig();

		loadDatabase();
		loadWorld();
		loadOptions();
		loadSchematic();

	}

	private void loadDatabase(){

		dbHostname = config.getString("MySQL.Hostname");
		dbUsername = config.getString("MySQL.Username");
		dbPassword = config.getString("MySQL.Password");
		dbDatabase = config.getString("MySQL.Database");

	}

	private void loadWorld(){

		worldName = config.getString("WorldName");

		radius = Utils.validate(10,255,config.getInt("Radius",-1));
		yLevel = Utils.validate(1,255,64, config.getInt("YLevel", -1));

		String biomeName = config.getString("DefaultBiome", "UNCHANGED");

		if (!biomeName.equals("UNCHANGED")) {

			defaultBiome = Utils.getBiome(biomeName);

			if (defaultBiome == null) {
				instance.getLoggerHelper().warn("Unknown default biome \"" + biomeName + "\"");
			}

		}

		allowedBiomes = new ArrayList<>();

		for (String biomeString : config.getStringList("AllowedBiomes")) {

			Biome biome = Utils.getBiome(biomeString);

			if (biome == null) {
				instance.getLoggerHelper().warn("Skipping unknown allowed biome \"" + biomeString + "\"");
				continue;
			}

			allowedBiomes.add(biome);
		}


	}

	private void loadOptions(){

		deleteRegion = config.getBoolean("DeleteRegion", true);
		tpCountdown = config.getInt("TeleportCountdown", 5);
		dataProvider = Providers.getFromName(config.getString("DataProvider", "null"));
		useBossShopForMenu = config.getBoolean("useBossShopForMenu", false);
		borderMode = IslandBorder.BorderMode.fromString(config.getString("BorderMode", "BLUE"));

	}

	private void loadSchematic(){

		schematic = config.getString("Schematic");

		saveResource("default.schematic");

		if(schematic.endsWith(".schematic")) schematic = schematic.substring(0, schematic.length() - 10);

		File schematicFile = new File(instance.getDataFolder(), schematic + ".schematic");

		if (!schematicFile.exists()) {
			instance.getLoggerHelper().error("Island schematic file \"" + schematic + ".schematic\" doesn't exist!");
		}

	}
	
	public void saveData() {

		File file = new File(instance.getDataFolder(), "data.yml");

		FileConfiguration data;

		try {
			data = YamlConfiguration.loadConfiguration(file);
		}catch (Exception ignored) { data = null; }

		if (data==null) {

			instance.getLoggerHelper().error("There was an error while saving data.yml!");

		} else {

			data.set("NextRegion", nextRegion);

			try {
				data.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	void reloadConfig(){
		loadConfig();
	}
	
	void saveResource(String name) {
		if (!new File(instance.getDataFolder(), name).exists()) {
			instance.saveResource(name, false);
		}
	}
	
	public static int[] nextRegionCalc(int nextRegion) {
		return new int[] { 1 + ((nextRegion * 3) % 1350), 1 + (((nextRegion * 3) / 1350) * 3) };
	}

	public static int nextRegionCalc(int x, int z) {
		return ((x - 1) / 3) + (((z - 1) * 1350) / 9);
	}
	
	public int[] nextRegion() {
		return nextRegionCalc(this.nextRegion);
	}

	public void addNextRegion(){
		nextRegion++;
	}


	public String getDbDatabase() {
		return dbDatabase;
	}

	public String getDbHostname() {
		return dbHostname;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public String getDbUsername() {
		return dbUsername;
	}



	public String getWorldName() {
		return worldName;
	}

	public int getRadius() {
		return radius;
	}

	public int getYLevel() {
		return yLevel;
	}

	public Biome getDefaultBiome() {
		return defaultBiome;
	}



	public List<Biome> getAllowedBiomes() {
		return allowedBiomes;
	}


	public String getSchematic() {
		return schematic;
	}


	public int getTpCountdown() {
		return tpCountdown;
	}

	public boolean deleteRegion() {
		return deleteRegion;
	}

	public int getNextRegion() {
		return nextRegion;
	}

	public Providers getDataProvider() {
		return dataProvider;
	}

	public boolean useBossShopForMenu() {
		return useBossShopForMenu;
	}

	public IslandBorder.BorderMode getBorderMode() {
		return borderMode;
	}
}
