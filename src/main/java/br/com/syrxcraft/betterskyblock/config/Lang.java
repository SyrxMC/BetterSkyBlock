package br.com.syrxcraft.betterskyblock.config;

import org.bukkit.entity.Player;

public class Lang {

    public static String BASE                           = "&8[&3&lRTP&r&8]&r ";
    public static String NO_PERMISSION_MESSAGE          = "&cVocê não possui permissão para executar este comando.";
    public static String IN_COOLDOWN_MESSAGE            = "&cVocê precisa esperar &c%s&r &cpara executar este comando.";
    public static String WARM_UP_MESSAGE                = "&eVocê será teleportado em alguns segundos.";
    public static String TELEPORT_MESSAGE               = "&aVocê foi teleportado para &8[ &bX: &3&o%d&r &bY: &3&o%d&r &bZ: &3&o%d&r &8]";
    public static String INVALID_WORLD_MESSAGE          = "&cO mundo &4&n%s&r &cnão existe.";
    public static String DISABLED_WORLD_MESSAGE         = "&cVocê não pode se teleportar aleatóriamente por esse mundo.";
    public static String NO_PERMISSION_WORLD_MESSAGE    = "&cVocê não tem permissão para se teleportar aleatóriamente por esse mundo.";
    public static String INVALID_PLAYER_MESSAGE         = "&cJogador não disponível no momento.";
    public static String PUSH_TO_SAFETY_MESSAGE         = "&aVocê estava prestes a se machucar, teleportei de volta para &8[ &bX: &3&o%d&r &bY: &3&o%d&r &bZ: &3&o%d&r &8]";

    public Lang(){

    }

    public static void send(Player player, String message){
        player.sendMessage((BASE + message).replace("&","\u00a7"));
    }

    public static void send(Player player, String message, Object ... args){
        send(player, String.format(message, args));
    }

}
