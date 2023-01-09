package online.nasgar.staffcore.profile;

public enum ProfileChat {

    NORMAL(""),
    STAFF_CHAT("staffchat"),
    MOD_CHAT("modchat"),
    ADMIN_CHAT("adminchat");

    private String permission;

    private ProfileChat(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}