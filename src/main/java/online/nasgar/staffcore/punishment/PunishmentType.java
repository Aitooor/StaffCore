package online.nasgar.staffcore.punishment;


import lombok.Getter;

@Getter
public enum PunishmentType {

    BLACKLIST("blacklisted", "&bYour account has been blacklisted from the CadiaMC Network.\n\n&bReason: &f{reason}\n\n&bThis type of punishment cannot be appealed.", "unblacklisted"),
    BAN("banned", "&bYour account has been suspended from the CadiaMC Network.\n\n&bReason: &f{reason}\n\n&bAppeal at &fts.saifed.net\n&bBuy unban at &fdonate.saifed.net", "unbanned"),
    TEMPBAN("temporarily banned", "&bYour account has been temporarily suspended from the CadiaMC Network.\n\nReason: &f{reason}\n&bExpires in &f{expires}.", "unbanned"),
    KICK("kicked", "&bYour account has been kicked.\nReason: &f{reason}", (String) null),
    MUTE("muted", "&bYou are currently muted &f{duration} &bdue the reason: &f{reason}.", "unmuted"),
    WARN("warned", "&bYou have been warned by &f{sender} &bfor &f{reason}&b.", (String) null);

    private String context;
    private String message;
    private String undoContext;

    private PunishmentType(String context, String message, String undoContext) {
        this.context = context;
        this.message = message;
        this.undoContext = undoContext;
    }
}
