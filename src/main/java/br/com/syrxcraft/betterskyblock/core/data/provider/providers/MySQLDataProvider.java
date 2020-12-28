package br.com.syrxcraft.betterskyblock.core.data.provider.providers;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.core.data.provider.IDataProvider;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionHolder;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionType;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.google.common.collect.Maps;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;
import com.griefdefender.api.claim.TrustTypes;

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
                    "id binary(16) NOT NULL," +
                    "player binary(16) NOT NULL, " +
                    "claimid binary(16) NOT NULL, " +
                    "sx int(11) NOT NULL, " +
                    "sy int(11) NOT NULL, " +
                    "sz int(11) NOT NULL, " +
                    "PRIMARY KEY (id));"
            );

            statement.executeUpdate("" +
                    "CREATE TABLE IF NOT EXISTS betterskyblock_permissions (" +
                    "islandid binary(16) NOT NULL," +
                    "player binary(16) NOT NULL," +
                    "perm int NOT NULL," +
                    "CONSTRAINT fk_islandid FOREIGN KEY (islandid) REFERENCES betterskyblock_islands (id) ON DELETE CASCADE," +
                    "CONSTRAINT pk_islandidplayer PRIMARY KEY (islandid, player));"
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

        instance.getDataStore().saveAll();

        return true;
    }

    @Override
    public HashMap<UUID, Island> loadData() {

        try{

            ResultSet rs = statement().executeQuery("SELECT * FROM betterskyblock_islands;");

            islands.clear();

            ClaimManager claimManager = instance.getClaimManager();

            while (rs.next()) {

                UUID islandUUID = Utils.toUUID(rs.getBytes(1));

                PermissionHolder holder = PermissionHolder.createInstance();

                ResultSet rs2 = statement().executeQuery("SELECT * FROM betterskyblock_permissions WHERE islandid = " + Utils.UUIDtoHexString(islandUUID) + ";");


                while (rs2.next()){

                    UUID player = Utils.toUUID(rs2.getBytes(2));
                    PermissionType permission = PermissionType.getPermissionType(rs2.getInt(3));

                    holder.updatePermission(player, permission);

                }

                UUID owner = Utils.toUUID(rs.getBytes(2));
                Claim claim = claimManager.getClaimByUUID(Utils.toUUID(rs.getBytes(3))).orElse(null);

                int x = rs.getInt(4);
                int y = rs.getInt(5);
                int z = rs.getInt(6);

                if (claim != null) {

                    if(holder.isEmpty()) holder.updatePermission(owner, PermissionType.OWNER);

                    holder.getPermissions().forEach((uuid, type) -> {
                        switch (type){

                            case MEMBER:{
                                claim.addUserTrust(uuid, TrustTypes.BUILDER);
                                return;
                            }

                            case ADMINISTRATOR:{
                                claim.addUserTrust(uuid, TrustTypes.MANAGER);
                            }

                        }
                    });

                    Island island = new Island(islandUUID, owner, claim, holder, x, y, z);
                    islands.put(owner, island);

                    continue;
                }

                instance.getLoggerHelper().warn("Unable to load island for: " + owner + " claim is null... [ X: " + x + " | Y: " + y + " | Z: " + z + "]");
            }



        }catch (Exception e) {
            e.printStackTrace();
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
                    Utils.UUIDtoHexString(island.getIslandId())             + ", " +
                    Utils.UUIDtoHexString(island.getOwnerId())              + ", " +
                    Utils.UUIDtoHexString(island.getClaim().getUniqueId())  + ", " +
                    island.getSpawn().getBlockX()                           + ", " +
                    island.getSpawn().getBlockY()                           + ", " +
                    island.getSpawn().getBlockZ()                           + ");"
            );

            Map<UUID, PermissionType> permissions = island.permissionHolder.getPermissions();

            statement().executeUpdate("DELETE FROM betterskyblock_permissions WHERE islandid = " + Utils.UUIDtoHexString(island.getIslandId()) + ";");

            permissions.forEach(((uuid, permissionType) -> {

                try {
                    statement().executeUpdate("INSERT INTO betterskyblock_permissions (ISLANDID, PLAYER, PERM) VALUES (" +
                            Utils.UUIDtoHexString(island.getIslandId())         + ", " +
                            Utils.UUIDtoHexString(uuid)                         + ", " +
                            permissionType.intPermission()                      +");"
                    );
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }));

        } catch (SQLException exception) {
            instance.getLoggerHelper().error("Unable to save a island on database. Details: " + exception.getMessage());
        }

    }

    @Override
    public void removeIsland(Island island) {
        try{
            statement().executeUpdate("DELETE FROM betterskyblock_islands WHERE id = " + Utils.UUIDtoHexString(island.getIslandId()) + " LIMIT 1");
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
