package br.com.syrxcraft.betterskyblock.utils;

import com.griefdefender.api.Tristate;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.permission.GDPermissionManager;

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
                return (Tristate) getClaimFlagPermissionMethod.invoke(claim, flag);
            } catch (IllegalAccessException | InvocationTargetException ignored) { }
        }

        return null;
    }

}
