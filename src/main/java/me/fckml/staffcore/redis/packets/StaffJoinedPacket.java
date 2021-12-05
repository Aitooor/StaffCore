package me.fckml.staffcore.redis.packets;

import com.google.gson.JsonObject;
import lombok.Data;
import me.fckml.staffcore.redis.packet.Packet;
import me.fckml.staffcore.redis.packet.json.JsonChain;

@Data
public class StaffJoinedPacket implements Packet {

    private String name;
    private String server;

    @Override
    public String id() {
        return "STAFF_JOINED";
    }

    @Override
    public JsonObject serialize() {
        return new JsonChain().addProperty("name", this.name).addProperty("server", this.server).get();
    }

    @Override
    public void deserialize(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.server = object.get("server").getAsString();
    }

    public StaffJoinedPacket() {}

    public StaffJoinedPacket(String name, String server) {
        this.name = name;
        this.server = server;
    }
}
