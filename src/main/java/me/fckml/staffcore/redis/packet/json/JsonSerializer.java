package me.fckml.staffcore.redis.packet.json;

import com.google.gson.JsonObject;

public interface JsonSerializer<T> {

    JsonObject serialize(T t);
}
