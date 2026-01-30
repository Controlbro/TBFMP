package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.mail.MailMessage;
import com.tbfmc.tbfmp.mail.MailStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MailCommand implements CommandExecutor {
    private final MailStorage mailStorage;
    private final MessageService messages;

    public MailCommand(MailStorage mailStorage, MessageService messages) {
        this.mailStorage = mailStorage;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messages.sendMessage(sender, messages.getMessage("messages.mail-usage"));
            return true;
        }

        String action = args[0].toLowerCase();
        switch (action) {
            case "read" -> handleRead(sender);
            case "send" -> handleSend(sender, args);
            default -> messages.sendMessage(sender, messages.getMessage("messages.mail-usage"));
        }
        return true;
    }

    private void handleRead(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return;
        }
        UUID playerId = player.getUniqueId();
        List<MailMessage> mail = mailStorage.getMail(playerId);
        if (mail.isEmpty()) {
            messages.sendMessage(player, messages.getMessage("messages.mail-read-empty"));
            return;
        }
        messages.sendMessage(player, messages.getMessage("messages.mail-read-header"));
        String lineFormat = messages.getMessage("messages.mail-item");
        for (MailMessage message : mail) {
            messages.sendMessage(player, lineFormat
                    .replace("{player}", message.sender())
                    .replace("{message}", message.message()));
        }
        mailStorage.clearMail(playerId);
    }

    private void handleSend(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return;
        }
        if (args.length < 3) {
            messages.sendMessage(player, messages.getMessage("messages.mail-send-usage"));
            return;
        }
        String targetName = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            messages.sendMessage(player, messages.getMessage("messages.player-not-found"));
            return;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            messages.sendMessage(player, messages.getMessage("messages.mail-self"));
            return;
        }
        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        mailStorage.addMail(target.getUniqueId(), new MailMessage(player.getName(), message));
        String resolvedTargetName = target.getName() == null ? targetName : target.getName();
        messages.sendMessage(player, messages.getMessage("messages.mail-sent")
                .replace("{player}", resolvedTargetName));
        if (target.isOnline() && target.getPlayer() != null) {
            int count = mailStorage.getMailCount(target.getUniqueId());
            String notification = messages.getMessage("messages.mail-notify")
                    .replace("{count}", String.valueOf(count));
            messages.sendMessage(target.getPlayer(), notification);
        }
    }
}
