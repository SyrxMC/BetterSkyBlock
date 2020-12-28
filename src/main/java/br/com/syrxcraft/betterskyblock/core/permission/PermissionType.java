package br.com.syrxcraft.betterskyblock.core.permission;

public enum PermissionType {

    OWNER(4),
    ADMINISTRATOR(3),
    MEMBER(2),
    ENTRY(1),
    NONE(-1);

    private final int permission;


    PermissionType(int permission) {
        this.permission = permission;
    }

    public static PermissionType getPermissionType(int value) {

        for (PermissionType permissionType : values()) {
            if (permissionType.permission == value) return permissionType;
        }

        return NONE;
    }

    public int intPermission() {
        return permission;
    }

    public String toPrettyType() {
        return PermissionsUtils.getPrettyType(this);
    }

    public String toFormattedType() {
        return PermissionsUtils.getFormatedType(this);
    }
}
