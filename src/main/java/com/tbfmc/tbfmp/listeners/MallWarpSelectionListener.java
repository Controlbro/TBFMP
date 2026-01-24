package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.mallwarp.MallWarpSelectionManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class MallWarpSelectionListener implements Listener {
    private final MallWarpSelectionManager selectionManager;
    private final MessageService messages;

    public MallWarpSelectionListener(MallWarpSelectionManager selectionManager, MessageService messages) {
        this.selectionManager = selectionManager;
        this.messages = messages;
    }

    @EventHandler
    public void onSelect(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.hasPermission("oakglowutil.admin.mallwarp")) {
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.GOLDEN_HOE) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_BLOCK) {
            selectionManager.setFirstSelection(player, block.getLocation());
            messages.sendMessage(player, messages.getMessage("messages.mallwarp-region-first"));
            event.setCancelled(true);
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            selectionManager.setSecondSelection(player, block.getLocation());
            messages.sendMessage(player, messages.getMessage("messages.mallwarp-region-second"));
            event.setCancelled(true);
        }
    }
}
