package br.com.syrxcraft.betterskyblock.config;

import org.bukkit.entity.Player;

public class Lang {

    public static void send(Player player, String message){
        player.sendMessage((message).replace("&","\u00a7"));
    }

    public static void send(Player player, String message, Object ... args){
        send(player, String.format(message, args));
    }

}
