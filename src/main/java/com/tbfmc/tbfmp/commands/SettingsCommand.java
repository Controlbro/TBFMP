package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.settings.SettingsMenuService;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand implements CommandExecutor {
    private final SettingsMenuService menuService;
    private final MessageService messages;

    public SettingsCommand(SettingsMenuService menuService, MessageService messages) {
        this.menuService = menuService;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }
        player.openInventory(menuService.createMenu(player));
        return true;
    }
}
