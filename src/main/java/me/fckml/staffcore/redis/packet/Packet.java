package me.fckml.staffcore.redis.packet;

import com.google.gson.JsonObject;

public interface Packet {

    String id();

    JsonObject serialize();

    void deserialize(JsonObject object);
}
