package br.com.syrxcraft.betterskyblock.islands;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.tasks.ResetIslandThread;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

public class Island {

    private final UUID ownerId;
    private final Claim claim;

    private Location spawn;
    private Location center;

    private int radius = -1;

    public boolean ready = true;


    public Island(UUID ownerId, Claim claim, Location spawn) {

        this.ownerId = ownerId;
        this.claim = claim;

        if (spawn != null) {
            this.spawn = spawn;
        } else {
            this.spawn = getCenter().add(0.5, 1, 0.5);
        }

    }

    public Island(UUID ownerId, Claim claim, int x, int y, int z) {
        this(ownerId, claim, new Location(Utils.worldFromUUID(claim.getWorldUniqueId()), (x + 0.5), y, (z + 0.5)));
    }

    public Island(UUID ownerId, Claim claim) {
        this(ownerId, claim, null);
    }


    public Claim getClaim() {
        return claim;
    }

    public Location getSpawn() {
        return spawn;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(ownerId);
    }

    public String getOwnerName() {

        String name = Bukkit.getOfflinePlayer(ownerId).getName();

        if (name != null) {
            return name;
        }

        return ownerId.toString();
    }

    public int getRadius() {

        if (radius != -1) {
            return radius;
        }

        int lx = claim.getLesserBoundaryCorner().getX();
        int gx = claim.getGreaterBoundaryCorner().getX();

        return radius = ((gx - lx) / 2);

    }

    public Location getCenter() {

        if (center != null)
            return center;

        return center = (
                new Location(
                        Utils.worldFromUUID(claim.getWorldUniqueId()),
                        claim.getLesserBoundaryCorner().getX() + getRadius(),
                        BetterSkyBlock.getInstance().config().getYLevel(),
                        claim.getLesserBoundaryCorner().getZ() + getRadius())
        );

    }

    public boolean isOwnerOnline() {
        return Bukkit.getPlayer(ownerId) != null;
    }


    public void setSpawn(Location location) throws SQLException {
        this.spawn = location;
        update();
    }

    public void setIslandBiome(Biome biome) {

        int sx = this.getClaim().getLesserBoundaryCorner().getX();
        int sz = this.getClaim().getLesserBoundaryCorner().getZ();
        int ux = this.getClaim().getGreaterBoundaryCorner().getX();
        int uz = this.getClaim().getGreaterBoundaryCorner().getZ();

        World world = Utils.worldFromUUID(claim.getWorldUniqueId());

        for (; sx <= ux; sx++) {
            for (int z = sz; z <= uz; z++) {
                world.setBiome(sx, z, biome);
            }
        }
    }

    public void setChunkBiome(Biome biome, int chunkX, int chunkZ) {

        int sx = chunkX << 4;
        int sz = chunkZ << 4;
        int ux = sx + 16;
        int uz = sz + 16;

        World world = Utils.worldFromUUID(claim.getWorldUniqueId());

        for (; sx < ux; sx++) {
            for (int z = sz; z < uz; z++) {
                world.setBiome(sx, z, biome);
            }
        }
    }

    public void setBlockBiome(Biome biome, int blockX, int blockZ) {
        Utils.worldFromUUID(claim.getWorldUniqueId()).setBiome(blockX, blockZ, biome);
    }


    public void teleportEveryoneToSpawn() {

        Location spawnLocation = BetterSkyBlock.getInstance().getSpawn();

        getClaim().getPlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            player.teleport(spawnLocation);
            player.sendMessage("Você foi teleportado para o spawn."); //TODO: Lang
        });
    }

    public void deleteRegionFile() {

        int x = getSpawn().getBlockX() >> 9;
        int z = getSpawn().getBlockZ() >> 9;

        File regionFile = new File(getSpawn().getWorld().getWorldFolder(), "region" + File.separator + "r." + x + "." + z + ".mca");

        if (!regionFile.delete()) {
            regionFile.deleteOnExit();
        }

    }

    public void delete() throws SQLException {

        ClaimManager claimManager = BetterSkyBlock.getInstance().getClaimManager();
        Claim claim = getClaim();

        this.teleportEveryoneToSpawn();

        if (claim != null) {
            if (!claimManager.deleteClaim(getClaim(), true).successful()) {
                BetterSkyBlock.getInstance().getLoggerHelper().warn("Delete task from " + ownerId + " island, cannot remove the claim. {Claim ID: " + claim.getUniqueId().toString() + "}");
            }
        }

        if (BetterSkyBlock.getInstance().config().deleteRegion()) {
            deleteRegionFile();
        }

        BetterSkyBlock.getInstance().getDataStore().removeIsland(this);
    }

    public void reset() {

        try {

            File schematicFile = new File(BetterSkyBlock.getInstance().getDataFolder(), BetterSkyBlock.getInstance().config().getSchematic() + ".schematic");

            if (!schematicFile.exists()) {
                throw new IllegalStateException("Schematic file '" + BetterSkyBlock.getInstance().config().getSchematic() + ".schematic' doesn't exist");
            }

            teleportEveryoneToSpawn();

            ready = false;

            new ResetIslandThread(this, schematicFile);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void update() {
        BetterSkyBlock.getInstance().getDataStore().updateIsland(this);
    }
}
