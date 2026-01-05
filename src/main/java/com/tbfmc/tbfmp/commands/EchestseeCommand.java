package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import com.tbfmc.tbfmp.util.OfflineInventoryHolder;
import com.tbfmc.tbfmp.util.OfflineInventoryStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EchestseeCommand implements CommandExecutor {
    private static final int ENDER_SIZE = 27;
    private final OfflineInventoryStorage storage;
    private final MessageService messages;

    public EchestseeCommand(OfflineInventoryStorage storage, MessageService messages) {
        this.storage = storage;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }
        if (!player.hasPermission("tbfmp.admin.echestsee")) {
            messages.sendMessage(player, messages.getMessage("messages.no-permission"));
            return true;
        }
        if (args.length < 1) {
            messages.sendMessage(player, messages.getMessage("messages.echestsee-usage"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target != null) {
            player.openInventory(target.getEnderChest());
            return true;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!offlinePlayer.hasPlayedBefore()) {
            messages.sendMessage(player, messages.getMessage("messages.player-not-found"));
            return true;
        }
        String targetName = offlinePlayer.getName() != null ? offlinePlayer.getName() : args[0];
        OfflineInventoryHolder holder = new OfflineInventoryHolder(offlinePlayer.getUniqueId(), true);
        Inventory inventory = Bukkit.createInventory(holder, ENDER_SIZE, "Echest: " + targetName);
        holder.setInventory(inventory);
        ItemStack[] stored = storage.getEnderChest(offlinePlayer.getUniqueId(), ENDER_SIZE);
        for (int i = 0; i < stored.length && i < ENDER_SIZE; i++) {
            inventory.setItem(i, stored[i]);
        }
        player.openInventory(inventory);
        return true;
    }
}
