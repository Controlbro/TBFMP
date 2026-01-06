package com.tbfmc.tbfmp.quests;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestConfig {
    private final String category;
    private final FileConfiguration data;
    private final List<QuestDefinition> quests = new ArrayList<>();

    public QuestConfig(JavaPlugin plugin, String category, String fileName) {
        this.category = category;
        File file = new File(plugin.getDataFolder(), fileName);
        this.data = YamlConfiguration.loadConfiguration(file);
        loadQuests();
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return data.getString("menu.title", "&2Quests");
    }

    public int getSize() {
        int size = data.getInt("menu.size", 27);
        if (size < 9) {
            return 9;
        }
        return Math.min(size, 54);
    }

    public Material getFillerMaterial() {
        String materialName = data.getString("menu.filler.material", "GRAY_STAINED_GLASS_PANE");
        Material material = Material.matchMaterial(materialName);
        return material != null ? material : Material.GRAY_STAINED_GLASS_PANE;
    }

    public String getFillerName() {
        return data.getString("menu.filler.name", " ");
    }

    public String getStatusComplete() {
        return data.getString("menu.status-complete", "&aComplete");
    }

    public String getStatusIncomplete() {
        return data.getString("menu.status-incomplete", "&eIn progress");
    }

    public List<String> getDefaultLore() {
        return new ArrayList<>(data.getStringList("menu.default-lore"));
    }

    public List<QuestDefinition> getQuests() {
        return Collections.unmodifiableList(quests);
    }

    private void loadQuests() {
        quests.clear();
        ConfigurationSection section = data.getConfigurationSection("quests");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection questSection = section.getConfigurationSection(key);
            if (questSection == null) {
                continue;
            }
            String name = questSection.getString("name", key);
            QuestType type = QuestType.fromString(questSection.getString("type", "DAILY"));
            ConfigurationSection objectiveSection = questSection.getConfigurationSection("objective");
            QuestObjectiveType objectiveType = objectiveSection != null
                    ? QuestObjectiveType.fromString(objectiveSection.getString("type", "BLOCK_BREAK"))
                    : QuestObjectiveType.BLOCK_BREAK;
            Material blockMaterial = null;
            EntityType entityType = null;
            if (objectiveSection != null) {
                String materialName = objectiveSection.getString("material", "");
                if (!materialName.isEmpty()) {
                    blockMaterial = Material.matchMaterial(materialName);
                }
                String entityName = objectiveSection.getString("entity", "");
                if (!entityName.isEmpty()) {
                    try {
                        entityType = EntityType.valueOf(entityName.toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
            int target = Math.max(1, questSection.getInt("target", 1));
            String itemMaterialName = questSection.getString("item", "BOOK");
            Material itemMaterial = Material.matchMaterial(itemMaterialName);
            if (itemMaterial == null) {
                itemMaterial = Material.BOOK;
            }
            int slot = questSection.getInt("slot", -1);
            List<String> lore = new ArrayList<>(questSection.getStringList("lore"));
            ConfigurationSection rewardSection = questSection.getConfigurationSection("rewards");
            double money = rewardSection != null ? rewardSection.getDouble("money", 0.0) : 0.0;
            int diamonds = rewardSection != null ? rewardSection.getInt("diamonds", 0) : 0;
            int iron = rewardSection != null ? rewardSection.getInt("iron", 0) : 0;
            QuestReward reward = new QuestReward(money, diamonds, iron);
            quests.add(new QuestDefinition(key, name, type, objectiveType, blockMaterial, entityType,
                    target, itemMaterial, slot, lore, reward));
        }
    }
}
