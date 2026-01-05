package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.commands.BankCommand;
import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BankListener implements Listener {
    private final BalanceStorage balanceStorage;
    private final MessageService messages;

    public BankListener(BalanceStorage balanceStorage, MessageService messages) {
        this.balanceStorage = balanceStorage;
        this.messages = messages;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory viewInventory = event.getView().getTopInventory();
        if (viewInventory == null) {
            return;
        }
        if (!event.getView().getTitle().equals(BankCommand.BANK_TITLE)) {
            return;
        }
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        if (clicked.getType() == Material.DIAMOND) {
            handleBuy(player);
        } else if (clicked.getType() == Material.EMERALD) {
            handleSell(player);
        }
    }

    private void handleBuy(Player player) {
        double balance = balanceStorage.getBalance(player.getUniqueId());
        if (balance < BankCommand.PRICE) {
            messages.sendMessage(player, messages.getMessage("messages.insufficient-funds"));
            return;
        }
        if (!hasSpaceFor(player, new ItemStack(Material.DIAMOND))) {
            messages.sendMessage(player, messages.getMessage("messages.bank-no-space"));
            return;
        }
        balanceStorage.subtractBalance(player.getUniqueId(), BankCommand.PRICE);
        player.getInventory().addItem(new ItemStack(Material.DIAMOND));
        messages.sendMessage(player, messages.getMessage("messages.bank-buy-success"));
    }

    private void handleSell(Player player) {
        ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
        if (!player.getInventory().containsAtLeast(diamond, 1)) {
            messages.sendMessage(player, messages.getMessage("messages.bank-no-diamonds"));
            return;
        }
        player.getInventory().removeItem(diamond);
        balanceStorage.addBalance(player.getUniqueId(), BankCommand.PRICE);
        messages.sendMessage(player, messages.getMessage("messages.bank-sell-success"));
    }

    private boolean hasSpaceFor(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() != -1) {
            return true;
        }
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null || stack.getType() != item.getType()) {
                continue;
            }
            if (stack.getAmount() < stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }
}
