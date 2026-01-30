package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.afk.AfkManager;
import com.tbfmc.tbfmp.nickname.NicknameStorage;
import com.tbfmc.tbfmp.tablist.TabListService;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnnickCommand implements CommandExecutor {
    private final MessageService messages;
    private final TabListService tabListService;
    private final AfkManager afkManager;
    private final NicknameStorage nicknameStorage;

    public UnnickCommand(MessageService messages, TabListService tabListService, AfkManager afkManager,
                         NicknameStorage nicknameStorage) {
        this.messages = messages;
        this.tabListService = tabListService;
        this.afkManager = afkManager;
        this.nicknameStorage = nicknameStorage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }

        if (!player.hasPermission("oakglowutil.nick")) {
            messages.sendMessage(player, messages.getMessage("messages.no-permission"));
            return true;
        }

        if (args.length > 0) {
            messages.sendMessage(player, messages.getMessage("messages.unnick-usage"));
            return true;
        }

        player.setDisplayName(player.getName());
        nicknameStorage.setNickname(player.getUniqueId(), null);
        tabListService.updatePlayer(player, afkManager.isAfk(player.getUniqueId()));
        messages.sendMessage(player, messages.getMessage("messages.nick-cleared"));
        return true;
    }
}
