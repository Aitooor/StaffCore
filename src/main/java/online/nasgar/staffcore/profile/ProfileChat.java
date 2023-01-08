package online.nasgar.staffcore.profile;

public enum ProfileChat {

    NORMAL(""),
    STAFF_CHAT("staffchat"),
    ADMIN_CHAT("adminchat"),
    DONOR_CHAT("donator");

    private String permission;

    private ProfileChat(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
