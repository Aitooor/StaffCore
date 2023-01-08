package online.nasgar.staffcore.profile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Data;
import lombok.Getter;
import online.nasgar.staffcore.mongo.CoreMongoDatabase;
import online.nasgar.staffcore.punishment.Punishment;
import online.nasgar.staffcore.punishment.PunishmentHelper;
import online.nasgar.staffcore.punishment.PunishmentType;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class Profile {

    @Getter
    private static Map<UUID, Profile> profileMap = Maps.newConcurrentMap();

    private UUID uuid;
    private String name, server, address;

    private long firstSeen, lastSeen;
    private List<String> identifiers;

    private ProfileChat chat;

    private List<Punishment> punishments;
    private List<UUID> alts, ignoreList;

    public Profile(UUID uuid) {
        this.chat = ProfileChat.NORMAL;

        this.identifiers = Lists.newArrayList();
        this.alts = Lists.newArrayList();
        this.punishments = Lists.newArrayList();
        this.ignoreList = Lists.newArrayList();

        this.uuid = uuid;
    }

    public void load() {
        Document document = CoreMongoDatabase.getInstance().getProfiles().find(Filters.eq("uuid", this.uuid.toString())).first();

        if (document == null) {
            this.save();
            return;
        }

        this.name = document.getString("name");
        this.server = document.getString("server");
        this.address = document.getString("address");

        this.firstSeen = document.getLong("firstSeen");
        this.lastSeen = document.getLong("lastSeen");

        this.identifiers = document.getList("identifiers", String.class);

        this.chat = ProfileChat.valueOf(document.getString("chatType"));

        try (MongoCursor<Document> mongoCursor = CoreMongoDatabase.getInstance().getPunishments().find(Filters.eq("targetID", this.uuid.toString())).iterator()) {
            mongoCursor.forEachRemaining(punishDocument -> {
                Punishment punishment = new Punishment();
                PunishmentHelper.load(punishDocument, punishment);

                this.punishments.add(punishment);
            });
        }
    }

    public void save() {
        Document document = new Document();

        document.put("uuid", this.uuid.toString());

        document.put("name", this.name);
        document.put("server", this.server);
        document.put("address", this.address);

        document.put("firstSeen", this.firstSeen);
        document.put("lastSeen", this.lastSeen);
        document.put("identifiers", this.identifiers);
        document.put("chatType", this.chat.name());

        CoreMongoDatabase.getInstance().getProfiles().replaceOne(Filters.eq("uuid", this.uuid.toString()), document, new ReplaceOptions().upsert(true));
    }

    public void addToMap() {
        Profile.profileMap.put(this.uuid, this);
    }

    public void removeFromMap() {
        Profile.profileMap.remove(this.uuid);
    }

    public List<Punishment> getPunishmentByType(PunishmentType type) {
        List<Punishment> toReturn = new ArrayList<Punishment>();
        for (Punishment punishment : this.punishments) {
            if (punishment.getType() == type) {
                toReturn.add(punishment);
            }
        }
        return toReturn;
    }

    public Punishment getWarnPunishment() {
        for (Punishment punishment : this.punishments) {
            if (punishment.isWarn() && punishment.isActive()) {
                return punishment;
            }
        }
        return null;
    }

    public Punishment getBannedPunishment() {
        for (Punishment punishment : this.punishments) {
            if (punishment.isBan() && punishment.isActive()) {
                return punishment;
            }
        }
        return null;
    }

    public Punishment getBlacklistPunishment() {
        for (Punishment punishment : this.punishments) {
            if (punishment.isBan() && punishment.isActive()) {
                return punishment;
            }
        }
        return null;
    }

    public Punishment getMutedPunishment() {
        for (Punishment punishment : this.punishments) {
            if (punishment.isMute() && punishment.isActive()) {
                return punishment;
            }
        }
        return null;
    }

    public void findAlts() {
        if (this.address == null) return;

        this.alts.clear();

        try (MongoCursor<Document> cursor = CoreMongoDatabase.getInstance().getProfiles().find(Filters.eq("address", this.address)).iterator()) {
            cursor.forEachRemaining(document -> {
                UUID uuid = UUID.fromString(document.getString("uuid"));

                if (!uuid.equals(this.getUuid()) && !this.alts.contains(uuid)) {
                    this.alts.add(uuid);
                }
            });
        }
    }

    public static Profile getProfileByUUID(UUID uuid) {
        Profile profile = new Profile(uuid);
        if (Profile.profileMap.get(uuid) != null) {
            profile = Profile.profileMap.get(uuid);
        }
        return profile;
    }
}