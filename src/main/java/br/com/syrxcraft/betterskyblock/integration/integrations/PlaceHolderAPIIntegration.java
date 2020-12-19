package br.com.syrxcraft.betterskyblock.integration.integrations;

import br.com.syrxcraft.betterskyblock.api.BetterSkyBlockAPI;
import br.com.syrxcraft.betterskyblock.integration.IIntegration;
import br.com.syrxcraft.betterskyblock.utils.GriefDefenderUtils;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.permission.flag.Flags;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;

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

                            System.out.println(tristate != null);
                            System.out.println(tristate.asBoolean());

                            return tristate != null && tristate.asBoolean() ? "Sim" : "Não"; // TODO: fix
                        }

                        return "Você não possui uma ilha ainda!"; // TODO: Lang Support
                    }

                    case "island_public_boolean":{

                        Tristate tristate = GriefDefenderUtils.getClaimFlagPermission(island.getClaim(),Flags.ENTER_CLAIM.getPermission());

                        System.out.println(tristate != null);
                        System.out.println(tristate.asBoolean());

                        return "" + (tristate != null && tristate.asBoolean());

                    }

                    case "island_radius": {
                        return "" + ((island != null) ? island.getRadius() : 0);
                    }

                    case "island_trust":{
                        return "" + island.getClaim().getUserTrusts().size();
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
