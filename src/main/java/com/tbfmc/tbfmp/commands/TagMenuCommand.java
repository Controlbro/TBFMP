package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.chat.TagMenuService;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TagMenuCommand implements CommandExecutor {
    private final TagMenuService menuService;
    private final MessageService messages;
    private final String chatFormat;

    public TagMenuCommand(TagMenuService menuService, MessageService messages, String chatFormat) {
        this.menuService = menuService;
        this.messages = messages;
        this.chatFormat = chatFormat;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }
        player.openInventory(menuService.createMenu(player, chatFormat));
        return true;
    }
}
