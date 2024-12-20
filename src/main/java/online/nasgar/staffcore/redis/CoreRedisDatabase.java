package online.nasgar.staffcore.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Data;
import lombok.Getter;

import online.nasgar.staffcore.StaffCore;
import online.nasgar.staffcore.profile.listeners.ProfilePacketListener;
import online.nasgar.staffcore.punishment.listeners.PunishPacketListener;
import online.nasgar.staffcore.punishment.packets.PunishmentExecutePacket;
import online.nasgar.staffcore.punishment.packets.PunishmentUndoExecutePacket;
import online.nasgar.staffcore.redis.packet.Packet;
import online.nasgar.staffcore.redis.packet.handler.PacketExceptionHandler;
import online.nasgar.staffcore.redis.packet.listener.PacketListener;
import online.nasgar.staffcore.redis.packet.listener.PacketListenerData;
import online.nasgar.staffcore.redis.packets.ChatPacket;
import online.nasgar.staffcore.redis.packets.StaffJoinedPacket;
import online.nasgar.staffcore.redis.packets.StaffLeftPacket;
import online.nasgar.staffcore.redis.packets.StaffSwitchedPacket;
import online.nasgar.staffcore.utils.config.ConfigFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

@Data
public class CoreRedisDatabase {

    @Getter
    private static CoreRedisDatabase instance;

    private JedisPool redisPool;
    private JedisPubSub jedisPubSub;

    private JsonParser parser;

    private List<PacketListenerData> packetListeners;

    private Map<String, Class> idToType;
    private Map<Class, String> typeToId;

    ConfigFile configFile = StaffCore.getInstance().getConfigFile();

    String ip = configFile.getString("REDIS.IP");
    int port = configFile.getInteger("REDIS.PORT");
    String password = configFile.getString("REDIS.PASSWORD");
    String channel = configFile.getString("REDIS.CHANNEL");

    public CoreRedisDatabase() {
        instance = this;

        this.parser = new JsonParser();
        this.packetListeners = new ArrayList<PacketListenerData>();

        this.idToType = new HashMap<String, Class>();
        this.typeToId = new HashMap<Class, String>();

        JedisPoolConfig config = new JedisPoolConfig();

        config.setTestWhileIdle(true);
        config.setNumTestsPerEvictionRun(-1);
        config.setMinEvictableIdleTimeMillis(60000L);
        config.setTimeBetweenEvictionRunsMillis(30000L);

        if (password == null || password.isEmpty()) {
            this.redisPool = new JedisPool(config, ip, port, 30000);
        } else {
            this.redisPool = new JedisPool(config, ip, port, 30000, password);
        }

        this.setupPubSub();

        Arrays.asList(ChatPacket.class, StaffJoinedPacket.class, StaffLeftPacket.class, StaffSwitchedPacket.class, PunishmentExecutePacket.class, PunishmentUndoExecutePacket.class).forEach(this::registerPacket);

        Arrays.asList(new PunishPacketListener(), new ProfilePacketListener()).forEach(this::registerListener);
    }

    public void sendPacket(Packet packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(Packet packet, PacketExceptionHandler exceptionHandler) {
        try {
            JsonObject object = packet.serialize();
            if (object == null) {
                throw new IllegalStateException("Packet cannot generate null serialized data");
            }
            try (Jedis jedis = this.redisPool.getResource()) {
                jedis.publish(configFile.getString("REDIS.CHANNEL"), packet.id() + ";" + object.toString());
            }
        } catch (Exception e) {
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
    }

    public Packet buildPacket(String id) {
        if (!this.idToType.containsKey(id)) {
            throw new IllegalStateException("A packet with that ID does not exist");
        }

        try {
            return (Packet) this.idToType.get(id).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not create new instance of packet type");
        }
    }

    public void registerPacket(Class clazz) {
        try {
            String id = (String) clazz.getDeclaredMethod("id", (Class<?>[]) new Class[0]).invoke(clazz.newInstance(), (Object[]) null);
            if (this.idToType.containsKey(id) || this.typeToId.containsKey(clazz)) {
                throw new IllegalStateException("A packet with that ID has already been registered");
            }
            this.idToType.put(id, clazz);
            this.typeToId.put(clazz, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerListener(PacketListener packetListener) {
        for (Method method : packetListener.getClass().getDeclaredMethods()) {
            Class packetClass = null;
            if (method.getParameters().length > 0 && Packet.class.isAssignableFrom(method.getParameters()[0].getType())) {
                packetClass = method.getParameters()[0].getType();
            }
            if (packetClass != null) {
                this.packetListeners.add(new PacketListenerData(packetListener, method, packetClass));
            }
        }
    }

    private void setupPubSub() {
        this.jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (channel.equalsIgnoreCase(configFile.getString("REDIS.CHANNEL"))) {
                    try {
                        String[] args = message.split(";");
                        Packet packet = CoreRedisDatabase.this.buildPacket(args[0]);
                        if (packet != null) {
                            packet.deserialize(CoreRedisDatabase.this.parser.parse(args[1]).getAsJsonObject());
                            for (PacketListenerData data : CoreRedisDatabase.this.packetListeners) {
                                if (data.matches(packet)) {
                                    data.getMethod().invoke(data.getInstance(), packet);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        ForkJoinPool.commonPool().execute(() -> {
            try {
                Jedis jedis = this.redisPool.getResource();

                if (password == null || !password.isEmpty()) jedis.auth(password); // REMOVE THIS LINE IN CASE THAT YOUR REDIS HAS NO PASSWORD
                jedis.subscribe(this.jedisPubSub, configFile.getString("REDIS.CHANNEL"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}