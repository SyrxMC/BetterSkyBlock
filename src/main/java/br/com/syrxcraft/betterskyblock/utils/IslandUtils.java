package br.com.syrxcraft.betterskyblock.utils;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import com.griefdefender.api.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.WeakHashMap;

public class IslandUtils {

    public static WeakHashMap<UUID, Claim> CACHED_PLAYER_POSITIONS = new WeakHashMap<>();

    public static boolean isIsland(Claim claim) {
        return getIsland(claim) != null;
    }

    public static Island getIsland(Claim claim) {

        if (!isIslandWorld(Utils.worldFromUUID(claim.getWorldUniqueId())) || claim.isWilderness()) {
            return null;
        }

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(claim.getOwnerUniqueId());

        if (island != null && island.getClaim().getUniqueId().equals(claim.getUniqueId())) {
            return island;
        }

        return null;
    }

    public static boolean isIslandWorld(World world) {
        return world.getName().equals(BetterSkyBlock.getInstance().config().getWorldName());
    }

    public static Island getCurrentIsland(Player player){

        if(player != null){

            Claim claim = BetterSkyBlock.getInstance().getClaimManager().getClaimAt(Utils.locationToVector(player.getLocation()));

            return getIsland(claim);

        }

        return null;
    }

    public static Island getPlayerIsland(Player player){
        return BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());
    }

    public static Island getPlayerIsland(UUID player){
        return BetterSkyBlock.getInstance().getDataStore().getIsland(player);
    }

    public static World getIslandWorld(Island island){

        if(island != null){
            return Bukkit.getWorld(island.getClaim().getWorldUniqueId());
        }

        return null;
    }

}
