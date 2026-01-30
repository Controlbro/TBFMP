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
        if (clicked.getType() == Material.DIAMOND_BLOCK) {
            handleBuyAll(player);
        } else if (clicked.getType() == Material.DIAMOND) {
            handleBuy(player);
        } else if (clicked.getType() == Material.EMERALD) {
            handleSell(player);
        } else if (clicked.getType() == Material.EMERALD_BLOCK) {
            handleSellAll(player);
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

    private void handleBuyAll(Player player) {
        double balance = balanceStorage.getBalance(player.getUniqueId());
        int maxPurchasable = (int) Math.floor(balance / BankCommand.PRICE);
        if (maxPurchasable <= 0) {
            messages.sendMessage(player, messages.getMessage("messages.insufficient-funds"));
            return;
        }
        int capacity = getInventoryCapacity(player, Material.DIAMOND);
        if (capacity <= 0) {
            messages.sendMessage(player, messages.getMessage("messages.bank-no-space"));
            return;
        }
        int purchaseCount = Math.min(maxPurchasable, capacity);
        if (purchaseCount <= 0) {
            messages.sendMessage(player, messages.getMessage("messages.bank-no-space"));
            return;
        }
        balanceStorage.subtractBalance(player.getUniqueId(), BankCommand.PRICE * purchaseCount);
        addItems(player, new ItemStack(Material.DIAMOND), purchaseCount);
        String message = messages.getMessage("messages.bank-buy-all-success")
                .replace("{amount}", String.valueOf(purchaseCount))
                .replace("{total}", String.valueOf(BankCommand.PRICE * purchaseCount));
        messages.sendMessage(player, message);
    }

    private void handleSellAll(Player player) {
        int diamondCount = countItems(player, Material.DIAMOND);
        if (diamondCount <= 0) {
            messages.sendMessage(player, messages.getMessage("messages.bank-no-diamonds"));
            return;
        }
        removeItems(player, Material.DIAMOND, diamondCount);
        balanceStorage.addBalance(player.getUniqueId(), BankCommand.PRICE * diamondCount);
        String message = messages.getMessage("messages.bank-sell-all-success")
                .replace("{amount}", String.valueOf(diamondCount))
                .replace("{total}", String.valueOf(BankCommand.PRICE * diamondCount));
        messages.sendMessage(player, message);
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

    private int getInventoryCapacity(Player player, Material material) {
        int capacity = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null) {
                capacity += material.getMaxStackSize();
                continue;
            }
            if (stack.getType() != material) {
                continue;
            }
            capacity += stack.getMaxStackSize() - stack.getAmount();
        }
        return capacity;
    }

    private int countItems(Player player, Material material) {
        int count = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null || stack.getType() != material) {
                continue;
            }
            count += stack.getAmount();
        }
        return count;
    }

    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (stack == null || stack.getType() != material) {
                continue;
            }
            int removed = Math.min(stack.getAmount(), remaining);
            stack.setAmount(stack.getAmount() - removed);
            remaining -= removed;
            if (stack.getAmount() <= 0) {
                contents[i] = null;
            }
        }
        player.getInventory().setContents(contents);
    }

    private void addItems(Player player, ItemStack baseItem, int amount) {
        int remaining = amount;
        int maxStack = baseItem.getMaxStackSize();
        while (remaining > 0) {
            int stackAmount = Math.min(remaining, maxStack);
            ItemStack stack = baseItem.clone();
            stack.setAmount(stackAmount);
            player.getInventory().addItem(stack);
            remaining -= stackAmount;
        }
    }
}
