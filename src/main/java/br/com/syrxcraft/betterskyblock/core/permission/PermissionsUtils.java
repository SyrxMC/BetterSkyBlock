package br.com.syrxcraft.betterskyblock.core.permission;

public class PermissionsUtils {

    public static String getPrettyType(PermissionType permissionType){
        switch (permissionType){
            case OWNER:         return          "§cDono§r";
            case ADMINISTRATOR: return          "§dAdministrador§r";
            case MEMBER:        return          "§aMembro§r";
            case ENTRY:         return          "§7Visitante§r";
        }

        return "§7§lNenhum§r";
    }

    public static String getFormatedType(PermissionType permissionType){

        switch (permissionType){
            case OWNER:         return          "Dono";
            case ADMINISTRATOR: return          "Administrador";
            case MEMBER:        return          "Membro";
            case ENTRY:         return          "Visitante";
        }

        return "Nenhum";
    }


    public static boolean canEnter(PermissionType permissionType){
        return permissionType != PermissionType.NONE;
    }

}
