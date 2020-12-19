package br.com.syrxcraft.betterskyblock.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Cooldown {

    private static class CooldownInstance{

        private final UUID uuid;
        private final Long time;

        private CooldownInstance(UUID uuid, Long time) {
            this.uuid = uuid;
            this.time = time;
        }

        public Long getTime() {
            return time;
        }

        public UUID getUuid() {
            return uuid;
        }

    }

    private static final HashMap<UUID, CooldownInstance> cooldownInstanceMap = new HashMap<>();

    public static CooldownInstance getCooldown(Player player){
        return cooldownInstanceMap.get(player.getUniqueId());
    }

    public static void removeCooldown(Player player){
        cooldownInstanceMap.remove(player.getUniqueId());
    }

    public static boolean isInCooldown(Player player){

        CooldownInstance cooldownInstance;

        if((cooldownInstance = getCooldown(player)) != null){

            if(cooldownInstance.getTime() > System.currentTimeMillis()){
                return true;
            }else {
                removeCooldown(player);
                return false;
            }

        }

        return false;
    }

    public static void setCooldown(Player player, Long time){
        cooldownInstanceMap.put(player.getUniqueId(), new CooldownInstance(player.getUniqueId(), System.currentTimeMillis() + (time * 1000)));
    }

    public static long getCooldownTime(Player player){

        CooldownInstance cooldownInstance;

        if((cooldownInstance = getCooldown(player)) != null){

            long time = cooldownInstance.getTime() - System.currentTimeMillis();

            if(time < 0){
                removeCooldown(player);
                return 0;
            }

            return time;

        }

        return 0;
    }

    public static long getCooldownTimeSec(Player player){
        return getCooldownTime(player) / 1000;
    }

}