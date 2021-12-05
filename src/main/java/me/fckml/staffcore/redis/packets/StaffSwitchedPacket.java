package me.fckml.staffcore.redis.packets;

import com.google.gson.JsonObject;
import lombok.Data;
import me.fckml.staffcore.redis.packet.Packet;
import me.fckml.staffcore.redis.packet.json.JsonChain;

@Data
public class StaffSwitchedPacket implements Packet {

    private String name;
    private String server;
    private String newServer;

    @Override
    public String id() {
        return "STAFF_SWITCHED";
    }

    @Override
    public JsonObject serialize() {
        return new JsonChain().addProperty("name", this.name).addProperty("server", this.server).addProperty("newServer", this.newServer).get();
    }

    @Override
    public void deserialize(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.server = object.get("server").getAsString();
        this.newServer = object.get("newServer").getAsString();
    }

    public StaffSwitchedPacket() {}

    public StaffSwitchedPacket(String name, String server, String newServer) {
        this.name = name;
        this.server = server;
        this.newServer = newServer;
    }
}