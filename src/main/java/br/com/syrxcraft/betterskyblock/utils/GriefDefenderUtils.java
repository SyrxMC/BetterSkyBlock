package br.com.syrxcraft.betterskyblock.utils;

import com.griefdefender.api.Tristate;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustType;
import com.griefdefender.api.claim.TrustTypes;
import com.griefdefender.api.event.Event;
import com.griefdefender.api.permission.flag.Flag;
import com.griefdefender.api.permission.flag.Flags;
import com.griefdefender.permission.GDPermissionManager;
import com.griefdefender.permission.GDPermissionUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GriefDefenderUtils {

    private static Method getClaimFlagPermissionMethod;

    static {
        try {
            getClaimFlagPermissionMethod = GDPermissionManager.class.getDeclaredMethod("getClaimFlagPermission", Claim.class, String.class);
        } catch (NoSuchMethodException e) {
            getClaimFlagPermissionMethod = null;
        }
    }

    public static Tristate getClaimFlagPermission(Claim claim, String flag){

        if(getClaimFlagPermissionMethod != null){
            try {
                getClaimFlagPermissionMethod.setAccessible(true);
                return (Tristate) getClaimFlagPermissionMethod.invoke(GDPermissionManager.getInstance(), claim, flag);
            } catch (IllegalAccessException | InvocationTargetException ignored) { ignored.printStackTrace(); }
        }

        return null;
    }

    public static Player getPlayerFromEvent(Event event){

        GDPermissionUser gdPermissionUser = event.getCause().first(GDPermissionUser.class).orElse(null);

        if(gdPermissionUser != null){
            return gdPermissionUser.getOnlinePlayer();
        }

        return null;
    }

    public static boolean getPlayerFlagPermission(Player player, Claim claim, Flag flag, TrustType trustType){

        Tristate tristate = GDPermissionManager.getInstance().getFinalPermission(null, player.getLocation(), claim, flag, null, player, player, trustType, true);

        if(tristate != null){
            return tristate.asBoolean();
        }

        return false;
    }
    
    public static TrustType getPlayerTrustType(Player player, Claim claim){
        
        if(player != null && claim != null){

            if(claim.getUserTrusts(TrustTypes.MANAGER).contains(player.getUniqueId()))
                return TrustTypes.MANAGER;
            if(claim.getUserTrusts(TrustTypes.CONTAINER).contains(player.getUniqueId()))
                return TrustTypes.CONTAINER;
            if(claim.getUserTrusts(TrustTypes.BUILDER).contains(player.getUniqueId()))
                return TrustTypes.BUILDER;
            if(claim.getUserTrusts(TrustTypes.ACCESSOR).contains(player.getUniqueId()))
                return TrustTypes.ACCESSOR;

        }
        
        return TrustTypes.NONE;
    }

}
