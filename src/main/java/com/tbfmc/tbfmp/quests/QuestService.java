package com.tbfmc.tbfmp.quests;

import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestService {
    private final QuestConfig config;
    private final QuestProgressStorage progressStorage;
    private final BalanceStorage balanceStorage;
    private final MessageService messages;
    private final NamespacedKey questKey;

    public QuestService(QuestConfig config, QuestProgressStorage progressStorage,
                        BalanceStorage balanceStorage, MessageService messages, NamespacedKey questKey) {
        this.config = config;
        this.progressStorage = progressStorage;
        this.balanceStorage = balanceStorage;
        this.messages = messages;
        this.questKey = questKey;
    }

    public String getCategory() {
        return config.getCategory();
    }

    public Inventory createMenu(Player player) {
        int size = config.getSize();
        Inventory inventory = Bukkit.createInventory(new QuestMenuHolder(config.getCategory()), size,
                messages.colorize(config.getTitle()));
        fillFiller(inventory);
        Set<Integer> usedSlots = new HashSet<>();
        for (QuestDefinition quest : config.getQuests()) {
            int slot = quest.getSlot();
            if (slot < 0 || slot >= size || usedSlots.contains(slot)) {
                slot = findNextSlot(inventory, usedSlots);
            }
            if (slot == -1) {
                continue;
            }
            usedSlots.add(slot);
            QuestProgress progress = progressStorage.getProgress(player.getUniqueId(), config.getCategory(), quest);
            inventory.setItem(slot, buildQuestItem(quest, progress));
        }
        return inventory;
    }

    public void handleBlockBreak(Player player, Material material) {
        for (QuestDefinition quest : config.getQuests()) {
            if (quest.getObjectiveType() != QuestObjectiveType.BLOCK_BREAK) {
                continue;
            }
            if (quest.getBlockMaterial() == null || quest.getBlockMaterial() != material) {
                continue;
            }
            updateProgress(player, quest, 1);
        }
    }

    public void handleMobKill(Player player, org.bukkit.entity.EntityType entityType) {
        for (QuestDefinition quest : config.getQuests()) {
            if (quest.getObjectiveType() != QuestObjectiveType.MOB_KILL) {
                continue;
            }
            if (quest.getEntityType() == null || quest.getEntityType() != entityType) {
                continue;
            }
            updateProgress(player, quest, 1);
        }
    }

    public void handleBreed(Player player, org.bukkit.entity.EntityType entityType) {
        for (QuestDefinition quest : config.getQuests()) {
            if (quest.getObjectiveType() != QuestObjectiveType.BREED) {
                continue;
            }
            if (quest.getEntityType() == null || quest.getEntityType() != entityType) {
                continue;
            }
            updateProgress(player, quest, 1);
        }
    }

    private void updateProgress(Player player, QuestDefinition quest, int amount) {
        QuestProgress progress = progressStorage.getProgress(player.getUniqueId(), config.getCategory(), quest);
        if (progress.isCompleted()) {
            return;
        }
        int newProgress = Math.min(quest.getTarget(), progress.getProgress() + amount);
        boolean completed = newProgress >= quest.getTarget();
        progressStorage.setProgress(player.getUniqueId(), config.getCategory(), quest.getId(),
                newProgress, completed, progress.getStartTime());
        if (completed) {
            rewardPlayer(player, quest);
        }
    }

    private void rewardPlayer(Player player, QuestDefinition quest) {
        QuestReward reward = quest.getReward();
        if (reward.getMoney() > 0) {
            balanceStorage.addBalance(player.getUniqueId(), reward.getMoney());
        }
        giveItem(player, Material.DIAMOND, reward.getDiamonds());
        giveItem(player, Material.IRON_INGOT, reward.getIron());
        String completeMessage = messages.getMessage("messages.quest-complete")
                .replace("{quest}", quest.getName());
        messages.sendMessage(player, completeMessage);
        String rewardMessage = messages.getMessage("messages.quest-reward")
                .replace("{money}", String.format("%.2f", reward.getMoney()))
                .replace("{diamonds}", String.valueOf(reward.getDiamonds()))
                .replace("{iron}", String.valueOf(reward.getIron()));
        messages.sendMessage(player, rewardMessage);
    }

    private void giveItem(Player player, Material material, int amount) {
        if (amount <= 0) {
            return;
        }
        ItemStack stack = new ItemStack(material, amount);
        player.getInventory().addItem(stack).values().forEach(item ->
                player.getWorld().dropItemNaturally(player.getLocation(), item));
    }

    private ItemStack buildQuestItem(QuestDefinition quest, QuestProgress progress) {
        ItemStack item = new ItemStack(quest.getItemMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String status = progress.isCompleted() ? config.getStatusComplete() : config.getStatusIncomplete();
            meta.setDisplayName(messages.colorize(applyPlaceholders(quest.getName(), quest, progress, status)));
            List<String> loreTemplate = quest.getLore().isEmpty() ? config.getDefaultLore() : quest.getLore();
            List<String> lore = new ArrayList<>();
            for (String line : loreTemplate) {
                lore.add(messages.colorize(applyPlaceholders(line, quest, progress, status)));
            }
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(questKey, PersistentDataType.STRING, quest.getId());
            item.setItemMeta(meta);
        }
        return item;
    }

    private String applyPlaceholders(String line, QuestDefinition quest, QuestProgress progress, String status) {
        QuestReward reward = quest.getReward();
        return line
                .replace("{quest}", quest.getName())
                .replace("{type}", quest.getType().getDisplayName())
                .replace("{progress}", String.valueOf(progress.getProgress()))
                .replace("{target}", String.valueOf(quest.getTarget()))
                .replace("{status}", status)
                .replace("{money}", String.format("%.2f", reward.getMoney()))
                .replace("{diamonds}", String.valueOf(reward.getDiamonds()))
                .replace("{iron}", String.valueOf(reward.getIron()));
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
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        }
    }

    private int findNextSlot(Inventory inventory, Set<Integer> usedSlots) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null && !usedSlots.contains(slot)) {
                return slot;
            }
        }
        return -1;
    }
}
