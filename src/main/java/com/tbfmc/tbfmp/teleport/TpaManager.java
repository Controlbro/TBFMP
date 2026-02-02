package com.tbfmc.tbfmp.teleport;

import com.tbfmc.tbfmp.util.MessageService;
import com.tbfmc.tbfmp.util.TeleportSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {
    private static final String COOLDOWN_PERMISSION_PREFIX = "oakglow.tpacooldown.";
    private static final int DEFAULT_COOLDOWN_SECONDS = 30;

    private final JavaPlugin plugin;
    private final MessageService messages;
    private final TpaSettingsStorage tpaSettingsStorage;
    private final Map<UUID, TpaRequest> pendingRequests = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public TpaManager(JavaPlugin plugin, MessageService messages, TpaSettingsStorage tpaSettingsStorage) {
        this.plugin = plugin;
        this.messages = messages;
        this.tpaSettingsStorage = tpaSettingsStorage;
    }

    public void requestTeleport(Player requester, Player target, TpaRequestType type) {
        if (requester.getUniqueId().equals(target.getUniqueId())) {
            messages.sendMessage(requester, messages.getMessage("messages.tpa-self"));
            return;
        }
        if (!tpaSettingsStorage.isEnabled(target.getUniqueId())) {
            messages.sendMessage(requester, messages.getMessage("messages.tpa-target-disabled"));
            return;
        }
        int remainingCooldown = getRemainingCooldownSeconds(requester.getUniqueId());
        if (remainingCooldown > 0) {
            messages.sendMessage(requester, messages.getMessage("messages.tpa-cooldown")
                    .replace("{seconds}", String.valueOf(remainingCooldown)));
            return;
        }
        UUID targetId = target.getUniqueId();
        if (pendingRequests.containsKey(targetId)) {
            messages.sendMessage(requester, messages.getMessage("messages.tpa-request-pending"));
            return;
        }
        pendingRequests.put(targetId, new TpaRequest(requester.getUniqueId(), type));
        messages.sendMessage(requester, messages.getMessage("messages.tpa-request-sent")
                .replace("{player}", target.getName()));
        String requestKey = type == TpaRequestType.TO
                ? "messages.tpa-request-received"
                : "messages.tpahere-request-received";
        messages.sendMessage(target, messages.getMessage(requestKey)
                .replace("{player}", requester.getName()));
        messages.sendMessage(target, messages.getMessage("messages.tpa-request-actions"));
    }

    public void acceptRequest(Player target) {
        TpaRequest request = pendingRequests.remove(target.getUniqueId());
        if (request == null) {
            messages.sendMessage(target, messages.getMessage("messages.tpa-no-request"));
            return;
        }
        Player requester = Bukkit.getPlayer(request.requesterId());
        if (requester == null || !requester.isOnline()) {
            messages.sendMessage(target, messages.getMessage("messages.tpa-requester-offline"));
            return;
        }
        Player teleportPlayer = request.type() == TpaRequestType.TO ? requester : target;
        Location destination = request.type() == TpaRequestType.TO
                ? target.getLocation()
                : requester.getLocation();
        teleportPlayer.teleportAsync(destination).thenAccept(success -> {
            if (!success) {
                return;
            }
            int cooldownSeconds = resolveCooldownSeconds(requester);
            if (cooldownSeconds > 0) {
                cooldowns.put(requester.getUniqueId(),
                        System.currentTimeMillis() + cooldownSeconds * 1000L);
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (teleportPlayer.isOnline()) {
                    TeleportSound.play(teleportPlayer);
                }
            });
        });
        messages.sendMessage(target, messages.getMessage("messages.tpa-accepted"));
        messages.sendMessage(requester, messages.getMessage("messages.tpa-accepted-requester")
                .replace("{player}", target.getName()));
    }

    public void denyRequest(Player target) {
        TpaRequest request = pendingRequests.remove(target.getUniqueId());
        if (request == null) {
            messages.sendMessage(target, messages.getMessage("messages.tpa-no-request"));
            return;
        }
        Player requester = Bukkit.getPlayer(request.requesterId());
        if (requester != null && requester.isOnline()) {
            messages.sendMessage(requester, messages.getMessage("messages.tpa-denied")
                    .replace("{player}", target.getName()));
        }
        messages.sendMessage(target, messages.getMessage("messages.tpa-denied-target")
                .replace("{player}", requester != null ? requester.getName() : ""));
    }

    private int getRemainingCooldownSeconds(UUID uuid) {
        Long until = cooldowns.get(uuid);
        if (until == null) {
            return 0;
        }
        long remainingMillis = until - System.currentTimeMillis();
        if (remainingMillis <= 0) {
            cooldowns.remove(uuid);
            return 0;
        }
        return (int) Math.ceil(remainingMillis / 1000.0);
    }

    private int resolveCooldownSeconds(Player player) {
        Integer override = null;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (!info.getValue()) {
                continue;
            }
            String permission = info.getPermission();
            if (!permission.startsWith(COOLDOWN_PERMISSION_PREFIX)) {
                continue;
            }
            String value = permission.substring(COOLDOWN_PERMISSION_PREFIX.length());
            try {
                int seconds = Integer.parseInt(value);
                if (seconds < 0) {
                    continue;
                }
                if (override == null || seconds < override) {
                    override = seconds;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return override != null ? override : DEFAULT_COOLDOWN_SECONDS;
    }

    public enum TpaRequestType {
        TO,
        HERE
    }

    public record TpaRequest(UUID requesterId, TpaRequestType type) {
    }
}
