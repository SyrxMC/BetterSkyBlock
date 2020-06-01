package br.com.syrxcraft.betterskyblock.integration.integrations;

import br.com.syrxcraft.betterskyblock.integration.IIntegration;
import com.griefdefender.api.permission.flag.Flags;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;

//TODO: Rewrite
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

        if(player != null){
            switch (placeholder.toLowerCase()){

                case "islands_total":{
                    return totalOfIslands();
                }

                case "island_is_public_bol":{
                    return "" + islandIsPublicBol(player);
                }

                case "island_is_public":{
                    return islandIsPublic(player);
                }

                case "island_radius": {
                    return islandRadius(player);
                }

                default: return "";
            }
        }

        return "";
    }

    private String islandIsPublic(Player player){
        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island == null){
            return "&cVocê não possui uma ilha ainda!";
        }

        return islandIsPublicBol(player) ? "Sim" : "Não";
    }

    private boolean islandIsPublicBol(Player player){

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island != null){
            return island.getClaim().getFlagPermissionValue(Flags.ENTER_CLAIM, new HashSet<>()).asBoolean();
        }

        return false;
    }

    private String islandRadius(Player player){

        Island island = BetterSkyBlock.getInstance().getDataStore().getIsland(player.getUniqueId());

        if (island == null){
            return "&cVocê não possui uma ilha ainda!";
        }

        return ""+ island.getRadius();
    }

    private String totalOfIslands(){
        return "" + BetterSkyBlock.getInstance().getDataStore().getTotalOfIslands();
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
