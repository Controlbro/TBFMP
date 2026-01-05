package com.tbfmc.tbfmp.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class TreeFellerListener implements Listener {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Set<Block> ignoredBlocks = new HashSet<>();

    public TreeFellerListener(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (ignoredBlocks.remove(block)) {
            return;
        }
        if (!config.getBoolean("tree-feller.enabled", true)) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!isAxe(tool)) {
            return;
        }
        if (!isLog(block.getType())) {
            return;
        }

        Set<Block> logs = collectLogs(block);
        logs.remove(block);
        if (logs.isEmpty()) {
            return;
        }
        for (Block log : logs) {
            ignoredBlocks.add(log);
            player.breakBlock(log);
        }
        if (config.getBoolean("tree-feller.auto-replant", false)) {
            attemptReplant(player, block.getType(), block);
        }
    }

    private Set<Block> collectLogs(Block origin) {
        Set<Block> logs = new HashSet<>();
        Deque<Block> queue = new ArrayDeque<>();
        queue.add(origin);
        logs.add(origin);
        while (!queue.isEmpty()) {
            Block current = queue.removeFirst();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        Block neighbor = current.getRelative(dx, dy, dz);
                        if (logs.contains(neighbor)) {
                            continue;
                        }
                        if (isLog(neighbor.getType())) {
                            logs.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        return logs;
    }

    private boolean isAxe(ItemStack tool) {
        if (tool == null) {
            return false;
        }
        Material type = tool.getType();
        return type.name().endsWith("_AXE");
    }

    private boolean isLog(Material material) {
        String name = material.name();
        return name.endsWith("_LOG") || name.endsWith("_STEM") || name.endsWith("_HYPHAE");
    }

    private void attemptReplant(Player player, Material logType, Block brokenBlock) {
        Material sapling = getSaplingForLog(logType);
        if (sapling == null) {
            return;
        }
        Block base = findBaseLog(brokenBlock);
        if (base == null) {
            return;
        }
        if (!player.getInventory().containsAtLeast(new ItemStack(sapling), 1)) {
            return;
        }
        Block soil = base.getRelative(BlockFace.DOWN);
        if (!isSoil(soil.getType())) {
            return;
        }
        player.getInventory().removeItem(new ItemStack(sapling, 1));
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (base.getType() == Material.AIR) {
                base.setType(sapling);
            }
        });
    }

    private Block findBaseLog(Block block) {
        Block current = block;
        while (isLog(current.getType())) {
            Block next = current.getRelative(BlockFace.DOWN);
            if (!isLog(next.getType())) {
                return current;
            }
            current = next;
        }
        return null;
    }

    private boolean isSoil(Material material) {
        return material == Material.DIRT
                || material == Material.GRASS_BLOCK
                || material == Material.PODZOL
                || material == Material.COARSE_DIRT
                || material == Material.ROOTED_DIRT
                || material == Material.MYCELIUM;
    }

    private Material getSaplingForLog(Material logType) {
        return switch (logType) {
            case OAK_LOG -> Material.OAK_SAPLING;
            case SPRUCE_LOG -> Material.SPRUCE_SAPLING;
            case BIRCH_LOG -> Material.BIRCH_SAPLING;
            case JUNGLE_LOG -> Material.JUNGLE_SAPLING;
            case ACACIA_LOG -> Material.ACACIA_SAPLING;
            case DARK_OAK_LOG -> Material.DARK_OAK_SAPLING;
            case MANGROVE_LOG -> Material.MANGROVE_PROPAGULE;
            case CHERRY_LOG -> Material.CHERRY_SAPLING;
            case CRIMSON_STEM, CRIMSON_HYPHAE -> Material.CRIMSON_FUNGUS;
            case WARPED_STEM, WARPED_HYPHAE -> Material.WARPED_FUNGUS;
            default -> null;
        };
    }
}
