package br.com.syrxcraft.betterskyblock.core.permission;

import br.com.syrxcraft.betterskyblock.core.islands.Island;
import org.bukkit.entity.Player;

public class PermissionManager {

    private static PermissionManager INSTANCE;

    public PermissionManager(){
        INSTANCE = this;
    }

    public static PermissionManager getINSTANCE() {
        return INSTANCE;
    }

    public PermissionType getCurrentPermission(Player player, Island island){
        return PermissionType.NONE;
    }


}
