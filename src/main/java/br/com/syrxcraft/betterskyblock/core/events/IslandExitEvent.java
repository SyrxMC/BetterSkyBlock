package br.com.syrxcraft.betterskyblock.core.events;

import br.com.syrxcraft.betterskyblock.core.islands.Island;
import com.griefdefender.api.claim.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IslandExitEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Island island;
    private final Player player;
    private final boolean isOnWilderness;
    private final Claim newClaim;

    private boolean isCancelled = false;

    public IslandExitEvent(Island island, Player player, boolean isOnWilderness, Claim newClaim) {
        this.island = island;
        this.player = player;
        this.isOnWilderness = isOnWilderness;
        this.newClaim = newClaim;
    }

    public Island getIsland() {
        return island;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isOnWilderness() {
        return isOnWilderness;
    }

    public Claim getNewClaim() {
        return newClaim;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}