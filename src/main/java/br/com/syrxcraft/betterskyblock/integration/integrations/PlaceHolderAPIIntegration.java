package br.com.syrxcraft.betterskyblock.integration.integrations;

import br.com.syrxcraft.betterskyblock.core.api.BetterSkyBlockAPI;
import br.com.syrxcraft.betterskyblock.integration.IIntegration;
import br.com.syrxcraft.betterskyblock.utils.GriefDefenderUtils;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.permission.flag.Flags;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import org.bukkit.entity.Player;

public class PlaceHolderAPIIntegration extends PlaceholderExpansion implements IIntegration {

    @Override
    public String targetPlugin() {
        return "PlaceholderAPI";
    }

    @Override
    public String targetVersion() {
        return "*";
    }

    @Override
    public boolean load() {
        return new PlaceHolderAPIIntegration().register();
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {

        BetterSkyBlockAPI betterSkyBlockAPI = BetterSkyBlockAPI.getInstance();

        if(betterSkyBlockAPI != null){
            if(player != null){

                Island island = betterSkyBlockAPI.getPlayerIsland(player);

                switch (placeholder.toLowerCase()){

                    case "island_count":{
                        return "" + betterSkyBlockAPI.getIslandCount();
                    }

                    case "island_public":{

                        if(island != null){

                            Tristate tristate = GriefDefenderUtils.getClaimFlagPermission(island.getClaim(),Flags.ENTER_CLAIM.getPermission());

                            return tristate != null && tristate.asBoolean() ? "Sim" : "Não";
                        }

                        return "Você não possui uma ilha ainda!";
                    }

                    case "island_public_boolean":{

                        if(island != null){
                            Tristate tristate = GriefDefenderUtils.getClaimFlagPermission(island.getClaim(),Flags.ENTER_CLAIM.getPermission());

                            return "" + (tristate != null && tristate.asBoolean());
                        }

                    }

                    case "island_radius": {
                        return "" + ((island != null) ? island.getRadius() : 0);
                    }

                    case "island_members":{
                        return "" + ((island != null) ? island.getPermissionHolder().getPermissions().size() : 0);
                    }

                    default: {
                        return "";
                    }
                }
            }
        }

        return "";

    }

    @Override
    public String getIdentifier() {
        return "betterskyblock";
    }

    @Override
    public String getAuthor() {
        return "brunoxkk0";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
