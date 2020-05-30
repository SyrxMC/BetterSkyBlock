package br.com.syrxcraft.betterskyblock.islands;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.tasks.ResetIsland;
import br.com.syrxcraft.betterskyblock.islands.tasks.ResetIslandTask;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;
import net.kyori.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Island {

	private final UUID ownerId;
	private final Claim claim;
	public boolean ready = true;
	private Location spawn;

	public Island(UUID ownerId, Claim claim, Location spawn) {

		this.ownerId = ownerId;
		this.claim = claim;

		if(spawn != null){
			this.spawn = spawn;
		}else {
			this.spawn =  getCenter().add(0.5, 1, 0.5);
		}

	}

	public Island(UUID ownerId, Claim claim) {
		this(ownerId, claim,null);
	}

	public Island(UUID ownerId, Claim claim, int x, int y, int z) {
		this(ownerId, claim, new Location(Utils.worldFromUUID(claim.getWorldUniqueId()),(x + 0.5), y , (z + 0.5)));
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

		if(name != null){
			return name;
		}

		return ownerId.toString();
	}
	
	public boolean isOwnerOnline() {
		return Bukkit.getPlayer(ownerId) != null;
	}
	
	public void reset() {

		try {

			File schematicFile = new File(BetterSkyBlock.getInstance().getDataFolder(), BetterSkyBlock.getInstance().config().getSchematic() + ".schematic");

			if (!schematicFile.exists()) {
				throw new IllegalStateException("Schematic file \""+ BetterSkyBlock.getInstance().config().getSchematic() + ".schematic\" doesn't exist");
			}

			teleportEveryoneToSpawn();

			ready = false;

			new ResetIslandTask(this, schematicFile).runTaskTimer(BetterSkyBlock.getInstance(), 1L, 1L);

		}catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public int getRadius() {

		int lx = claim.getLesserBoundaryCorner().getX();
		int gx = claim.getGreaterBoundaryCorner().getX();

		return (gx-lx) / 2;

	}
	
	public void setRadius(int radius) {

		if (radius > 254 || radius < 1) {
			throw new IllegalArgumentException("Invalid radius (max 254)");
		}

		Location center = getCenter();

		claim.resize((center.getBlockX() - radius), (center.getBlockX() + radius),claim.getLesserBoundaryCorner().getY(), claim.getGreaterBoundaryCorner().getY(),(center.getBlockZ() - radius), (center.getBlockZ() + radius));
	}
	
	public Location getCenter() {
		return new Location(Utils.worldFromUUID(claim.getWorldUniqueId()), claim.getLesserBoundaryCorner().getX() + getRadius(), BetterSkyBlock.getInstance().config().getYLevel(), claim.getLesserBoundaryCorner().getZ() + getRadius());
	}
	
	public void teleportEveryoneToSpawn() {
		Location spawnLocation = BetterSkyBlock.getInstance().getSpawn();
		getClaim().getPlayers().forEach(uuid -> Bukkit.getPlayer(uuid).teleport(spawnLocation));
	}
	
	public void setSpawn(Location location) throws SQLException {
		this.spawn = location;
		BetterSkyBlock.getInstance().getDataStore().updateIsland(this);
	}
	
	public void setIslandBiome(Biome biome) {

		int x  = this.getClaim().getLesserBoundaryCorner().getX();
		int sz = this.getClaim().getLesserBoundaryCorner().getZ();
		int ux = this.getClaim().getGreaterBoundaryCorner().getX();
		int uz = this.getClaim().getGreaterBoundaryCorner().getZ();

		World world = Utils.worldFromUUID(claim.getWorldUniqueId());

		for (; x <= ux; x++) {
			for (int z = sz; z <= uz; z++) {
				world.setBiome(x, z, biome);
			}
		}
	}
	
	public void setChunkBiome(Biome biome, int chunkX, int chunkZ) {
		int x = chunkX << 4;
		int sz = chunkZ << 4;
		int ux = x + 16;
		int uz = sz + 16;

		World world = Utils.worldFromUUID(claim.getWorldUniqueId());

		for (; x < ux; x++) {
			for (int z = sz; z < uz; z++) {
				world.setBiome(x, z, biome);
			}
		}
	}
	
	public void setBlockBiome(Biome biome, int blockX, int blockZ) {
		Utils.worldFromUUID(claim.getWorldUniqueId()).setBiome(blockX, blockZ, biome);
	}
	
	public void deleteRegionFile() {

		int x = getSpawn().getBlockX() >> 9;
		int z = getSpawn().getBlockZ() >> 9;

		File regionFile = new File(getSpawn().getWorld().getWorldFolder(), "region" + File.separator + "r." + x + "." + z + ".mca");

		if (!regionFile.delete()) {
			regionFile.deleteOnExit();
		}

	}

	void removeThisIslandFromServer() {

		int x = this.getSpawn().getBlockX() >> 9;
		int z = this.getSpawn().getBlockZ() >> 9;

		File regionFile = new File(this.getSpawn().getWorld().getWorldFolder(), "region" + File.separator + "r."+x+"."+z+".mca");

		if (!regionFile.delete()) {
			regionFile.deleteOnExit();
		}

	}

	public void delete() throws SQLException {
		ClaimManager claimManager = BetterSkyBlock.getInstance().getClaimManager();
		UUID uuid = null;

		if(getClaim() != null){
			uuid = getClaim().getUniqueId();
			claimManager.deleteClaim(getClaim());
		}

		//Sanity Check
		if(uuid != null && claimManager.getClaimByUUID(uuid).isPresent()){
			claimManager.getClaimByUUID(uuid).ifPresent(claimManager::deleteClaim);
			BetterSkyBlock.getInstance().getLoggerHelper().warn("Delete task from island, uses the sanity check: isPresent ? " + claimManager.getClaimByUUID(uuid).isPresent());
		}

		if (BetterSkyBlock.getInstance().config().deleteRegion()) {
			deleteRegionFile();
		}

		BetterSkyBlock.getInstance().getDataStore().removeIsland(this);
	}
}
