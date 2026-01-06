package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.quests.QuestService;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class QuestProgressListener implements Listener {
    private final List<QuestService> questServices;

    public QuestProgressListener(List<QuestService> questServices) {
        this.questServices = questServices;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        for (QuestService service : questServices) {
            service.handleBlockBreak(player, event.getBlock().getType());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) {
            return;
        }
        EntityType type = entity.getType();
        for (QuestService service : questServices) {
            service.handleMobKill(killer, type);
        }
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        LivingEntity breeder = event.getBreeder();
        if (!(breeder instanceof Player player)) {
            return;
        }
        EntityType type = event.getEntityType();
        for (QuestService service : questServices) {
            service.handleBreed(player, type);
        }
    }
}
