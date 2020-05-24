package br.com.syrxcraft.betterskyblock.listeners;

import br.com.syrxcraft.betterskyblock.listeners.events.ClaimEvents;
import br.com.syrxcraft.betterskyblock.listeners.events.PlayerEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class EventManager {

    private Plugin plugin;

    public EventManager(Plugin plugin){
        this.plugin = plugin;

        registerEvents();
    }

    void registerEvents(){
        Bukkit.getPluginManager().registerEvents(new ClaimEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), plugin);
    }
}
