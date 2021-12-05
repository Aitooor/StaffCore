package me.fckml.staffcore.redis.packet.handler;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IncomingPacketHandler {}
