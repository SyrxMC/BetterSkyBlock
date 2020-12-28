package br.com.syrxcraft.betterskyblock.core.permission;

public class PermissionsUtils {

    public static String getPrettyType(PermissionType permissionType){
        switch (permissionType){
            case OWNER: return "§c§lDono§r";
            case ADMINISTRATOR: return "§c§lAdministrador§r";
            case MEMBER: return "§2§lMembro§r";
            case ENTRY: return "§a§lVisitante§r";
        }

        return "§7§lNenhum§r";
    }

}
