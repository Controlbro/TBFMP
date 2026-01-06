package com.tbfmc.tbfmp.quests;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.List;

public class QuestDefinition {
    private final String id;
    private final String name;
    private final QuestType type;
    private final QuestObjectiveType objectiveType;
    private final Material blockMaterial;
    private final EntityType entityType;
    private final int target;
    private final Material itemMaterial;
    private final int slot;
    private final List<String> lore;
    private final QuestReward reward;

    public QuestDefinition(String id, String name, QuestType type, QuestObjectiveType objectiveType,
                           Material blockMaterial, EntityType entityType, int target,
                           Material itemMaterial, int slot, List<String> lore, QuestReward reward) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.objectiveType = objectiveType;
        this.blockMaterial = blockMaterial;
        this.entityType = entityType;
        this.target = target;
        this.itemMaterial = itemMaterial;
        this.slot = slot;
        this.lore = lore;
        this.reward = reward;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public QuestType getType() {
        return type;
    }

    public QuestObjectiveType getObjectiveType() {
        return objectiveType;
    }

    public Material getBlockMaterial() {
        return blockMaterial;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public int getTarget() {
        return target;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public int getSlot() {
        return slot;
    }

    public List<String> getLore() {
        return lore;
    }

    public QuestReward getReward() {
        return reward;
    }
}
