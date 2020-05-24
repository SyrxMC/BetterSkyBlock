package br.com.syrxcraft.betterskyblock.islands;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.tasks.ResetIslandTask;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.griefdefender.api.claim.Claim;
import net.kyori.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class Island {

	private final UUID ownerId;
	private final Claim claim;
	private Location spawn;
	public boolean ready = true;
	
	public Island(UUID ownerId, Claim claim) {
		this.ownerId = ownerId;
		this.claim = claim;
		this.spawn = getCenter().add(0.5, 1, 0.5);
	}
	
	public Island(UUID ownerId, Claim claim, Location spawn) {
		this.ownerId = ownerId;
		this.claim = claim;
		this.spawn = spawn;
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
		return PlainComponentSerializer.INSTANCE.serialize(claim.getOwnerName());
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

			this.teleportEveryoneToSpawn();

			this.ready = false;
			new ResetIslandTask(this, schematicFile).runTaskTimer(BetterSkyBlock.getInstance(), 1L, 1L);
			//new ResetIslandThread(this, schematicFile);
		}catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public int getRadius() {

		int lx = claim.getLesserBoundaryCorner().getX();
		int gx = claim.getGreaterBoundaryCorner().getX();

		return (gx-lx)/2;

	}
	
	public void setRadius(int radius) {

		if (radius > 254 || radius < 1) {
			throw new IllegalArgumentException("Invalid radius (max 254)");
		}

		Location center = getCenter();
		//int size = claim.getArea();

		claim.resize((center.getBlockX() - radius), (center.getBlockX() + radius),claim.getLesserBoundaryCorner().getY(), claim.getGreaterBoundaryCorner().getY(),(center.getBlockZ() - radius), (center.getBlockZ() + radius));

		//BetterSkyBlock.getInstance().getGriefPrevention().dataStore.resizeClaim(claim, (center.getBlockX() - radius), (center.getBlockX() + radius),center.getBlockY(), center.getBlockY(),(center.getBlockZ() - radius), (center.getBlockZ() + radius), player);
		//PlayerData playerData = BetterSkyBlock.getInstance().getGriefPrevention().dataStore.getPlayerData(ownerId);
		//playerData.setBonusClaimBlocks(playerData.getBonusClaimBlocks() + (claim.getArea() - size));
		//BetterSkyBlock.getInstance().getGriefPrevention().dataStore.savePlayerData(ownerId, playerData);

		//GriefPreventionPlus.getInstance().getDataStore().resizeClaim(this.claim, center.getBlockX()-radius, center.getBlockZ()-radius, center.getBlockX()+radius, center.getBlockZ()+radius, null);
		//PlayerData playerData = GriefPreventionPlus.getInstance().getDataStore().getPlayerData(ownerId);
		//playerData.setBonusClaimBlocks(playerData.getBonusClaimBlocks()+(this.claim.getArea()-size));
		//GriefPreventionPlus.getInstance().getDataStore().savePlayerData(ownerId, playerData);
	}
	
	public Location getCenter() {
		return new Location(Utils.worldFromUUID(claim.getWorldUniqueId()), claim.getLesserBoundaryCorner().getX() + getRadius(), BetterSkyBlock.getInstance().config().getYLevel(), claim.getLesserBoundaryCorner().getZ() + getRadius());
	}
	
	public void teleportEveryoneToSpawn() {
		Location spawnLocation = BetterSkyBlock.getInstance().getSpawn();
		getClaim().getPlayers().forEach(uuid -> Bukkit.getPlayer(uuid).teleport(spawnLocation));
//		for (Player player : Bukkit.getOnlinePlayers()) {
//			getClaim().getPlayers()
//			if (getClaim().contains(player.getLocation(), true, false)) {
//				player.teleport(spawnLocation);
//			}
//		}
	}
	
	public void setSpawn(Location location) throws SQLException {
		this.spawn = location;
		BetterSkyBlock.getInstance().getDataStore().updateIsland(this);
	}
	
	public void setIslandBiome(Biome biome) {

		int x = this.getClaim().getLesserBoundaryCorner().getX();
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

		File regionFile = new File(this.getSpawn().getWorld().getWorldFolder(), "region" + File.separator + "r."+x+"."+z+".mca");

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
}
