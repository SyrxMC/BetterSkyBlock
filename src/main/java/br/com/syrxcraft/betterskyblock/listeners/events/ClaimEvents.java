package br.com.syrxcraft.betterskyblock.listeners.events;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.core.data.DataStore;
import br.com.syrxcraft.betterskyblock.core.events.IslandEnterEvent;
import br.com.syrxcraft.betterskyblock.core.events.IslandExitEvent;
import br.com.syrxcraft.betterskyblock.core.islands.Island;
import br.com.syrxcraft.betterskyblock.utils.GriefDefenderUtils;
import br.com.syrxcraft.betterskyblock.utils.IslandUtils;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.griefdefender.api.User;
import com.griefdefender.api.event.*;
import com.griefdefender.event.GDTransferClaimEvent;
import net.kyori.event.method.annotation.IgnoreCancelled;
import net.kyori.event.method.annotation.PostOrder;
import net.kyori.event.method.annotation.Subscribe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static br.com.syrxcraft.betterskyblock.utils.IslandUtils.getIsland;

public class ClaimEvents implements Listener {

    @Subscribe
    @PostOrder(-100)
    @IgnoreCancelled
    public void onClaimRemove(RemoveClaimEvent event) {

        Island island = getIsland(event.getClaim());
        Player player = GriefDefenderUtils.getPlayerFromEvent(event);

        if (island != null) {

            if (event instanceof RemoveClaimEvent.Abandon) {

                if(player != null){
                    player.sendMessage(ChatColor.RED + "1 Se você quer deletar a sua ilha use o comando \"/is delete\"!"); //TODO: LANG
                }

                event.cancelled(true);
                return;
            }

            island.teleportEveryoneToSpawn();

            if(BetterSkyBlock.getInstance().config().deleteRegion()){
                island.deleteRegionFile();
            }

            try {
                DataStore.getInstance().removeIsland(island);
            } catch (Exception exception) {
                exception.printStackTrace();
            }


            BetterSkyBlock.getInstance().getLoggerHelper().info("Removed " + island.getOwnerName() + "'s island because the claim was deleted. Reason: " + event.getMessage() + ".");
        }
    }

    @Subscribe
    @PostOrder(-100)
    @IgnoreCancelled
    public void onClaimCreate(CreateClaimEvent.Pre event) {

        Player player = GriefDefenderUtils.getPlayerFromEvent(event);

        if (player == null)
            return;

        if (event.getClaim().isAdminClaim() || event.getClaim().isTown())
            return;

        if (!event.getClaim().getWorldUniqueId().equals(BetterSkyBlock.getInstance().getIslandWorld().getUID())) {
            return;
        }

        event.cancelled(true);
        player.sendMessage("You do not have permissions to create claims on the islands world. Create a admin claim instead a basic claim."); //TODO: Implement a lang system
    }


    @Subscribe
    @PostOrder(-100)
    @IgnoreCancelled
    public void onClaimChange(ChangeClaimEvent event) {

        Player player = GriefDefenderUtils.getPlayerFromEvent(event);

        if (player == null) {
            return;
        }

        if (IslandUtils.isIsland(event.getClaim())) {

            event.cancelled(true);

            if (event instanceof ChangeClaimEvent.Resize) {
                player.sendMessage(ChatColor.RED + "Você não pode redefinir o tamanho dessa ilha!"); //TODO: Lang
            }

            if (event instanceof ChangeClaimEvent.Type) {
                player.sendMessage(ChatColor.RED + "Você não pode alterar o tipo dessa ilha!"); //TODO: Lang
            }
        }

    }

    @Subscribe()
    @PostOrder(-100)
    @IgnoreCancelled
    public void onClaimTransfer(GDTransferClaimEvent event) {

        Player player = GriefDefenderUtils.getPlayerFromEvent(event);

        if (IslandUtils.isIsland(event.getClaim())) {

            event.cancelled(true);

            if (player != null){
                player.sendMessage("§c ! §fEsse claim é uma ilha! E as ilhas são Intransferíveis."); // TODO: Lang
            }
        }
    }


    @Subscribe()
    @PostOrder(-100)
    @IgnoreCancelled
    public void onBorderClaim(BorderClaimEvent event){
        User user;
        Player player;

        if((user = event.getUser().orElse(null)) != null){
            if((player = Utils.asBukkitPlayer(user)) != null){

                if(event.getExitClaim() != null){

                    Island island = IslandUtils.getIsland(event.getExitClaim());

                    if(island != null){

                        IslandExitEvent islandExitEvent = new IslandExitEvent(island, player, event.getEnterClaim().isWilderness());
                        Bukkit.getPluginManager().callEvent(islandExitEvent);

                        if(islandExitEvent.isCancelled()){
                            event.cancelled(true);
                            return;
                        }
                    }
                }

                if(event.getEnterClaim() != null){

                    Island island = IslandUtils.getIsland(event.getEnterClaim());

                    if(island != null){

                        IslandEnterEvent islandEnterEvent = new IslandEnterEvent(island, player);
                        Bukkit.getPluginManager().callEvent(islandEnterEvent);

                        if(islandEnterEvent.isCancelled()){
                            event.cancelled(true);
                        }
                    }
                }
            }
        }
    }
}
