package br.com.syrxcraft.betterskyblock.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.*;
import com.griefdefender.api.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class DataStore {

	private final BetterSkyBlock instance;

	private final MySQLDriver mySQLDriver;

	private Connection databaseConnection;
	private final Map<UUID, Island> islands = new HashMap<UUID,Island>();

	public int getTotalOfIslands(){
		return this.islands.size();
	}

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public DataStore(BetterSkyBlock instance) throws Exception {

		this.instance = instance;

		mySQLDriver = new MySQLDriver(
				instance.config().getDbHostname(),
				instance.config().getDbUsername(),
				instance.config().getDbPassword(),
				instance.config().getDbDatabase());

		if(!mySQLDriver.isDriverAvailable()){
			throw new Exception("Unable to load Java's mySQL database driver.  Check to make sure you've installed it properly.");
		}
		
		try {
			databaseConnection = mySQLDriver.connect();
		} catch(Exception e) {
			throw new Exception("Unable to connect to database.  Check your config file settings. Details: \n" + e.getMessage());
		}
		
		Statement statement = databaseConnection.createStatement();

		try {

			// Creates tables on the database
			statement.executeUpdate("" +
					"CREATE TABLE IF NOT EXISTS betterskyblock_islands (" +
					"player binary(16) NOT NULL, " +
					"claimid binary(16) NOT NULL, " +
					"sx int(11) NOT NULL, " +
					"sy int(11) NOT NULL, " +
					"sz int(11) NOT NULL, " +
					"PRIMARY KEY (player));");

		} catch(Exception e) {
			throw new Exception("Unable to create the necessary database table. Details: \n" + e.getMessage());
		}
		
		ResultSet rs = this.statement().executeQuery("SELECT * FROM betterskyblock_islands;");
		islands.clear();

		ClaimManager claimManager = GriefDefender.getCore().getClaimManager(BetterSkyBlock.getInstance().getIslandWorld().getUID());

		while (rs.next()) {

			UUID uuid = Utils.toUUID(rs.getBytes(1));
			Claim claim = claimManager.getClaimByUUID(Utils.toUUID(rs.getBytes(2))).orElse(null);

			//BetterSkyBlock.getInstance().getGriefPrevention().dataStore.getClaim(rs.getInt(2));

			if (claim != null) {
				islands.put(uuid, new Island(uuid, claim, new Location(Utils.worldFromUUID(claim.getWorldUniqueId()), rs.getInt(3) + 0.5, rs.getInt(4), rs.getInt(5) + 0.5)));
			}
		}
	}
	
	public Island createIsland(UUID uuid) throws Exception {

		if (instance.config().getNextRegion() > 1822500) {
			throw new Exception("Max amount of islands reached.");
		}

		int[] xz = instance.config().nextRegion();
		
		int bx = xz[0] << 9;
		int bz = xz[1] << 9;
		
		World world = instance.getIslandWorld();
		int radius = instance.config().getRadius();
		int yLevel = instance.config().getYLevel();

		PlayerData playerData = GriefDefender.getCore().getPlayerData(world.getUID(), uuid).orElse(null);
		// BetterSkyBlock.getInstance().getGriefPrevention().dataStore.getPlayerData(uuid);
		//assert playerData != null;

		//playerData.setBonusClaimBlocks( playerData.getBonusClaimBlocks() + ((( instance.config().getRadius() * 2 ) + 1 ) * 2 ));

		Vector3i locL = new Vector3i((bx + 255 - radius), 0, (bz + 255 - radius));
		Vector3i locU = new Vector3i((bx + 255 + radius), 255, (bz + 255 + radius));

		ClaimResult result = Claim.builder()
				.world(world.getUID())
				.bounds(locL, locU)
				.owner(uuid)
				.requireClaimBlocks(false)
				.sizeRestrictions(false)
				.resizable(false)
				.type(ClaimTypes.TOWN)
				.build();

		//BetterSkyBlock.getInstance().getGriefPrevention().dataStore.createClaim(world, (bx + 255 - instance.config().radius), (bx + 255 + instance.config().radius), instance.config().yLevel, instance.config().yLevel, (bz + 255 - instance.config().radius), ( bz + 255 + instance.config().radius), uuid, null, null, null, false);
		//BetterSkyBlock.getInstance().getGriefPrevention().dataStore.savePlayerData(uuid, playerData);

		if (!result.successful()) {

			//			playerData.setBonusClaimBlocks( playerData.getBonusClaimBlocks() + ((( instance.config().radius * 2 ) + 1 ) * 2 ));
			// 			BetterSkyBlock.getInstance().getGriefPrevention().dataStore.savePlayerData(uuid, playerData);

			throw new Exception(result.getResultType().name());
		}
		
		instance.config().addNextRegion();
		instance.config().saveData();
		
		Island island = new Island(uuid, result.getClaim().orElse(null));

		try {

			instance.getDataStore().addIsland(island);

		} catch (SQLException e) {

			//BetterSkyBlock.getInstance().getGriefPrevention().dataStore.deleteClaim(result.claim);
			GriefDefender.getCore().getClaimManager(world.getUID()).deleteClaim(result.getClaim().orElse(null));
			throw new Exception("data store issue." + e.getMessage());
		}
		
		island.reset();
		
		return island;
	}

	void asyncUpdate(List<String> sql) {
		String[] arr = new String[(sql.size())];
		asyncUpdate(sql.toArray(arr));
	}

	void asyncUpdate(String... sql) {
		executor.execute(new DatabaseUpdate(sql));
	}
	
	Future<ResultSet> asyncQuery(String sql) {
		return executor.submit(new DatabaseQuery(sql));
	}
	
	Future<ResultSet> asyncUpdateGenKeys(String sql) {
		return executor.submit(new DatabaseUpdateGenKeys(sql));
	}
	
	synchronized void update(String sql) throws SQLException {
		this.update(this.statement(), sql);
	}
	
	synchronized void update(Statement statement, String sql) throws SQLException {
		statement.executeUpdate(sql);
	}
	
	synchronized void update(String... sql) throws SQLException {
		this.update(this.statement(), sql);
	}
	
	synchronized void update(Statement statement, String... sql) throws SQLException {
		for (String sqlRow : sql) {
			statement.executeUpdate(sqlRow);
		}
	}
	
	synchronized ResultSet query(String sql) throws SQLException {
		return this.query(this.statement(), sql);
	}
	
	synchronized ResultSet query(Statement statement, String sql) throws SQLException {
		return statement.executeQuery(sql);
	}
	
	synchronized ResultSet updateGenKeys(String sql) throws SQLException {
		return this.updateGenKeys(this.statement(), sql);
	}
	
	synchronized ResultSet updateGenKeys(Statement statement, String sql) throws SQLException {
		statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
		return statement.getGeneratedKeys();
	}
	
	synchronized Statement statement() throws SQLException {

		if(databaseConnection == null || databaseConnection.isClosed()) databaseConnection = mySQLDriver.connect();

		return databaseConnection.createStatement();
	}

	
	synchronized void databaseConnectionClose()  {
		try {
			if (databaseConnection.isClosed()) {
				databaseConnection.close();
				databaseConnection = null;
			}
		} catch (SQLException ignored) { }
	}
	
	public Island getIsland(UUID playerId) {
		return this.islands.get(playerId);
	}

    public void addIsland(Island island) throws SQLException {

		statement().executeUpdate("INSERT INTO betterskyblock_islands VALUES(" +

				Utils.UUIDtoHexString(island.getOwnerId()) + ", " +
				Utils.UUIDtoHexString(island.getClaim().getUniqueId()) + ", " +
				island.getSpawn().getBlockX() + ", " +
				island.getSpawn().getBlockY() + ", " +
				island.getSpawn().getBlockZ() +");"

		);

		islands.put(island.getOwnerId(), island);
	}
	
	public void removeIsland(Island island) throws SQLException {

		statement().executeUpdate("DELETE FROM betterskyblock_islands WHERE player = "+Utils.UUIDtoHexString(island.getOwnerId())+" LIMIT 1");

		islands.remove(island.getOwnerId());
	}

    public void updateIsland(Island island) throws SQLException {

		statement().executeUpdate("UPDATE betterskyblock_islands SET " +
				"sx = " + island.getSpawn().getBlockX() + ", " +
				"sy = " + island.getSpawn().getBlockY() + ", " +
				"sz = " + island.getSpawn().getBlockZ() + " WHERE " +
				"player = " + Utils.UUIDtoHexString(island.getOwnerId()) + " LIMIT 1");
	}
	
	private class DatabaseUpdate implements Runnable {

		private String[] sql;
		
		public DatabaseUpdate(String... sql) {
			this.sql = sql;
		}

		@Override
		public void run() {
			try {
				for (String sql : this.sql) {
					if (sql==null) {
						break;
					}
					update(sql);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class DatabaseUpdateGenKeys implements Callable<ResultSet> {
		private String sql;
		
		public DatabaseUpdateGenKeys(String sql) {
			this.sql = sql;
		}
		
		@Override
		public ResultSet call() throws Exception {
			return updateGenKeys(sql);
		}
		
	}
	
	private class DatabaseQuery implements Callable<ResultSet> {
		private String sql;
		
		public DatabaseQuery(String sql) {
			this.sql = sql;
		}
		
		@Override
		public ResultSet call() throws Exception {
			return query(sql);
		}
		
	}

	public MySQLDriver getMySQLDriver() {
		return mySQLDriver;
	}
}
