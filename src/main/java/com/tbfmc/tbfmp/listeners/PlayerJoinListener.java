package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.mail.MailStorage;
import com.tbfmc.tbfmp.mallwarp.MallWarpManager;
import com.tbfmc.tbfmp.nickname.NicknameStorage;
import com.tbfmc.tbfmp.util.FirstJoinOnboardingService;
import com.tbfmc.tbfmp.util.HelpBookService;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import com.tbfmc.tbfmp.TBFMPPlugin;

import java.util.Map;

public class PlayerJoinListener implements Listener {
    private final TBFMPPlugin plugin;
    private final MessageService messages;
    private final MallWarpManager mallWarpManager;
    private final MailStorage mailStorage;
    private final NicknameStorage nicknameStorage;
    private final HelpBookService helpBookService;
    private final FirstJoinOnboardingService onboardingService;

    public PlayerJoinListener(TBFMPPlugin plugin, MessageService messages, MallWarpManager mallWarpManager,
                              MailStorage mailStorage, NicknameStorage nicknameStorage) {
        this.plugin = plugin;
        this.messages = messages;
        this.mallWarpManager = mallWarpManager;
        this.mailStorage = mailStorage;
        this.nicknameStorage = nicknameStorage;
        this.helpBookService = new HelpBookService(plugin, messages);
        this.onboardingService = new FirstJoinOnboardingService();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean firstJoin = !player.hasPlayedBefore();
        String joinKey = firstJoin ? "messages.first-join" : "messages.join";
        String joinMessage = messages.getMessage(joinKey).replace("{player}", player.getName());
        if (!joinMessage.isBlank()) {
            event.setJoinMessage(joinMessage);
        }
        messages.sendMessage(player, messages.getMessage("messages.motd"));
        applyNickname(player);
        notifyMallWarpRejoin(player);
        notifyMail(player);
        if (firstJoin) {
            onboardingService.send(player);
        }
        maybeGiveJoinBook(player, firstJoin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String quitMessage = messages.getMessage("messages.leave").replace("{player}", player.getName());
        if (!quitMessage.isBlank()) {
            event.setQuitMessage(quitMessage);
        }
        plugin.flushMysqlAsync();
    }

    private void maybeGiveJoinBook(Player player, boolean firstJoin) {
        if (!helpBookService.isEnabled()) {
            return;
        }
        if (helpBookService.isOnlyFirstJoin() && !firstJoin) {
            return;
        }
        ItemStack book = helpBookService.createHelpBook(player);
        if (book == null) {
            return;
        }
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(book);
        if (!leftover.isEmpty()) {
            for (ItemStack item : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

    private void notifyMallWarpRejoin(Player player) {
        if (mallWarpManager == null) {
            return;
        }
        if (!mallWarpManager.isMallPlayer(player) || !mallWarpManager.hasBackLocation(player)) {
            return;
        }
        String message = messages.getMessage("messages.mallwarp-rejoin");
        if (!message.isBlank()) {
            messages.sendMessage(player, message);
        }
    }

    private void notifyMail(Player player) {
        if (mailStorage == null) {
            return;
        }
        int count = mailStorage.getMailCount(player.getUniqueId());
        if (count <= 0) {
            return;
        }
        String message = messages.getMessage("messages.mail-notify")
                .replace("{count}", String.valueOf(count));
        if (!message.isBlank()) {
            messages.sendMessage(player, message);
        }
    }

    private void applyNickname(Player player) {
        if (nicknameStorage == null) {
            return;
        }
        String nickname = nicknameStorage.getNickname(player.getUniqueId());
        if (nickname == null || nickname.isBlank()) {
            return;
        }
        player.setDisplayName(nickname);
    }

}
