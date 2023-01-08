package online.nasgar.staffcore.punishment.packets;


import com.google.gson.JsonObject;
import lombok.Data;
import online.nasgar.staffcore.StaffCore;
import online.nasgar.staffcore.punishment.Punishment;
import online.nasgar.staffcore.redis.packet.Packet;
import online.nasgar.staffcore.redis.packet.json.JsonChain;

@Data
public class PunishmentExecutePacket implements Packet {

    private Punishment punishment;
    private String victimName;
    private String staffName;

    @Override
    public String id() {
        return "PUNISHMENT_EXECUTE";
    }

    @Override
    public JsonObject serialize() {
        return new JsonChain().addProperty("punishment", StaffCore.GSON.toJson(this.punishment)).addProperty("victimName", this.victimName).addProperty("staffName", this.staffName).get();
    }

    @Override
    public void deserialize(JsonObject object) {
        this.punishment = StaffCore.GSON.fromJson(object.get("punishment").getAsString(), Punishment.class);
        this.staffName = object.get("staffName").getAsString();
        this.victimName = object.get("victimName").getAsString();
    }

    public PunishmentExecutePacket() {}

    public PunishmentExecutePacket(Punishment punishment, String victimName, String staffName) {
        this.punishment = punishment;
        this.victimName = victimName;
        this.staffName = staffName;
    }
}