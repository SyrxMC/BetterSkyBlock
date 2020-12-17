package br.com.syrxcraft.betterskyblock.listeners;

import br.com.syrxcraft.betterskyblock.listeners.events.CommandHunterEvents;
import br.com.syrxcraft.betterskyblock.listeners.events.ClaimEvents;
import br.com.syrxcraft.betterskyblock.listeners.events.PlayerEvents;
import com.griefdefender.api.GriefDefender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class EventManager {

    private final Plugin plugin;

    public EventManager(Plugin plugin){
        this.plugin = plugin;

        registerEvents();

    }

    void registerEvents(){
        GriefDefender.getEventManager().register(new ClaimEvents());
       //GriefDefender.getEventManager().register(new IslandBorderHandler());
        Bukkit.getPluginManager().registerEvents(new CommandHunterEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), plugin);
    }
}
