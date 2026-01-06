package com.tbfmc.tbfmp.quests;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestSummaryService {
    private final QuestAssignmentManager assignmentManager;
    private final MessageService messages;
    private final FileConfiguration config;

    public QuestSummaryService(JavaPlugin plugin, MessageService messages, QuestAssignmentManager assignmentManager) {
        this.assignmentManager = assignmentManager;
        this.messages = messages;
        this.config = plugin.getConfig();
    }

    public Inventory createMenu(Player player) {
        int size = clampSize(config.getInt("quests-summary.size", 27));
        Inventory inventory = Bukkit.createInventory(new QuestSummaryMenuHolder(), size,
                messages.colorize(config.getString("quests-summary.title", "&2Quests")));
        fillFiller(inventory);
        Set<Integer> usedSlots = new HashSet<>();
        List<AssignedQuest> assignedQuests = assignmentManager.getAssignedQuests(player.getUniqueId());
        for (AssignedQuest assignedQuest : assignedQuests) {
            QuestService service = assignedQuest.getQuestService();
            QuestDefinition quest = assignedQuest.getQuestDefinition();
            int slot = findNextSlot(inventory, usedSlots);
            if (slot == -1) {
                break;
            }
            usedSlots.add(slot);
            inventory.setItem(slot, service.createQuestSummaryItem(player, quest));
        }
        return inventory;
    }

    private int clampSize(int size) {
        if (size < 9) {
            return 9;
        }
        return Math.min(size, 54);
    }

    private void fillFiller(Inventory inventory) {
        String materialName = config.getString("quests-summary.filler.material", "GRAY_STAINED_GLASS_PANE");
        Material fillerMaterial = Material.matchMaterial(materialName);
        if (fillerMaterial == null || fillerMaterial == Material.AIR) {
            return;
        }
        ItemStack filler = new ItemStack(fillerMaterial);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(messages.colorize(config.getString("quests-summary.filler.name", " ")));
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
