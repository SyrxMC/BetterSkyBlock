package br.com.syrxcraft.betterskyblock.core.data;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.core.data.provider.DataProvider;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionHolder;
import br.com.syrxcraft.betterskyblock.core.permission.PermissionType;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimResult;
import com.griefdefender.api.claim.ClaimTypes;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataStore {

	private static DataStore INSTANCE;

	public static DataStore getInstance(){
		return INSTANCE != null ? INSTANCE : new DataStore();
	}

	private final BetterSkyBlock instance;
	private final DataProvider dataProvider;

	private static final HashMap<UUID, Island> islands = new HashMap<>();

	private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setDaemon(true).build());
	private final Queue<Island> updatedIslands = new LinkedList<>();

	private DataStore(){

		INSTANCE = this;
		this.instance = BetterSkyBlock.getInstance();

		if(instance.config().getDataProvider() == null)
			throw new RuntimeException("Invalid data provider.");

		dataProvider = new DataProvider(instance.config().getDataProvider());

		if(!dataProvider.onLoad(instance)){
			throw new RuntimeException();
		}


		Map<UUID, Island> data = dataProvider.loadData();

		if(data == null){
			throw new RuntimeException("Unable to load islands...");
		}

		islands.putAll(data);


		instance.getLoggerHelper().info("Loaded " + getTotalOfIslands() + " islands...");

		service.scheduleAtFixedRate(() -> {

			if(!updatedIslands.isEmpty()){

				Island island = updatedIslands.poll();

				dataProvider.saveIsland(island);
				BetterSkyBlock.getInstance().getLoggerHelper().info("Island " + island.getIslandId() + " was saved on database.");
			}
		}, 0, 25, TimeUnit.MILLISECONDS);

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
				.expire(false)
				.levelRestrictions(false)
				.build();

		if (!result.successful()) {
			throw new Exception(result.getResultType().name());
		}
		
		instance.config().addNextRegion();
		instance.config().saveData();

		Claim claim = result.getClaim().orElse(null);

		PermissionHolder permissionHolder = PermissionHolder.createInstance();
		permissionHolder.updatePermission(uuid, PermissionType.OWNER);

		UUID islandUUID = UUID.randomUUID();

		Island island = new Island(islandUUID, uuid, claim, permissionHolder);
		island.setGenerating(true);
		try {

			instance.getDataStore().addIsland(island);

		} catch (SQLException e) {

			if(claim != null){
				GriefDefender.getCore().getClaimManager(world.getUID()).deleteClaim(claim);
			}

			throw new Exception("data store issue." + e.getMessage());
		}
		
		island.reset();
		
		return island;
	}

	
	public Island getIsland(UUID playerId) {
		return islands.get(playerId);
	}

    public void addIsland(Island island) throws SQLException {
		dataProvider.saveIsland(island);
		islands.put(island.getOwnerId(), island);
	}
	public void addIslandAndQueueUpdate(Island island) {
		islands.put(island.getOwnerId(), island);
		updateIsland(island);
	}
	public void removeIsland(Island island) {
		dataProvider.removeIsland(island);
		islands.remove(island.getOwnerId());
	}

	public void transferIslandClaim(Island island, UUID newOwnerUUID) {
		island.getClaim().transferOwner(newOwnerUUID);
	}

    public void updateIsland(Island island) {
		updatedIslands.add(island);
		//dataProvider.saveIsland(island);
	}

	public DataProvider getDataProvider() {
		return dataProvider;
	}

	public int getTotalOfIslands(){
		return islands.size();
	}

	public void saveAll(){
		islands.forEach((uuid, island) -> updateIsland(island));
	}

	public void processSaveQueue(){

		if (!service.isShutdown() && !service.isTerminated() && !updatedIslands.isEmpty()){
			try {

				boolean success = service.awaitTermination(30, TimeUnit.SECONDS);

				if (!success){
					BetterSkyBlock.getInstance().getLoggerHelper().error("Process queue task is coming out after 30 seconds.");
					service.shutdown();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
