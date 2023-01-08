package online.nasgar.staffcore.redis.packets;

import com.google.gson.JsonObject;
import lombok.Data;
import online.nasgar.staffcore.profile.ProfileChat;
import online.nasgar.staffcore.redis.packet.Packet;
import online.nasgar.staffcore.redis.packet.json.JsonChain;

@Data
public class ChatPacket implements Packet {

    private String name, server, message;
    private ProfileChat chatType;

    @Override
    public String id() {
        return "CHAT_PACKET";
    }

    @Override
    public JsonObject serialize() {
        return new JsonChain().addProperty("name", this.name)
                .addProperty("server", this.server)
                .addProperty("message", this.message)
                .addProperty("chatType", this.chatType.name()).get();
    }

    @Override
    public void deserialize(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.server = object.get("server").getAsString();
        this.message = object.get("message").getAsString();
        this.chatType = ProfileChat.valueOf(object.get("chatType").getAsString());
    }

    public ChatPacket() {}

    public ChatPacket(String name, String server, String message, ProfileChat chatType) {
        this.name = name;
        this.server = server;
        this.message = message;
        this.chatType = chatType;
    }
}