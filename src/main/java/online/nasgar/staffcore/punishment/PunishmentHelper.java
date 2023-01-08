package online.nasgar.staffcore.punishment;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import online.nasgar.staffcore.mongo.CoreMongoDatabase;
import online.nasgar.staffcore.punishment.packets.PunishmentExecutePacket;
import online.nasgar.staffcore.punishment.packets.PunishmentUndoExecutePacket;
import online.nasgar.staffcore.redis.CoreRedisDatabase;
import org.bson.Document;

import java.util.UUID;

public class PunishmentHelper {
    public static void load(Document document, Punishment punishment) {
        if (document == null) return;

        punishment.setUuid(UUID.fromString(document.getString("uuid")));
        punishment.setType(PunishmentType.valueOf(document.getString("type")));
        punishment.setTargetID(UUID.fromString(document.getString("targetID")));
        punishment.setAddedReason(document.getString("addedReason"));
        punishment.setAddedAt(document.getLong("addedAt"));
        punishment.setSilent(document.getBoolean("silent"));
        punishment.setStaffName(document.getString("staffName"));
        punishment.setVictimName(document.getString("victimName"));
        
        if (document.containsKey("addedBy")) punishment.setAddedBy(UUID.fromString(document.getString("addedBy")));
        if (document.containsKey("removedAt")) punishment.setRemovedAt(document.getLong("removedAt"));
        if (document.containsKey("removedBy")) punishment.setRemovedBy(UUID.fromString(document.getString("removedBy")));
        if (document.containsKey("removedReason")) punishment.setRemovedReason(document.getString("removedReason"));
        if (document.containsKey("expiration")) punishment.setExpiration(document.getLong("expiration"));
    }

    public static void save(Punishment punishment) {
        Document document = new Document();

        document.put("uuid", punishment.getUuid().toString());
        document.put("type", punishment.getType().name());
        document.put("targetID", punishment.getTargetID().toString());
        document.put("addedReason", punishment.getAddedReason());
        document.put("addedAt", punishment.getAddedAt());
        document.put("silent", punishment.isSilent());
        document.put("staffName", punishment.getStaffName());
        document.put("victimName", punishment.getVictimName());

        if (punishment.getAddedBy() != null) document.put("addedBy", punishment.getAddedBy().toString());
        if (punishment.getRemovedAt() > 0L) document.put("removedAt", punishment.getRemovedAt());
        if (punishment.getRemovedBy() != null) document.put("removedBy", punishment.getRemovedBy().toString());
        if (punishment.getRemovedReason() != null) document.put("removedReason", punishment.getRemovedReason());
        if (punishment.getExpiration() > 0L) document.put("expiration", punishment.getExpiration());

        CoreMongoDatabase.getInstance().getPunishments().replaceOne(Filters.eq("uuid", punishment.getUuid().toString()), document, new ReplaceOptions().upsert(true));
    }

    public static void publishBan(Punishment punishment, String victimName, String staffName) {
        PunishmentExecutePacket executePacket = new PunishmentExecutePacket(punishment, victimName, staffName);

        CoreRedisDatabase.getInstance().sendPacket(executePacket);
    }

    public static void publishUnBan(Punishment punishment, String victimName, String staffName) {
        PunishmentUndoExecutePacket executePacket = new PunishmentUndoExecutePacket(punishment, victimName, staffName);

        CoreRedisDatabase.getInstance().sendPacket(executePacket);
    }
}

