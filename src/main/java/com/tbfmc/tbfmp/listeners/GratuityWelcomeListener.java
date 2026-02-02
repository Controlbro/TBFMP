package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.TBFMPPlugin;
import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class GratuityWelcomeListener implements Listener {
    private static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("0.##");
    private static final String NEW_MESSAGE_KEY = "messages.gratuity-new";
    private static final String RETURNING_MESSAGE_KEY = "messages.gratuity-returning";

    private final TBFMPPlugin plugin;
    private final BalanceStorage balanceStorage;
    private final MessageService messages;
    private final Map<UUID, JoinWindow> joinWindows = new ConcurrentHashMap<>();

    public GratuityWelcomeListener(TBFMPPlugin plugin, BalanceStorage balanceStorage, MessageService messages) {
        this.plugin = plugin;
        this.balanceStorage = balanceStorage;
        this.messages = messages;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!isEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        boolean firstJoin = !player.hasPlayedBefore();
        JoinWindow window = new JoinWindow(firstJoin, System.currentTimeMillis());
        joinWindows.put(player.getUniqueId(), window);

        long windowTicks = getWindowSeconds() * 20L;
        if (windowTicks <= 0) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> joinWindows.remove(player.getUniqueId(), window), windowTicks);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        joinWindows.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!isEnabled() || balanceStorage == null) {
            return;
        }
        String normalized = normalize(event.getMessage());
        Player sender = event.getPlayer();
        long now = System.currentTimeMillis();

        for (Map.Entry<UUID, JoinWindow> entry : joinWindows.entrySet()) {
            UUID joinedId = entry.getKey();
            JoinWindow window = entry.getValue();
            if (window.isExpired(now, getWindowMillis())) {
                joinWindows.remove(joinedId, window);
                continue;
            }
            if (joinedId.equals(sender.getUniqueId())) {
                continue;
            }
            if (!matchesTrigger(window.isFirstJoin(), normalized)) {
                continue;
            }
            if (!window.markRewarded()) {
                continue;
            }

            double amount = window.isFirstJoin() ? getNewReward() : getReturningReward();
            String messageKey = window.isFirstJoin() ? NEW_MESSAGE_KEY : RETURNING_MESSAGE_KEY;
            String message = messages.getMessage(messageKey).replace("{amount}", AMOUNT_FORMAT.format(amount));
            Bukkit.getScheduler().runTask(plugin, () -> {
                balanceStorage.addBalance(sender.getUniqueId(), amount);
                if (!message.isBlank()) {
                    messages.sendMessage(sender, message);
                }
            });
            break;
        }
    }

    private boolean isEnabled() {
        return plugin.getConfig().getBoolean("gratuity.enabled", true);
    }

    private long getWindowSeconds() {
        return plugin.getConfig().getLong("gratuity.window-seconds", 30L);
    }

    private long getWindowMillis() {
        long seconds = getWindowSeconds();
        return Math.max(0L, seconds) * 1000L;
    }

    private double getNewReward() {
        return plugin.getConfig().getDouble("gratuity.new-player-reward", 10.0);
    }

    private double getReturningReward() {
        return plugin.getConfig().getDouble("gratuity.returning-player-reward", 7.0);
    }

    private boolean matchesTrigger(boolean firstJoin, String normalizedMessage) {
        FileConfiguration config = plugin.getConfig();
        List<String> triggers = firstJoin
                ? config.getStringList("gratuity.new-player-triggers")
                : config.getStringList("gratuity.returning-player-triggers");
        if (triggers == null || triggers.isEmpty()) {
            return false;
        }
        for (String trigger : triggers) {
            if (trigger == null || trigger.isBlank()) {
                continue;
            }
            String normalizedTrigger = trigger.trim().toLowerCase();
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(normalizedTrigger) + "\\b");
            if (pattern.matcher(normalizedMessage).find()) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String message) {
        return message == null ? "" : message.toLowerCase();
    }

    private static final class JoinWindow {
        private final boolean firstJoin;
        private final long joinedAt;
        private final AtomicBoolean rewarded = new AtomicBoolean(false);

        private JoinWindow(boolean firstJoin, long joinedAt) {
            this.firstJoin = firstJoin;
            this.joinedAt = joinedAt;
        }

        private boolean isFirstJoin() {
            return firstJoin;
        }

        private boolean isExpired(long now, long windowMillis) {
            if (windowMillis <= 0) {
                return true;
            }
            return now - joinedAt > windowMillis;
        }

        private boolean markRewarded() {
            return rewarded.compareAndSet(false, true);
        }
    }
}
