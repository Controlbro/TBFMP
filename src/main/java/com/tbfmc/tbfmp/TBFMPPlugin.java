package com.tbfmc.tbfmp;

import com.tbfmc.tbfmp.chat.ChatNotificationTask;
import com.tbfmc.tbfmp.commands.BalanceCommand;
import com.tbfmc.tbfmp.commands.BalanceTopCommand;
import com.tbfmc.tbfmp.commands.BankCommand;
import com.tbfmc.tbfmp.commands.ConfirmCommand;
import com.tbfmc.tbfmp.commands.EcoCommand;
import com.tbfmc.tbfmp.commands.FlyCommand;
import com.tbfmc.tbfmp.commands.HugCommand;
import com.tbfmc.tbfmp.commands.InfoCommand;
import com.tbfmc.tbfmp.commands.PayCommand;
import com.tbfmc.tbfmp.commands.PayToggleCommand;
import com.tbfmc.tbfmp.commands.ResetRtpCommand;
import com.tbfmc.tbfmp.commands.RtpCommand;
import com.tbfmc.tbfmp.commands.SitCommand;
import com.tbfmc.tbfmp.commands.TbfmcCommand;
import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.economy.PaySettingsStorage;
import com.tbfmc.tbfmp.economy.VaultEconomyProvider;
import com.tbfmc.tbfmp.listeners.BankListener;
import com.tbfmc.tbfmp.listeners.PlayerJoinListener;
import com.tbfmc.tbfmp.listeners.SitListener;
import com.tbfmc.tbfmp.rtp.RtpManager;
import com.tbfmc.tbfmp.sit.SitSettingsStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class TBFMPPlugin extends JavaPlugin {
    private BalanceStorage balanceStorage;
    private PaySettingsStorage paySettingsStorage;
    private SitSettingsStorage sitSettingsStorage;
    private RtpManager rtpManager;
    private MessageService messageService;
    private ChatNotificationTask chatNotificationTask;
    private HugCommand hugCommand;
    private SitCommand sitCommand;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.messageService = new MessageService(this);
        this.balanceStorage = new BalanceStorage(this);
        this.paySettingsStorage = new PaySettingsStorage(this);
        this.sitSettingsStorage = new SitSettingsStorage(this);
        this.rtpManager = new RtpManager(this, messageService);
        this.hugCommand = new HugCommand(this, messageService);
        this.sitCommand = new SitCommand(sitSettingsStorage, messageService);

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
        if (paySettingsStorage != null) {
            paySettingsStorage.save();
        }
        if (sitSettingsStorage != null) {
            sitSettingsStorage.save();
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
        getCommand("pay").setExecutor(new PayCommand(balanceStorage, paySettingsStorage, messageService));
        getCommand("paytoggle").setExecutor(new PayToggleCommand(paySettingsStorage, messageService));
        getCommand("vote").setExecutor(new InfoCommand(messageService, "messages.vote"));
        getCommand("web").setExecutor(new InfoCommand(messageService, "messages.web"));
        getCommand("discord").setExecutor(new InfoCommand(messageService, "messages.discord"));
        getCommand("shoptut").setExecutor(new InfoCommand(messageService, "messages.shoptut"));
        getCommand("hug").setExecutor(hugCommand);
        getCommand("tbfmc").setExecutor(new TbfmcCommand(this, messageService));
        getCommand("fly").setExecutor(new FlyCommand(messageService));
        getCommand("sit").setExecutor(sitCommand);
        getCommand("sit").setTabCompleter(sitCommand);
        getCommand("bank").setExecutor(new BankCommand(balanceStorage, messageService));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(messageService), this);
        Bukkit.getPluginManager().registerEvents(new SitListener(sitSettingsStorage, messageService), this);
        Bukkit.getPluginManager().registerEvents(new BankListener(balanceStorage, messageService), this);
    }

    private void startChatNotifications() {
        if (getConfig().getBoolean("chat-notifications.enabled", true)) {
            chatNotificationTask = new ChatNotificationTask(this, messageService);
            chatNotificationTask.start();
        }
    }

    public void reloadPluginConfig() {
        reloadConfig();
        this.messageService = new MessageService(this);
        if (chatNotificationTask != null) {
            chatNotificationTask.stop();
            chatNotificationTask = null;
        }
        if (getConfig().getBoolean("chat-notifications.enabled", true)) {
            chatNotificationTask = new ChatNotificationTask(this, messageService);
            chatNotificationTask.start();
        }
        this.hugCommand = new HugCommand(this, messageService);
        this.sitCommand = new SitCommand(sitSettingsStorage, messageService);
        registerCommands();
    }
}
