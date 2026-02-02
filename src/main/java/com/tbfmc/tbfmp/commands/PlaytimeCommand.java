package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.playtime.PlaytimeRewardsService;
import com.tbfmc.tbfmp.playtime.PlaytimeTracker;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaytimeCommand implements CommandExecutor {
    private final PlaytimeTracker tracker;
    private final PlaytimeRewardsService rewardsService;
    private final MessageService messages;

    public PlaytimeCommand(PlaytimeTracker tracker, PlaytimeRewardsService rewardsService, MessageService messages) {
        this.tracker = tracker;
        this.rewardsService = rewardsService;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, messages.getMessage("messages.players-only"));
                return true;
            }
            long playtime = tracker.getTotalPlaytimeSeconds(player.getUniqueId());
            String message = messages.getMessage("messages.playtime-total")
                    .replace("{player}", player.getName())
                    .replace("{seconds}", String.valueOf(playtime));
            messages.sendMessage(player, message);
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("claim")) {
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, messages.getMessage("messages.players-only"));
                return true;
            }
            player.openInventory(rewardsService.createMenu(player));
            messages.sendMessage(player, messages.getMessage("messages.playtime-claim-opened"));
            return true;
        }
        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null || (target.getName() == null && !target.hasPlayedBefore())) {
                messages.sendMessage(sender, messages.getMessage("messages.player-not-found"));
                return true;
            }
            if (sender instanceof Player player && !player.getName().equalsIgnoreCase(args[0])
                    && !sender.hasPermission("oakglowutil.playtime.others")) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            long playtime = tracker.getTotalPlaytimeSeconds(target.getUniqueId());
            String message = messages.getMessage("messages.playtime-total")
                    .replace("{player}", target.getName() == null ? args[0] : target.getName())
                    .replace("{seconds}", String.valueOf(playtime));
            messages.sendMessage(sender, message);
            return true;
        }
        messages.sendMessage(sender, messages.getMessage("messages.playtime-usage"));
        return true;
    }
}
