package br.com.syrxcraft.betterskyblock.data;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.data.providers.DataProvider;
import br.com.syrxcraft.betterskyblock.data.providers.Providers;
import br.com.syrxcraft.betterskyblock.islands.Island;
import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimResult;
import com.griefdefender.api.claim.ClaimTypes;
import com.griefdefender.api.data.PlayerData;
import net.kyori.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class DataStore {

	private final BetterSkyBlock instance;
	private final DataProvider dataProvider;
	private final Map<UUID, Island> islands;

	public DataStore(BetterSkyBlock instance){

		this.instance = instance;

		if(instance.config().getDataProvider() == null)
			throw new RuntimeException("Invalid data provider.");

		dataProvider = new DataProvider(instance.config().getDataProvider());

		if(!dataProvider.onLoad(instance)){
			throw new RuntimeException();
		}

		if((islands = dataProvider.loadData()) == null){
			throw new RuntimeException("Unable to load islands...");
		}

		instance.getLoggerHelper().info("Loaded " + getTotalOfIslands() + " islands...");

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

		String name = Bukkit.getPlayer(uuid).getName();

		ClaimResult result = Claim.builder()
				.world(world.getUID())
				.bounds(locL, locU)
				.owner(uuid)
				.requireClaimBlocks(false)
				.sizeRestrictions(false)
				.resizable(false)
				.farewell(TextComponent.of("§3" + name + "'s island."))
				.greeting(TextComponent.of("§3" + name + "'s island."))
				.type(ClaimTypes.TOWN)
				.build();

		if (!result.successful()) {
			throw new Exception(result.getResultType().name());
		}
		
		instance.config().addNextRegion();
		instance.config().saveData();

		Claim claim = result.getClaim().orElse(null);
		Island island = new Island(uuid, claim);

		try {

			instance.getDataStore().addIsland(island);

		} catch (SQLException e) {

			GriefDefender.getCore().getClaimManager(world.getUID()).deleteClaim(result.getClaim().orElse(null));
			throw new Exception("data store issue." + e.getMessage());
		}
		
		island.reset();
		
		return island;
	}

	
	public Island getIsland(UUID playerId) {
		return this.islands.get(playerId);
	}

    public void addIsland(Island island) throws SQLException {
		dataProvider.saveIsland(island);
		islands.put(island.getOwnerId(), island);
	}
	
	public void removeIsland(Island island) throws SQLException {
		dataProvider.removeIsland(island);
		islands.remove(island.getOwnerId());
	}

    public void updateIsland(Island island) throws SQLException {
		dataProvider.saveIsland(island);
	}

	public DataProvider getDataProvider() {
		return dataProvider;
	}

	public int getTotalOfIslands(){
		return islands.size();
	}
}
