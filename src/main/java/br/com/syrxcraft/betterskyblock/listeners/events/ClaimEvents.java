package br.com.syrxcraft.betterskyblock.listeners.events;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.islands.IslandUtils;
import com.griefdefender.api.event.ClaimEvent;
import com.griefdefender.api.event.RemoveClaimEvent;
import com.griefdefender.api.event.TransferClaimEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import java.sql.SQLException;

import static br.com.syrxcraft.betterskyblock.islands.IslandUtils.getIsland;

public class ClaimEvents implements Listener {
//
//    @EventHandler(ignoreCancelled=true, priority = EventPriority.MONITOR)
//    void onClaimDeleteMonitor(RemoveClaimEvent.Abandon event) {
//        Island island = getIsland(event.getClaim());
//
//        if (island != null) {
//
//            if (event.getDeleteReason()!=Reason.EXPIRED && event.getDeleteReason()!=Reason.DELETE) {
//                event.setCancelled(true);
//                event.getPlayer().sendMessage(ChatColor.RED + "Se você quer deletar a sua ilha use o comando \"/is delete\"!");
//                return;
//            }
//            island.teleportEveryoneToSpawn();
//            if (instance.config().deleteRegion) {
//                island.deleteRegionFile();
//            }
//            try {
//                instance.getDataStore().removeIsland(island);
//                PlayerData playerData = GriefPreventionPlus.getInstance().getDataStore().getPlayerData(event.getClaim().getOwnerID());
//                playerData.setBonusClaimBlocks(playerData.getBonusClaimBlocks()-(((instance.config().radius*2)+1)*2));
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            instance.getLogger().info("Removed "+island.getOwnerName()+"'s island because the claim was deleted. Reason: "+event.getDeleteReason()+".");
//        }
//    }

//    private void deleteLogic(Event event){
//
//        ClaimExpirationEvent claimExpirationEvent;
//        ClaimDeletedEvent claimDeletedEvent;
//        Claim claim = null;
//
//        if(event instanceof ClaimExpirationEvent){
//
//            claimExpirationEvent = (ClaimExpirationEvent) event;
//            claim = claimExpirationEvent.getClaim();
//
//        }else if(event instanceof ClaimDeletedEvent){
//
//            claimDeletedEvent = (ClaimDeletedEvent) event;
//            claim = claimDeletedEvent.getClaim();
//
//        }
//
//
//        if(claim == null){
//            return;
//        }
//
//        Island island = getIsland(claim);
//
//        if (island != null) {
//
//            island.teleportEveryoneToSpawn();
//
//            if (BetterSkyBlock.getInstance().config().deleteRegion) {
//                island.deleteRegionFile();
//            }
//
//            try {
//
//                BetterSkyBlock.getInstance().getDataStore().removeIsland(island);
//                PlayerData playerData = BetterSkyBlock.getInstance().getGriefPrevention().dataStore.getPlayerData(claim.ownerID);
//
//                playerData.setBonusClaimBlocks( playerData.getBonusClaimBlocks() - ((( BetterSkyBlock.getInstance().config().radius * 2 ) + 1 ) * 2 ));
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//
//            BetterSkyBlock.getInstance().getLoggerHelper().info("Removed " + island.getOwnerName() + "'s island because the claim was deleted.");
//        }
//    }
//
//    @EventHandler(ignoreCancelled=true)
//    void onClaimCreate(ClaimCreatedEvent event) {
//
//        if (event.getCreator() == null) {
//            //Maybe it is a admin claim ?
//            return;
//        }
//
//        Player player = (Player) event.getCreator();
//
//        if (player != null && player.isOp()) {
//            return;
//        }
//
//        if (event.getClaim().parent != null) {
//            return;
//        }
//
//        if (!event.getClaim().getLesserBoundaryCorner().getWorld().getName().equals(BetterSkyBlock.getInstance().config().worldName)) {
//
//            if (player != null && player.hasPermission("gppskyblock.claimoutsidemainworld")){
//                event.setCancelled(true);
//            }
//
//            return;
//        }
//
//        event.setCancelled(true);
//        //TODO: Implement a lang system
//        event.getCreator().sendMessage("You do not have permissions to create claims on the islands world.");
//    }
//
//    @EventHandler(ignoreCancelled=true)
//    void onClaimResize(ClaimCreatedEvent event) {
//        if (event.getCreator() != null && IslandUtils.isIsland(event.getClaim())) {
//
//            Island island = getIsland(event.getClaim());
//
//            if(island != null && island.getClaim().modifiedDate != event.getClaim().modifiedDate){
//                event.setCancelled(true);
//                if (event.getCreator() != null) {
//                    event.getCreator().sendMessage(ChatColor.RED+"Você não pode redefinir o tamanho dessa ilha. É uma ilha afinal das contas.It's an island!");
//                }
//            }
//        }
//    }
//

//    @EventHandler(ignoreCancelled=true)
//    void onClaimOwnerTransfer(ClaimOwnerTransfer event) {
//        Island island = getIsland(event.getClaim());
//        if (island != null) {
//            Island is2 = instance.getDataStore().getIsland(event.getNewOwnerUUID());
//            if (is2 != null) {
//                event.setCancelled(true);
//                event.setReason("This claim is an island and the other player has an island already. The other player has to delete their island first.");
//            }
//        }
//    }
//
//    //@EventHandler(ignoreCancelled=true, priority = EventPriority.MONITOR)
//    void onClaimOwnerTransferMonitor(TransferClaimEvent event) {
//
//        Island island = getIsland(event.getClaim());
//
//        if (island != null) {
//
//            Island newIsland = new Island(event.getNewOwner(), event.getClaim(), island.getSpawn());
//
//            try {
//                instance.getDataStore().removeIsland(island);
//                this.instance.getDataStore().addIsland(newIsland);
//            } catch (SQLException e) {
//                instance.getLogger().severe("SQL exception while transferring island " + event.getClaim().getID() + "owner from " + event.getClaim().getOwnerName() + " to " + event.getNewOwnerUUID());
//                if (event.getPlayer() != null) {
//                    event.getPlayer().sendMessage(ChatColor.RED + "WARNING! A severe error occurred while transferring the island! Contact your server administrator!");
//                }
//                e.printStackTrace();
//            }
//
//        }
//    }
}
