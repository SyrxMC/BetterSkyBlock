package br.com.syrxcraft.betterskyblock.core.api;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.GriefDefenderUtils;
import br.com.syrxcraft.betterskyblock.utils.IslandUtils;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.permission.flag.Flags;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BetterSkyBlockAPI {

    private final BetterSkyBlock pluginInstance;

    private static BetterSkyBlockAPI INSTANCE;

    public static BetterSkyBlockAPI getInstance() {
        return INSTANCE;
    }

    public BetterSkyBlockAPI(BetterSkyBlock betterSkyBlock){

        INSTANCE = this;
        pluginInstance = betterSkyBlock;

        betterSkyBlock.getLoggerHelper().info("Init API");

    }

    public int getIslandCount(){
        return pluginInstance.getDataStore().getTotalOfIslands();
    }

    public Island getPlayerIsland(Player player){
        return getPlayerIsland(player.getUniqueId());
    }

    public Island getPlayerIsland(UUID player){
        return IslandUtils.getPlayerIsland(player);
    }

    public boolean hasIsland(Player player){
        return getPlayerIsland(player) != null;
    }

    public boolean isIslandPublic(Player player){

        Island island = getPlayerIsland(player);
        Tristate tristate = GriefDefenderUtils.getClaimFlagPermission(island.getClaim(), Flags.ENTER_CLAIM.getPermission());

        return tristate != null && tristate.asBoolean();
    }

}
