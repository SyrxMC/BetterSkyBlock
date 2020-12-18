package br.com.syrxcraft.betterskyblock.data.provider.providers;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.data.provider.IDataProvider;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.google.common.collect.Maps;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.*;

public class MySQLDataProvider implements IDataProvider {

    private MySQLDriver mySQLDriver;
    private BetterSkyBlock instance;
    private Connection databaseConnection;
    private HashMap<UUID, Island> islands;

    @Override
    public boolean onLoad(BetterSkyBlock instance) {

        this.instance = instance;

        islands = Maps.newHashMap();

        mySQLDriver = new MySQLDriver(
                instance.config().getDbHostname(),
                instance.config().getDbUsername(),
                instance.config().getDbPassword(),
                instance.config().getDbDatabase()
        );

        if(!mySQLDriver.isDriverAvailable()){
            instance.getLoggerHelper().error("Unable to load Java's mySQL database driver.  Check to make sure you've installed it properly.");
            return false;
        }

        try {
            databaseConnection = mySQLDriver.connect();
        } catch(Exception e) {
            instance.getLoggerHelper().error("Unable to connect to database.  Check your config file settings. Details: \n" + e.getMessage());
            return false;
        }

        Statement statement;

        try {

            statement = databaseConnection.createStatement();

            // Creates tables on the database
            statement.executeUpdate("" +
                    "CREATE TABLE IF NOT EXISTS betterskyblock_islands (" +
                    "player binary(16) NOT NULL, " +
                    "claimid binary(16) NOT NULL, " +
                    "sx int(11) NOT NULL, " +
                    "sy int(11) NOT NULL, " +
                    "sz int(11) NOT NULL, " +
                    "PRIMARY KEY (player));"
            );

        } catch(Exception e) {
            instance.getLoggerHelper().error("Unable to create the necessary database table. Details: \n" + e.getMessage());
            return false;
        }

        instance.getLoggerHelper().info("MySQLDataProvider - Loaded!");

        return true;
    }

    @Override
    public boolean onStop(BetterSkyBlock instance) {
        return true;
    }

    @Override
    public HashMap<UUID, Island> loadData() {

        try{

            ResultSet rs = statement().executeQuery("SELECT * FROM betterskyblock_islands;");

            islands.clear();

            ClaimManager claimManager = instance.getClaimManager();

            while (rs.next()) {

                UUID uuid = Utils.toUUID(rs.getBytes(1));
                Claim claim = claimManager.getClaimByUUID(Utils.toUUID(rs.getBytes(2))).orElse(null);

                int x = rs.getInt(3);
                int y = rs.getInt(4);
                int z = rs.getInt(5);

                if (claim != null) {

                    Island island = new Island(uuid, claim, x, y, z);
                    islands.put(uuid, island);

                    continue;
                }

                instance.getLoggerHelper().warn("Unable to load island for: " + uuid + " claim is null... [ X: " + x + " | Y: " + y + " | Z: " + z + "]");
            }

        }catch (Exception e) {
            instance.getLoggerHelper().error("Unable to load islands from database. Details: \n" + e.getMessage());
            return null;
        }

        return islands;
    }

    @Override
    public void saveData(Set<Island> islands) {
        for(Island island : islands){
            saveIsland(island);
        }
    }

    @Override
    public void saveData(Map<UUID, Island> islands) {
        for(Island island : islands.values()){
            saveIsland(island);
        }
    }

    @Override
    public void saveIsland(Island island) {

        try {
            statement().executeUpdate("REPLACE INTO betterskyblock_islands VALUES(" +

                    Utils.UUIDtoHexString(island.getOwnerId()) + ", " +
                    Utils.UUIDtoHexString(island.getClaim().getUniqueId()) + ", " +
                    island.getSpawn().getBlockX() + ", " +
                    island.getSpawn().getBlockY() + ", " +
                    island.getSpawn().getBlockZ() + ");"

            );
        } catch (SQLException exception) {
            instance.getLoggerHelper().error("Unable to save a island on database. Details: " + exception.getMessage());
        }

    }


    @Override
    public void removeIsland(Island island) {
        try{
            statement().executeUpdate("DELETE FROM betterskyblock_islands WHERE player = " + Utils.UUIDtoHexString(island.getOwnerId()) + " LIMIT 1");
        }
        catch (SQLException exception) {
            instance.getLoggerHelper().error("Unable to remove a island on database. Details: " + exception.getMessage());
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    synchronized Statement statement() throws SQLException {

        if(databaseConnection == null || databaseConnection.isClosed()){
            databaseConnection = mySQLDriver.connect();
        }

        return databaseConnection.createStatement();
    }

}

class MySQLDriver {

    private final String host;
    private final String username;
    private final String password;
    private final String database;

    public MySQLDriver(@Nonnull String host, @Nonnull String username, @Nonnull String password, @Nonnull String database) {

        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;

    }

    public boolean isDriverAvailable() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    public String getTargetConnection() {
        return "jdbc:mysql://" + host + "/" + database;
    }

    public synchronized Connection connect() throws SQLException {

        if (!isDriverAvailable()) {
            throw new SQLException("Driver is not available.");
        }

        Properties properties = new Properties();

        properties.setProperty("user", username);
        properties.setProperty("password", password);

        return DriverManager.getConnection(getTargetConnection(), properties);

    }
}
