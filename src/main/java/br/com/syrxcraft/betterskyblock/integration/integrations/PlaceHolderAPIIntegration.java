package br.com.syrxcraft.betterskyblock.integration.integrations;

import br.com.syrxcraft.betterskyblock.integration.IIntegration;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceHolderAPIIntegration extends EZPlaceholderHook implements IIntegration {

    @Override
    public String targetPlugin() {
        return "PlaceHolderAPI";
    }

    @Override
    public String targetVersion() {
        return "*";
    }

    @Override
    public boolean load() {
        PlaceHolderAPIIntegration instance = new PlaceHolderAPIIntegration(BetterSkyBlock.getInstance(), "gppskyblock");

        return instance.hook();
    }

    public PlaceHolderAPIIntegration(Plugin plugin, String identifier) {
        super(plugin, identifier);
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {

        switch (placeholder){
            case "total_ilhas":
                return totalOfIslands();
        }

        if (player == null){
            return "";
        }

        switch (placeholder){
            case "island_is_public":
                return islandIsPublic(player);
            case "island_is_public_command":
                if (playersIslandIsPublic(player)){
                    return "private";
                }
                return "public";
            case "island_radius":
                return islandRadius(player);
        }

        return null;
    }

    private static String islandIsPublic(Player player){
        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island == null){
            return "&cVocê não possui uma ilha ainda!";
        }

//        if (island.getClaim().getPermission(GriefPreventionPlus.UUID0) == 16){
//            return "Sim";
//        }
        return "Não";
    }

    public static boolean playersIslandIsPublic(Player player){

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island != null){
//            if (island.getClaim().getPermission(GriefPreventionPlus.UUID0) == 16){
//                return true;
//            }
        }

        return false;
    }

    private static String islandRadius(Player player){
        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island == null){
            return "&cVocê não possui uma ilha ainda!";
        }

        return ""+ island.getRadius();
    }

    private static String totalOfIslands(){
        return "" + BetterSkyBlock.getInstance().getDataStore().getTotalOfIslands();
    }

}
