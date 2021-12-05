package me.fckml.staffcore.punishment;


import java.util.*;

import org.apache.commons.lang.time.*;

public class Punishment {

    private UUID uuid;
    private UUID targetID;
    private UUID addedBy;
    private UUID removedBy;

    private String staffName;
    private String victimName;
    private String addedReason;
    private String removedReason;

    private PunishmentType type;

    private long addedAt;
    private long removedAt;
    private long expiration;

    private boolean silent;

    public boolean isBan() {
        return this.type == PunishmentType.TEMPBAN || this.type == PunishmentType.BAN || this.type == PunishmentType.BLACKLIST;
    }

    public boolean isMute() {
        return this.type == PunishmentType.MUTE;
    }

    public boolean isWarn() {
        return this.type == PunishmentType.WARN;
    }

    public boolean isRemoved() {
        return this.removedReason != null;
    }

    public boolean isPermanent() {
        return this.expiration <= 0L;
    }

    public boolean isActive() {
        return !this.isRemoved() && (this.isPermanent() || this.addedAt + this.expiration > System.currentTimeMillis());
    }

    public String getRemaining() {
        String toReturn;
        if (this.isRemoved()) {
            toReturn = "Removed";
        } else if (this.isPermanent()) {
            toReturn = "Permanent";
        } else if (!this.isActive()) {
            toReturn = "Expired";
        } else {
            toReturn = DurationFormatUtils.formatDurationWords(this.addedAt + this.expiration - System.currentTimeMillis(), true, true);
        }
        return toReturn;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public UUID getTargetID() {
        return this.targetID;
    }

    public UUID getAddedBy() {
        return this.addedBy;
    }

    public String getStaffName() {
        return this.staffName;
    }

    public String getVictimName() {
        return this.victimName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public void setVictimName(String victimName) {
        this.victimName = victimName;
    }

    public UUID getRemovedBy() {
        return this.removedBy;
    }

    public String getAddedReason() {
        return this.addedReason;
    }

    public String getRemovedReason() {
        return this.removedReason;
    }

    public PunishmentType getType() {
        return this.type;
    }

    public long getAddedAt() {
        return this.addedAt;
    }

    public long getRemovedAt() {
        return this.removedAt;
    }

    public long getExpiration() {
        return this.expiration;
    }

    public boolean isSilent() {
        return this.silent;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setTargetID(UUID targetID) {
        this.targetID = targetID;
    }

    public void setAddedBy(UUID addedBy) {
        this.addedBy = addedBy;
    }

    public void setRemovedBy(UUID removedBy) {
        this.removedBy = removedBy;
    }

    public void setAddedReason(String addedReason) {
        this.addedReason = addedReason;
    }

    public void setRemovedReason(String removedReason) {
        this.removedReason = removedReason;
    }

    public void setType(PunishmentType type) {
        this.type = type;
    }

    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }

    public void setRemovedAt(long removedAt) {
        this.removedAt = removedAt;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }
}