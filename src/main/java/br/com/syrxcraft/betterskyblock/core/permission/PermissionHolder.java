package br.com.syrxcraft.betterskyblock.core.permission;

import org.bukkit.entity.Player;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionHolder {

    public static PermissionHolder createInstance(){
        return new PermissionHolder();
    }

    private PermissionHolder(){}

    private final HashMap<UUID, PermissionType> cachedData = new HashMap<>();

    public PermissionType getEffectivePermission(UUID uuid){

        if(uuid != null)
            return cachedData.getOrDefault(uuid, PermissionType.NONE);

        return PermissionType.NONE;
    }

    public PermissionType getEffectivePermission(Player player){
        if(player != null)
            return getEffectivePermission(player.getUniqueId());

        return PermissionType.NONE;
    }

    public void updatePermission(UUID uuid, PermissionType newPermission){
        if(uuid != null)
            cachedData.put(uuid, (newPermission != null) ? newPermission : PermissionType.NONE);
    }

    public void updatePermission(Player player, PermissionType newPermission){
        if(player != null)
            updatePermission(player.getUniqueId(), newPermission);
    }

    public Map<UUID, PermissionType> getPermissions(){
        return getPermissions(true);
    }

    public Map<UUID, PermissionType> getPermissions(boolean ignoreNone){

        if(ignoreNone)
            return cachedData.entrySet().stream()
                .filter(uuidPermissionTypeEntry -> uuidPermissionTypeEntry.getValue() != PermissionType.NONE)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        return cachedData;
    }

    public boolean isEmpty(){
        return cachedData.isEmpty();
    }

}
