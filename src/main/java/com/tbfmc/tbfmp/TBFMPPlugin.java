package com.tbfmc.tbfmp;

import com.tbfmc.tbfmp.chat.ChatNotificationTask;
import com.tbfmc.tbfmp.chat.ChatNotificationSettingsStorage;
import com.tbfmc.tbfmp.chat.TagConfig;
import com.tbfmc.tbfmp.chat.TagMenuConfig;
import com.tbfmc.tbfmp.chat.TagMenuService;
import com.tbfmc.tbfmp.chat.TagSelectionStorage;
import com.tbfmc.tbfmp.commands.AfkCommand;
import com.tbfmc.tbfmp.commands.BalanceCommand;
import com.tbfmc.tbfmp.commands.BalanceTopCommand;
import com.tbfmc.tbfmp.commands.BankCommand;
import com.tbfmc.tbfmp.commands.ConfirmCommand;
import com.tbfmc.tbfmp.commands.EcoCommand;
import com.tbfmc.tbfmp.commands.CustomCommand;
import com.tbfmc.tbfmp.commands.EventCommand;
import com.tbfmc.tbfmp.commands.FlyCommand;
import com.tbfmc.tbfmp.commands.HugCommand;
import com.tbfmc.tbfmp.commands.InfoCommand;
import com.tbfmc.tbfmp.commands.InvseeCommand;
import com.tbfmc.tbfmp.commands.PayCommand;
import com.tbfmc.tbfmp.commands.PayToggleCommand;
import com.tbfmc.tbfmp.commands.EchestseeCommand;
import com.tbfmc.tbfmp.commands.ResetRtpCommand;
import com.tbfmc.tbfmp.commands.RtpCommand;
import com.tbfmc.tbfmp.commands.SettingsCommand;
import com.tbfmc.tbfmp.commands.SitCommand;
import com.tbfmc.tbfmp.commands.SitSettingCommand;
import com.tbfmc.tbfmp.commands.TagMenuCommand;
import com.tbfmc.tbfmp.commands.TbfmcCommand;
import com.tbfmc.tbfmp.afk.AfkManager;
import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.economy.PaySettingsStorage;
import com.tbfmc.tbfmp.economy.VaultEconomyProvider;
import com.tbfmc.tbfmp.event.EventSettingsStorage;
import com.tbfmc.tbfmp.event.MiningEventService;
import com.tbfmc.tbfmp.event.MiningEventStorage;
import com.tbfmc.tbfmp.listeners.AfkListener;
import com.tbfmc.tbfmp.listeners.BabyFaithListener;
import com.tbfmc.tbfmp.listeners.CritParticleListener;
import com.tbfmc.tbfmp.listeners.DeathParticleListener;
import com.tbfmc.tbfmp.listeners.MiningEventListener;
import com.tbfmc.tbfmp.listeners.MiningEventPlayerListener;
import com.tbfmc.tbfmp.listeners.SettingsMenuListener;
import com.tbfmc.tbfmp.listeners.BankListener;
import com.tbfmc.tbfmp.listeners.ChatFormatListener;
import com.tbfmc.tbfmp.listeners.DurabilityWarningListener;
import com.tbfmc.tbfmp.listeners.OfflineInventoryListener;
import com.tbfmc.tbfmp.listeners.PlayerJoinListener;
import com.tbfmc.tbfmp.listeners.SitDamageListener;
import com.tbfmc.tbfmp.listeners.SitListener;
import com.tbfmc.tbfmp.listeners.SpawnListener;
import com.tbfmc.tbfmp.listeners.TagMenuListener;
import com.tbfmc.tbfmp.listeners.TreeFellerListener;
import com.tbfmc.tbfmp.rtp.RtpManager;
import com.tbfmc.tbfmp.settings.SettingsMenuConfig;
import com.tbfmc.tbfmp.settings.SettingsMenuService;
import com.tbfmc.tbfmp.sit.SitManager;
import com.tbfmc.tbfmp.sit.SitSettingsStorage;
import com.tbfmc.tbfmp.tablist.TabListService;
import com.tbfmc.tbfmp.util.ConfigUpdater;
import com.tbfmc.tbfmp.util.CustomConfig;
import com.tbfmc.tbfmp.util.MessageService;
import com.tbfmc.tbfmp.util.OfflineInventoryStorage;
import com.tbfmc.tbfmp.util.SpawnService;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class TBFMPPlugin extends JavaPlugin {
    private BalanceStorage balanceStorage;
    private PaySettingsStorage paySettingsStorage;
    private SitSettingsStorage sitSettingsStorage;
    private ChatNotificationSettingsStorage chatNotificationSettingsStorage;
    private EventSettingsStorage eventSettingsStorage;
    private SitManager sitManager;
    private RtpManager rtpManager;
    private MessageService messageService;
    private ChatNotificationTask chatNotificationTask;
    private HugCommand hugCommand;
    private SitCommand sitCommand;
    private SitSettingCommand sitSettingCommand;
    private OfflineInventoryStorage offlineInventoryStorage;
    private TagConfig tagConfig;
    private TagMenuConfig tagMenuConfig;
    private TagSelectionStorage tagSelectionStorage;
    private TagMenuService tagMenuService;
    private SettingsMenuConfig settingsMenuConfig;
    private SettingsMenuService settingsMenuService;
    private Chat vaultChat;
    private TabListService tabListService;
    private AfkManager afkManager;
    private BukkitTask afkTask;
    private SpawnService spawnService;
    private CustomConfig customConfig;
    private MiningEventStorage miningEventStorage;
    private MiningEventService miningEventService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigUpdater.updateConfig(this, "config.yml");
        ConfigUpdater.updateConfig(this, "settings-menu.yml");
        reloadConfig();
        saveResource("tags.yml", false);
        saveResource("tag-menu.yml", false);
        saveResource("CustomConfig.yml", false);
        this.customConfig = new CustomConfig(this);
        this.messageService = new MessageService(this);
        this.balanceStorage = new BalanceStorage(this);
        this.paySettingsStorage = new PaySettingsStorage(this);
        this.sitSettingsStorage = new SitSettingsStorage(this);
        this.chatNotificationSettingsStorage = new ChatNotificationSettingsStorage(this);
        this.eventSettingsStorage = new EventSettingsStorage(this);
        this.miningEventStorage = new MiningEventStorage(this);
        this.miningEventService = new MiningEventService(miningEventStorage, eventSettingsStorage);
        this.sitManager = new SitManager(messageService, this);
        this.offlineInventoryStorage = new OfflineInventoryStorage(this);
        this.tagConfig = new TagConfig(this);
        this.tagMenuConfig = new TagMenuConfig(this);
        this.tagSelectionStorage = new TagSelectionStorage(this);
        this.settingsMenuConfig = new SettingsMenuConfig(this);
        this.rtpManager = new RtpManager(this, messageService);
        this.hugCommand = new HugCommand(this, messageService);
        this.sitCommand = new SitCommand(sitManager, messageService);
        this.sitSettingCommand = new SitSettingCommand(sitSettingsStorage, messageService);
        this.vaultChat = getServer().getServicesManager().getRegistration(Chat.class) != null
                ? getServer().getServicesManager().getRegistration(Chat.class).getProvider()
                : null;
        this.tabListService = new TabListService(this, messageService, vaultChat);
        long afkTimeoutSeconds = getConfig().getLong("afk.timeout-seconds", 300L);
        this.afkManager = new AfkManager(afkTimeoutSeconds * 1000L, messageService, tabListService);

        NamespacedKey tagKey = new NamespacedKey(this, "tag-id");
        NamespacedKey navigationKey = new NamespacedKey(this, "tag-menu-page");
        this.tagMenuService = new TagMenuService(tagConfig, tagMenuConfig, tagSelectionStorage,
                messageService, vaultChat, tagKey, navigationKey);
        this.settingsMenuService = new SettingsMenuService(settingsMenuConfig, paySettingsStorage, sitSettingsStorage,
                chatNotificationSettingsStorage, messageService, new NamespacedKey(this, "settings-option"));
        this.spawnService = new SpawnService(this);

        VaultEconomyProvider economyProvider = new VaultEconomyProvider(balanceStorage);
        Bukkit.getServicesManager().register(net.milkbowl.vault.economy.Economy.class, economyProvider, this, ServicePriority.Normal);

        registerCommands();
        registerListeners();
        miningEventService.applyToOnlinePlayers();
        startChatNotifications();
        startAfkTask();
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
        if (chatNotificationSettingsStorage != null) {
            chatNotificationSettingsStorage.save();
        }
        if (eventSettingsStorage != null) {
            eventSettingsStorage.save();
        }
        if (miningEventStorage != null) {
            miningEventStorage.save();
        }
        if (tagSelectionStorage != null) {
            tagSelectionStorage.save();
        }
        if (rtpManager != null) {
            rtpManager.save();
        }
        if (afkTask != null) {
            afkTask.cancel();
            afkTask = null;
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
        getCommand("tbfmc").setExecutor(new TbfmcCommand(this, messageService, spawnService));
        getCommand("fly").setExecutor(new FlyCommand(messageService));
        getCommand("sit").setExecutor(sitCommand);
        getCommand("sitsetting").setExecutor(sitSettingCommand);
        getCommand("sitsetting").setTabCompleter(sitSettingCommand);
        getCommand("bank").setExecutor(new BankCommand(balanceStorage, messageService));
        getCommand("invsee").setExecutor(new InvseeCommand(offlineInventoryStorage, messageService));
        getCommand("echestsee").setExecutor(new EchestseeCommand(offlineInventoryStorage, messageService));
        getCommand("tags").setExecutor(new TagMenuCommand(tagMenuService, messageService,
                getConfig().getString("chat.format", "{prefix}{name}&r %tag% &7>> {message-color}{message}")));
        getCommand("settings").setExecutor(new SettingsCommand(settingsMenuService, messageService));
        getCommand("afk").setExecutor(new AfkCommand(afkManager, messageService));
        getCommand("custom").setExecutor(new CustomCommand(this, messageService));
        getCommand("event").setExecutor(new EventCommand(miningEventService, messageService));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(messageService), this);
        Bukkit.getPluginManager().registerEvents(new AfkListener(afkManager), this);
        Bukkit.getPluginManager().registerEvents(new SitListener(sitSettingsStorage, sitManager), this);
        Bukkit.getPluginManager().registerEvents(new SitDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new BankListener(balanceStorage, messageService), this);
        Bukkit.getPluginManager().registerEvents(new OfflineInventoryListener(offlineInventoryStorage), this);
        Bukkit.getPluginManager().registerEvents(new TagMenuListener(tagConfig, tagSelectionStorage, tagMenuService,
                messageService, new NamespacedKey(this, "tag-id"),
                new NamespacedKey(this, "tag-menu-page"), messageService.colorize(tagMenuConfig.getTitle()),
                getConfig().getString("chat.format", "{prefix}{name}&r %tag% &7>> {message-color}{message}")), this);
        Bukkit.getPluginManager().registerEvents(new SettingsMenuListener(settingsMenuService, paySettingsStorage,
                sitSettingsStorage, chatNotificationSettingsStorage, messageService,
                new NamespacedKey(this, "settings-option")), this);
        Bukkit.getPluginManager().registerEvents(new ChatFormatListener(tagConfig, tagSelectionStorage, messageService,
                vaultChat, getConfig().getString("chat.format", "{prefix}{name}&r %tag% &7>> {message-color}{message}")), this);
        Bukkit.getPluginManager().registerEvents(new DurabilityWarningListener(messageService), this);
        Bukkit.getPluginManager().registerEvents(new TreeFellerListener(this, getConfig()), this);
        Bukkit.getPluginManager().registerEvents(new BabyFaithListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CritParticleListener(customConfig), this);
        Bukkit.getPluginManager().registerEvents(new DeathParticleListener(this, customConfig), this);
        Bukkit.getPluginManager().registerEvents(new SpawnListener(spawnService), this);
        Bukkit.getPluginManager().registerEvents(new MiningEventListener(miningEventService), this);
        Bukkit.getPluginManager().registerEvents(new MiningEventPlayerListener(miningEventService), this);
    }

    private void startChatNotifications() {
        if (getConfig().getBoolean("chat-notifications.enabled", true)) {
            chatNotificationTask = new ChatNotificationTask(this, messageService, chatNotificationSettingsStorage);
            chatNotificationTask.start();
        }
    }

    private void startAfkTask() {
        if (afkTask != null) {
            afkTask.cancel();
        }
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            afkManager.initialize(player);
        }
        afkTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            afkManager.checkAfk();
            tabListService.updateAll(afkManager::isAfk);
        }, 20L * 60L, 20L * 60L);
    }

    public void reloadPluginConfig() {
        ConfigUpdater.updateConfig(this, "config.yml");
        ConfigUpdater.updateConfig(this, "settings-menu.yml");
        reloadConfig();
        this.messageService = new MessageService(this);
        if (chatNotificationTask != null) {
            chatNotificationTask.stop();
            chatNotificationTask = null;
        }
        if (getConfig().getBoolean("chat-notifications.enabled", true)) {
            chatNotificationTask = new ChatNotificationTask(this, messageService, chatNotificationSettingsStorage);
            chatNotificationTask.start();
        }
        this.hugCommand = new HugCommand(this, messageService);
        this.sitCommand = new SitCommand(sitManager, messageService);
        this.sitSettingCommand = new SitSettingCommand(sitSettingsStorage, messageService);
        this.tagConfig = new TagConfig(this);
        this.tagMenuConfig = new TagMenuConfig(this);
        this.tagSelectionStorage = new TagSelectionStorage(this);
        this.settingsMenuConfig = new SettingsMenuConfig(this);
        this.vaultChat = getServer().getServicesManager().getRegistration(Chat.class) != null
                ? getServer().getServicesManager().getRegistration(Chat.class).getProvider()
                : null;
        long afkTimeoutSeconds = getConfig().getLong("afk.timeout-seconds", 300L);
        if (this.tabListService == null) {
            this.tabListService = new TabListService(this, messageService, vaultChat);
        } else {
            this.tabListService.updateServices(messageService, vaultChat);
        }
        if (this.afkManager == null) {
            this.afkManager = new AfkManager(afkTimeoutSeconds * 1000L, messageService, tabListService);
        } else {
            this.afkManager.updateSettings(afkTimeoutSeconds * 1000L, messageService, tabListService);
        }
        this.tagMenuService = new TagMenuService(tagConfig, tagMenuConfig, tagSelectionStorage,
                messageService, vaultChat, new NamespacedKey(this, "tag-id"), new NamespacedKey(this, "tag-menu-page"));
        this.settingsMenuService = new SettingsMenuService(settingsMenuConfig, paySettingsStorage, sitSettingsStorage,
                chatNotificationSettingsStorage, messageService, new NamespacedKey(this, "settings-option"));
        registerCommands();
        startAfkTask();
    }

    public void reloadCustomConfig() {
        if (customConfig != null) {
            customConfig.reload();
        }
    }
}
