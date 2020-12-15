package br.com.syrxcraft.betterskyblock.listeners.events;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.PermissionNodes;
import br.com.syrxcraft.betterskyblock.events.IslandEnterEvent;
import br.com.syrxcraft.betterskyblock.events.IslandExitEvent;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.islands.IslandUtils;
import br.com.syrxcraft.betterskyblock.utils.Utils;
import com.griefdefender.api.User;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.event.*;
import com.griefdefender.event.GDTransferClaimEvent;
import net.kyori.event.method.annotation.IgnoreCancelled;
import net.kyori.event.method.annotation.PostOrder;
import net.kyori.event.method.annotation.Subscribe;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;

import static br.com.syrxcraft.betterskyblock.islands.IslandUtils.getIsland;
import static br.com.syrxcraft.betterskyblock.islands.IslandUtils.isIslandWorld;

public class ClaimEvents implements Listener {

    @Subscribe
    @PostOrder(-100)
    @IgnoreCancelled
    public void onClaimRemove(RemoveClaimEvent event) {

        Island island = getIsland(event.getClaim());

        Player player = event.getCause().first(Player.class).orElse(null);

        if (island != null) {

            if (event instanceof RemoveClaimEvent.Abandon) {

                if(player != null){
                    player.sendMessage(ChatColor.RED + "Se você quer deletar a sua ilha use o comando \"/is delete\"!");
                }else if(island.getPlayer() != null){
                    island.getPlayer().sendMessage(ChatColor.RED + "Se você quer deletar a sua ilha use o comando \"/is delete\"!");
                }

                event.cancelled(true);

                return;
            }

            island.teleportEveryoneToSpawn();
            BetterSkyBlock.getInstance().getLoggerHelper().info("Removed " + island.getOwnerName() + "'s island because the claim was deleted. Reason: " + event.getMessage() + ".");
        }
    }

    @Subscribe
    @PostOrder(-100)
    @IgnoreCancelled
    public void onClaimCreate(CreateClaimEvent event) {

        if (!event.getCause().containsType(Player.class)) {
            return;
        }

        if (event.getClaim().isAdminClaim()) {
            return;
        }

        if (!event.getClaim().getWorldUniqueId().equals(BetterSkyBlock.getInstance().getIslandWorld().getUID())) {
            return;
        }

        event.cancelled(true);
        //TODO: Implement a lang system
        event.setMessage(TextComponent.of("You do not have permissions to create claims on the islands world."));
    }


    @Subscribe
    @PostOrder(-100)
    @IgnoreCancelled
    public void onClaimChange(ChangeClaimEvent event) {

        Player player = event.getCause().first(Player.class).orElse(null);

        if (player == null || event.cancelled()) {
            return;
        }

        if (IslandUtils.isIsland(event.getClaim())) {
            if (getIsland(event.getClaim()) != null) {
                event.cancelled(true);

                if (event instanceof ChangeClaimEvent.Resize) {
                    player.sendMessage(ChatColor.RED + "Você não pode redefinir o tamanho dessa ilha. É uma ilha afinal das contas.It's an island!");
                }

            }
        }
    }

    @Subscribe()
    @PostOrder(-100)
    @IgnoreCancelled
    public void onClaimTransfer(GDTransferClaimEvent event) {

        Island island = getIsland(event.getClaim());

        if (island != null) {


            Island is2 = BetterSkyBlock.getInstance().getIsland(event.getNewOwner());

            if(is2 != null){
                event.cancelled(true);
                event.setMessage(TextComponent.of("This claim is an island and the other player has an island already. The other player has to delete their island first."));
            }


            Island newIsland = new Island(event.getNewOwner(), event.getClaim(), island.getSpawn());

            try {

                BetterSkyBlock.getInstance().getDataStore().removeIsland(island);
                BetterSkyBlock.getInstance().getDataStore().addIsland(newIsland);

                BetterSkyBlock.getInstance().getLoggerHelper().info("Transferred island " + event.getClaim().getUniqueId() + " owner from " + PlainComponentSerializer.INSTANCE.serialize(event.getClaim().getOwnerDisplayName()) + " to " + event.getNewOwner());


            } catch (SQLException e) {

                BetterSkyBlock.getInstance().getLoggerHelper().error("SQL exception while transferring island " + event.getClaim().getUniqueId() + "owner from " + PlainComponentSerializer.INSTANCE.serialize(event.getClaim().getOwnerDisplayName()) + " to " + event.getNewOwner());

                if (Utils.asBukkitPlayer(event.getNewOwner())!= null) {
                    Utils.asBukkitPlayer(event.getNewOwner()).sendMessage(ChatColor.RED + "WARNING! A severe error occurred while transferring the island! Contact your server administrator!");
                }

                e.printStackTrace();

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
            if((player = Bukkit.getPlayer(user.getUniqueId())) != null){

                if(event.getExitClaim() != null){

                    Island island = IslandUtils.getIsland(event.getExitClaim());

                    if(island != null){

                        IslandExitEvent islandExitEvent = new IslandExitEvent(island, player);
                        Bukkit.getPluginManager().callEvent(islandExitEvent);

                    }
                }

                if(event.getEnterClaim() != null){

                    Island island = IslandUtils.getIsland(event.getEnterClaim());

                    if(island != null){

                        IslandEnterEvent islandEnterEvent = new IslandEnterEvent(island, player);
                        Bukkit.getPluginManager().callEvent(islandEnterEvent);

                    }
                }
            }
        }
    }

//    @Subscribe()
//    @PostOrder(-100)
//    @IgnoreCancelled
    public void onClaimExit(BorderClaimEvent event) {


        if(!event.getUser().isPresent()){
            return;
        }

        Player player = Utils.asBukkitPlayer(event.getUser().get().getUniqueId());

        if(player == null) return;

        Island island;

        if((island = IslandUtils.getIsland(event.getExitClaim())) != null){

            if (player.hasPermission(PermissionNodes.OPTIONS_OVERRIDE) || player.hasPermission(PermissionNodes.OPTIONS_LEAVE_ISLAND)) {
                return;
            }

            if(!player.getLocation().getWorld().getUID().equals(BetterSkyBlock.getInstance().getIslandWorld().getUID())){
                return;
            }

            player.sendMessage(ChatColor.RED + "Você não pode voar para fora de uma ilha.");
            player.teleport(island.getSpawn());
        }
    }


}
