package com.tbfmc.tbfmp;

import com.tbfmc.tbfmp.chat.ChatNotificationTask;
import com.tbfmc.tbfmp.commands.BalanceCommand;
import com.tbfmc.tbfmp.commands.BalanceTopCommand;
import com.tbfmc.tbfmp.commands.ConfirmCommand;
import com.tbfmc.tbfmp.commands.EcoCommand;
import com.tbfmc.tbfmp.commands.ResetRtpCommand;
import com.tbfmc.tbfmp.commands.RtpCommand;
import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.economy.VaultEconomyProvider;
import com.tbfmc.tbfmp.listeners.PlayerJoinListener;
import com.tbfmc.tbfmp.rtp.RtpManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class TBFMPPlugin extends JavaPlugin {
    private BalanceStorage balanceStorage;
    private RtpManager rtpManager;
    private MessageService messageService;
    private ChatNotificationTask chatNotificationTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.messageService = new MessageService(this);
        this.balanceStorage = new BalanceStorage(this);
        this.rtpManager = new RtpManager(this, messageService);

        VaultEconomyProvider economyProvider = new VaultEconomyProvider(balanceStorage);
        Bukkit.getServicesManager().register(net.milkbowl.vault.economy.Economy.class, economyProvider, this, ServicePriority.Normal);

        registerCommands();
        registerListeners();
        startChatNotifications();
    }

    @Override
    public void onDisable() {
        if (chatNotificationTask != null) {
            chatNotificationTask.stop();
        }
        if (balanceStorage != null) {
            balanceStorage.save();
        }
        if (rtpManager != null) {
            rtpManager.save();
        }
    }

    private void registerCommands() {
        getCommand("balance").setExecutor(new BalanceCommand(balanceStorage, messageService));
        getCommand("balancetop").setExecutor(new BalanceTopCommand(balanceStorage, messageService));
        getCommand("eco").setExecutor(new EcoCommand(balanceStorage, messageService));
        getCommand("rtp").setExecutor(new RtpCommand(rtpManager, messageService));
        getCommand("confirm").setExecutor(new ConfirmCommand(rtpManager, messageService));
        getCommand("resetrtp").setExecutor(new ResetRtpCommand(rtpManager, messageService));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(messageService), this);
    }

    private void startChatNotifications() {
        if (getConfig().getBoolean("chat-notifications.enabled", true)) {
            chatNotificationTask = new ChatNotificationTask(this, messageService);
            chatNotificationTask.start();
        }
    }
}
