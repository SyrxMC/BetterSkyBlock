package br.com.syrxcraft.betterskyblock.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Cooldown {

    private static class CooldownInstance{

        private final UUID uuid;
        private final Long time;
        private final String key;

        private CooldownInstance(UUID uuid, Long time, String key) {
            this.uuid = uuid;
            this.time = time;
            this.key = key;
        }

        public Long getTime() {
            return time;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getKey() {
            return key;
        }
    }

    private static final HashSet<CooldownInstance> cooldownInstanceSet = new HashSet<>();

    public static CooldownInstance getCooldown(Player player, String key){
        return cooldownInstanceSet.stream()
                .filter(cooldownInstance -> player.getUniqueId().equals(cooldownInstance.getUuid()) && cooldownInstance.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public static void removeCooldown(Player player, String key){
        cooldownInstanceSet.removeIf(cooldownInstance -> player.getUniqueId().equals(cooldownInstance.getUuid()) && cooldownInstance.getKey().equals(key));
    }

    public static boolean isInCooldown(Player player, String key){

        CooldownInstance cooldownInstance;

        if((cooldownInstance = getCooldown(player, key)) != null){

            if(cooldownInstance.getTime() > System.currentTimeMillis()){
                return true;
            }else {
                removeCooldown(player, key);
                return false;
            }

        }

        return false;
    }

    public static void setCooldown(Player player, Long time, String key){
        cooldownInstanceSet.add(new CooldownInstance(player.getUniqueId(), System.currentTimeMillis() + (time * 1000), key));
    }

    public static long getCooldownTime(Player player, String key){

        CooldownInstance cooldownInstance;

        if((cooldownInstance = getCooldown(player, key)) != null){

            long time = cooldownInstance.getTime() - System.currentTimeMillis();

            if(time < 0){
                removeCooldown(player, key);
                return 0;
            }

            return time;

        }

        return 0;
    }

    public static long getCooldownTimeSec(Player player, String key){
        return getCooldownTime(player, key) / 1000;
    }

}