package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.TBFMPPlugin;
import com.tbfmc.tbfmp.event.MiningEventService;
import com.tbfmc.tbfmp.settings.KeepInventorySettingsStorage;
import com.tbfmc.tbfmp.settings.PvpSettingsStorage;
import com.tbfmc.tbfmp.util.FirstJoinOnboardingService;
import com.tbfmc.tbfmp.util.HelpBookService;
import com.tbfmc.tbfmp.util.SpawnService;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TbfmcCommand implements CommandExecutor {
    private final TBFMPPlugin plugin;
    private final MessageService messages;
    private final SpawnService spawnService;
    private final KeepInventorySettingsStorage keepInventorySettingsStorage;
    private final PvpSettingsStorage pvpSettingsStorage;
    private final MiningEventService miningEventService;
    private final HelpBookService helpBookService;
    private final FirstJoinOnboardingService onboardingService;

    public TbfmcCommand(TBFMPPlugin plugin, MessageService messages, SpawnService spawnService,
                        KeepInventorySettingsStorage keepInventorySettingsStorage,
                        PvpSettingsStorage pvpSettingsStorage,
                        MiningEventService miningEventService) {
        this.plugin = plugin;
        this.messages = messages;
        this.spawnService = spawnService;
        this.keepInventorySettingsStorage = keepInventorySettingsStorage;
        this.pvpSettingsStorage = pvpSettingsStorage;
        this.miningEventService = miningEventService;
        this.helpBookService = new HelpBookService(plugin, messages);
        this.onboardingService = new FirstJoinOnboardingService();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("oakglowutil.admin.reload")) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            plugin.reloadPluginConfig();
            messages.sendMessage(sender, messages.getMessage("messages.reload-complete"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("setspawn")) {
            if (!sender.hasPermission("oakglowutil.admin.setspawn")) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, messages.getMessage("messages.players-only"));
                return true;
            }
            Location location = player.getLocation();
            spawnService.setSpawnLocation(location);
            messages.sendMessage(sender, messages.getMessage("messages.setspawn-success"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("dragondropset")) {
            if (!sender.hasPermission("oakglowutil.admin.dragondropset")) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, messages.getMessage("messages.players-only"));
                return true;
            }
            Location location = player.getLocation();
            plugin.getConfig().set("dragon-drops.enabled", true);
            plugin.getConfig().set("dragon-drops.location.world", location.getWorld().getName());
            plugin.getConfig().set("dragon-drops.location.x", location.getX());
            plugin.getConfig().set("dragon-drops.location.y", location.getY());
            plugin.getConfig().set("dragon-drops.location.z", location.getZ());
            plugin.getConfig().set("dragon-drops.location.yaw", location.getYaw());
            plugin.getConfig().set("dragon-drops.location.pitch", location.getPitch());
            plugin.saveConfig();
            messages.sendMessage(sender, messages.getMessage("messages.dragondrop-set"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("convert")) {
            if (!sender.isOp()) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            boolean converted = plugin.convertLegacyData();
            if (converted) {
                messages.sendMessage(sender, messages.getMessage("messages.convert-complete"));
            }
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("keepinvtoggle")) {
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, messages.getMessage("messages.players-only"));
                return true;
            }
            boolean enabled = keepInventorySettingsStorage.toggle(player.getUniqueId());
            messages.sendMessage(player, messages.getMessage(
                    enabled ? "messages.keep-inventory-enabled" : "messages.keep-inventory-disabled"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("pvptoggle")) {
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, messages.getMessage("messages.players-only"));
                return true;
            }
            boolean enabled = pvpSettingsStorage.toggle(player.getUniqueId());
            messages.sendMessage(player, messages.getMessage(
                    enabled ? "messages.pvp-toggle-on" : "messages.pvp-toggle-off"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("toggleleaderboard")) {
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, messages.getMessage("messages.players-only"));
                return true;
            }
            boolean enabled = miningEventService.toggleLeaderboard(player);
            messages.sendMessage(player, messages.getMessage(
                    enabled ? "messages.event-leaderboard-enabled" : "messages.event-leaderboard-disabled"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("resetevent")) {
            if (!sender.isOp()) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            miningEventService.resetEvent();
            messages.sendMessage(sender, messages.getMessage("messages.event-reset"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("givehelpbook")) {
            if (!sender.hasPermission("oakglowutil.admin.givehelpbook")) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            if (args.length < 2) {
                messages.sendMessage(sender, messages.getMessage("messages.givehelpbook-usage"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                messages.sendMessage(sender, messages.getMessage("messages.player-not-found"));
                return true;
            }
            org.bukkit.inventory.ItemStack book = helpBookService.createHelpBook(target);
            if (book == null) {
                messages.sendMessage(sender, messages.getMessage("messages.helpbook-failed"));
                return true;
            }
            java.util.Map<Integer, org.bukkit.inventory.ItemStack> leftover = target.getInventory().addItem(book);
            if (!leftover.isEmpty()) {
                for (org.bukkit.inventory.ItemStack item : leftover.values()) {
                    target.getWorld().dropItemNaturally(target.getLocation(), item);
                }
            }
            messages.sendMessage(sender, messages.getMessage("messages.helpbook-given")
                    .replace("{player}", target.getName()));
            messages.sendMessage(target, messages.getMessage("messages.helpbook-received"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("sendonboarding")) {
            if (!sender.hasPermission("oakglowutil.admin.sendonboarding")) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            if (args.length < 2) {
                messages.sendMessage(sender, messages.getMessage("messages.sendonboarding-usage"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                messages.sendMessage(sender, messages.getMessage("messages.player-not-found"));
                return true;
            }
            onboardingService.send(target);
            messages.sendMessage(sender, messages.getMessage("messages.sendonboarding-sent")
                    .replace("{player}", target.getName()));
            return true;
        }

        messages.sendMessage(sender, messages.getMessage("messages.oakglow-usage"));
        return true;
    }
}
