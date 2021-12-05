package me.fckml.staffcore.utils;

import me.fckml.staffcore.StaffCore;
import org.bukkit.Bukkit;

public class Tasks {

    public static void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(StaffCore.getInstance(), runnable);
    }

    public static void runTaskLater(Runnable runnable, long time) {
        Bukkit.getScheduler().runTaskLater(StaffCore.getInstance(), runnable, time);
    }

    public static void runTaskTimer(Runnable runnable, long delay, long repeat) {
        Bukkit.getScheduler().runTaskTimer(StaffCore.getInstance(), runnable, delay, repeat);
    }

    public static void runAsyncTask(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(StaffCore.getInstance(), runnable);
    }

    public static void runAsyncTaskLater(Runnable runnable, long time) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(StaffCore.getInstance(), runnable, time);
    }

    public static void runAsyncTaskTimer(Runnable runnable, long delay, long repeat) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(StaffCore.getInstance(), runnable, delay, repeat);
    }
}
