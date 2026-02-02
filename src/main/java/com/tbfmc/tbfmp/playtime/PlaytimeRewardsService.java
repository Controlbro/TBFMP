package com.tbfmc.tbfmp.playtime;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaytimeRewardsService {
    private final PlaytimeRewardsConfig config;
    private final PlaytimeStorage storage;
    private final PlaytimeTracker tracker;
    private final MessageService messages;

    public PlaytimeRewardsService(PlaytimeRewardsConfig config, PlaytimeStorage storage,
                                  PlaytimeTracker tracker, MessageService messages) {
        this.config = config;
        this.storage = storage;
        this.tracker = tracker;
        this.messages = messages;
    }

    public Inventory createMenu(Player player) {
        Map<Integer, PlaytimeReward> rewardSlots = new HashMap<>();
        Inventory inventory = Bukkit.createInventory(new PlaytimeRewardsMenuHolder(rewardSlots),
                config.getSize(), messages.colorize(config.getTitle()));
        fillFiller(inventory);
        long playtime = tracker.getTotalPlaytimeSeconds(player.getUniqueId());
        for (PlaytimeReward reward : config.getRewards().values()) {
            ItemStack item = buildItem(player, reward, playtime);
            inventory.setItem(reward.slot(), item);
            rewardSlots.put(reward.slot(), reward);
        }
        return inventory;
    }

    public void handleClick(Player player, PlaytimeReward reward) {
        long playtime = tracker.getTotalPlaytimeSeconds(player.getUniqueId());
        if (storage.hasClaimed(player.getUniqueId(), reward.id())) {
            messages.sendMessage(player, messages.getMessage("messages.playtime-reward-already-claimed"));
            return;
        }
        if (playtime < reward.requiredSeconds()) {
            String message = messages.getMessage("messages.playtime-reward-locked")
                    .replace("{required}", formatDuration(reward.requiredSeconds()))
                    .replace("{playtime}", formatDuration(playtime));
            messages.sendMessage(player, message);
            return;
        }
        runCommands(player, reward.commands());
        storage.claimReward(player.getUniqueId(), reward.id());
        storage.save();
        String message = messages.getMessage("messages.playtime-reward-claimed")
                .replace("{reward}", reward.id());
        messages.sendMessage(player, message);
        player.openInventory(createMenu(player));
    }

    private void runCommands(Player player, List<String> commands) {
        for (String command : commands) {
            if (command == null || command.isBlank()) {
                continue;
            }
            String parsed = command
                    .replace("{player}", player.getName())
                    .replace("{uuid}", player.getUniqueId().toString());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        }
    }

    private ItemStack buildItem(Player player, PlaytimeReward reward, long playtime) {
        Material material = reward.material();
        if (material == null) {
            material = Material.STONE;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(messages.colorize(reward.name()));
            List<String> lore = new ArrayList<>();
            String status;
            if (storage.hasClaimed(player.getUniqueId(), reward.id())) {
                status = config.getStatusClaimed();
            } else if (playtime >= reward.requiredSeconds()) {
                status = config.getStatusClaimable();
            } else {
                status = config.getStatusLocked();
            }
            for (String line : reward.lore()) {
                lore.add(messages.colorize(line
                        .replace("{status}", status)
                        .replace("{required}", formatDuration(reward.requiredSeconds()))
                        .replace("{playtime}", formatDuration(playtime))));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void fillFiller(Inventory inventory) {
        Material fillerMaterial = config.getFillerMaterial();
        if (fillerMaterial == null || fillerMaterial == Material.AIR) {
            return;
        }
        ItemStack filler = new ItemStack(fillerMaterial);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(messages.colorize(config.getFillerName()));
            filler.setItemMeta(meta);
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private String formatDuration(long seconds) {
        long hours = seconds / 3600L;
        long minutes = (seconds % 3600L) / 60L;
        long remaining = seconds % 60L;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m " + remaining + "s";
        }
        return remaining + "s";
    }
}
