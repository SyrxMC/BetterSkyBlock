package br.com.syrxcraft.betterskyblock.islands;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.griefdefender.api.claim.Claim;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class IslandUtils {

    public static boolean isIsland(Claim claim) {
        return getIsland(claim) != null;
    }

    public static Island getIsland(Claim claim) {

        if (!isIslandWorld(Utils.worldFromUUID(claim.getWorldUniqueId()))) {
            return null;
        }

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(claim.getOwnerUniqueId());

//        System.out.println(island.getClaim().getUniqueId());
//        System.out.println(claim.getUniqueId());

        if (island != null && island.getClaim().getUniqueId().equals(claim.getUniqueId())) {
            return island;
        }

        return null;
    }

    public static boolean isIslandWorld(World world) {
        return world.getName().equals(BetterSkyBlock.getInstance().config().getWorldName());
    }

    public static Island isOnIsland(Player player){

        if(player != null){
            Claim claim = BetterSkyBlock.getInstance().getClaimManager().getClaimAt(Utils.locationToVector(player.getLocation()));

            if(claim != null && isIsland(claim)){
                return getIsland(claim);
            }

        }

        return null;
    }

}
