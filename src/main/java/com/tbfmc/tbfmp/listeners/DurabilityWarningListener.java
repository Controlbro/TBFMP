package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.util.MessageService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class DurabilityWarningListener implements Listener {
    private static final int WARNING_THRESHOLD = 10;
    private static final long COOLDOWN_MS = 3000L;

    private final MessageService messages;
    private final Map<UUID, Long> lastWarning = new HashMap<>();

    public DurabilityWarningListener(MessageService messages) {
        this.messages = messages;
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        if (!(item.getItemMeta() instanceof Damageable damageable)) {
            return;
        }
        int maxDurability = item.getType().getMaxDurability();
        if (maxDurability <= 0) {
            return;
        }
        int remaining = maxDurability - (damageable.getDamage() + event.getDamage());
        if (remaining <= 0 || remaining > WARNING_THRESHOLD) {
            return;
        }
        Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        Long last = lastWarning.get(player.getUniqueId());
        if (last != null && now - last < COOLDOWN_MS) {
            return;
        }
        lastWarning.put(player.getUniqueId(), now);
        String message = messages.getMessage("messages.durability-warning")
                .replace("{item}", formatItemName(item))
                .replace("{remaining}", String.valueOf(remaining))
                .replace("{max}", String.valueOf(maxDurability));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    private String formatItemName(ItemStack item) {
        String name = item.getType().name().toLowerCase(Locale.ROOT).replace('_', ' ');
        String[] parts = name.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
        }
        return builder.toString().trim();
    }
}
