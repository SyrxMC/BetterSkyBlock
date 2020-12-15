package br.com.syrxcraft.betterskyblock.events;

import br.com.syrxcraft.betterskyblock.islands.Island;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IslandEnterEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Island island;
    private final Player player;

    public IslandEnterEvent(Island island, Player player) {
        this.island = island;
        this.player = player;
    }

    public Island getIsland() {
        return island;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}