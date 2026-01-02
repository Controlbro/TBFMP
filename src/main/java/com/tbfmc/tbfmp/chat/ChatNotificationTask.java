package com.tbfmc.tbfmp.chat;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatNotificationTask {
    private final JavaPlugin plugin;
    private final MessageService messages;
    private final AtomicInteger index = new AtomicInteger(0);
    private BukkitTask task;

    public ChatNotificationTask(JavaPlugin plugin, MessageService messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void start() {
        List<String> notifications = plugin.getConfig().getStringList("chat-notifications.messages");
        if (notifications.isEmpty()) {
            return;
        }
        long intervalTicks = plugin.getConfig().getLong("chat-notifications.interval-seconds", 300) * 20L;
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (notifications.isEmpty()) {
                return;
            }
            int current = index.getAndUpdate(value -> (value + 1) % notifications.size());
            String message = notifications.get(current);
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(messages.formatMessage(message)));
        }, intervalTicks, intervalTicks);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }
}
