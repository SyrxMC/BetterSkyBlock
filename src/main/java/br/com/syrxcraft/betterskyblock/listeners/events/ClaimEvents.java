package br.com.syrxcraft.betterskyblock.listeners.events;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import br.com.syrxcraft.betterskyblock.islands.Island;
import br.com.syrxcraft.betterskyblock.islands.IslandUtils;
import br.com.syrxcraft.betterskyblock.utils.Utils;
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
import org.bukkit.event.Listener;

import java.sql.SQLException;

import static br.com.syrxcraft.betterskyblock.islands.IslandUtils.getIsland;

public class ClaimEvents implements Listener {

    @Subscribe
    @PostOrder(-100)
    @IgnoreCancelled
    public void onClaimRemove(RemoveClaimEvent event) {

        Island island = getIsland(event.getClaim());

        Player player = event.getCause().first(Player.class).orElse(null);

        if (island != null) {

            if (event instanceof RemoveClaimEvent.Abandon) {

                System.out.println(player != null);

                if(player != null){
                    player.sendMessage(ChatColor.RED + "Se você quer deletar a sua ilha use o comando \"/is delete\"!");
                }else if(island.getPlayer() != null){
                    island.getPlayer().sendMessage(ChatColor.RED + "Se você quer deletar a sua ilha use o comando \"/is delete\"!");
                }

                event.cancelled(true);

                return;
            }

            island.teleportEveryoneToSpawn();

            if (BetterSkyBlock.getInstance().config().deleteRegion()) {
                island.deleteRegionFile();
            }

            try {
                BetterSkyBlock.getInstance().getDataStore().removeIsland(island);

            } catch (SQLException e) {
                e.printStackTrace();
            }

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
}
