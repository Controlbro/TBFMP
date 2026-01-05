package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BankCommand implements CommandExecutor {
    public static final String BANK_TITLE = ChatColor.DARK_GREEN + "Bank";
    public static final int PRICE = 2000;
    private final BalanceStorage balanceStorage;
    private final MessageService messages;

    public BankCommand(BalanceStorage balanceStorage, MessageService messages) {
        this.balanceStorage = balanceStorage;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }

        Inventory inventory = Bukkit.createInventory(player, 9, BANK_TITLE);
        inventory.setItem(3, createItem(Material.DIAMOND, ChatColor.GREEN + "Buy diamond for $" + PRICE));
        inventory.setItem(5, createItem(Material.EMERALD, ChatColor.GOLD + "Sell diamond for $" + PRICE));
        player.openInventory(inventory);
        messages.sendMessage(player, messages.getMessage("messages.bank-opened"));
        return true;
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
