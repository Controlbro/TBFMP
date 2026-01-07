package com.tbfmc.tbfmp;

import com.tbfmc.tbfmp.chat.ChatNotificationTask;
import com.tbfmc.tbfmp.chat.ChatNotificationSettingsStorage;
import com.tbfmc.tbfmp.chat.TagConfig;
import com.tbfmc.tbfmp.chat.TagMenuConfig;
import com.tbfmc.tbfmp.chat.TagMenuService;
import com.tbfmc.tbfmp.chat.TagSelectionStorage;
import com.tbfmc.tbfmp.commands.BalanceCommand;
import com.tbfmc.tbfmp.commands.BalanceTopCommand;
import com.tbfmc.tbfmp.commands.BankCommand;
import com.tbfmc.tbfmp.commands.ConfirmCommand;
import com.tbfmc.tbfmp.commands.EcoCommand;
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
import com.tbfmc.tbfmp.commands.QuestMenuCommand;
import com.tbfmc.tbfmp.commands.QuestSummaryCommand;
import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.economy.PaySettingsStorage;
import com.tbfmc.tbfmp.economy.VaultEconomyProvider;
import com.tbfmc.tbfmp.listeners.SettingsMenuListener;
import com.tbfmc.tbfmp.listeners.BankListener;
import com.tbfmc.tbfmp.listeners.ChatFormatListener;
import com.tbfmc.tbfmp.listeners.DurabilityWarningListener;
import com.tbfmc.tbfmp.listeners.OfflineInventoryListener;
import com.tbfmc.tbfmp.listeners.PlayerJoinListener;
import com.tbfmc.tbfmp.listeners.QuestMenuListener;
import com.tbfmc.tbfmp.listeners.QuestProgressListener;
import com.tbfmc.tbfmp.listeners.SitListener;
import com.tbfmc.tbfmp.listeners.TagMenuListener;
import com.tbfmc.tbfmp.listeners.TreeFellerListener;
import com.tbfmc.tbfmp.quests.QuestAssignmentManager;
import com.tbfmc.tbfmp.quests.QuestConfig;
import com.tbfmc.tbfmp.quests.QuestProgressStorage;
import com.tbfmc.tbfmp.quests.QuestService;
import com.tbfmc.tbfmp.quests.QuestSummaryService;
import com.tbfmc.tbfmp.rtp.RtpManager;
import com.tbfmc.tbfmp.settings.SettingsMenuConfig;
import com.tbfmc.tbfmp.settings.SettingsMenuService;
import com.tbfmc.tbfmp.sit.SitManager;
import com.tbfmc.tbfmp.sit.SitSettingsStorage;
import com.tbfmc.tbfmp.util.ConfigUpdater;
import com.tbfmc.tbfmp.util.MessageService;
import com.tbfmc.tbfmp.util.OfflineInventoryStorage;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class TBFMPPlugin extends JavaPlugin {
    private BalanceStorage balanceStorage;
    private PaySettingsStorage paySettingsStorage;
    private SitSettingsStorage sitSettingsStorage;
    private ChatNotificationSettingsStorage chatNotificationSettingsStorage;
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
    private QuestProgressStorage questProgressStorage;
    private QuestAssignmentManager questAssignmentManager;
    private QuestService farmerQuestService;
    private QuestService mobQuestService;
    private QuestService minerQuestService;
    private QuestSummaryService questSummaryService;
    private Chat vaultChat;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigUpdater.updateConfig(this, "config.yml");
        ConfigUpdater.updateConfig(this, "settings-menu.yml");
        ConfigUpdater.updateConfig(this, "farmer-quests.yml");
        ConfigUpdater.updateConfig(this, "mob-quests.yml");
        ConfigUpdater.updateConfig(this, "miner-quests.yml");
        reloadConfig();
        saveResource("tags.yml", false);
        saveResource("tag-menu.yml", false);
        this.messageService = new MessageService(this);
        this.balanceStorage = new BalanceStorage(this);
        this.paySettingsStorage = new PaySettingsStorage(this);
        this.sitSettingsStorage = new SitSettingsStorage(this);
        this.chatNotificationSettingsStorage = new ChatNotificationSettingsStorage(this);
        this.sitManager = new SitManager(messageService, this);
        this.offlineInventoryStorage = new OfflineInventoryStorage(this);
        this.tagConfig = new TagConfig(this);
        this.tagMenuConfig = new TagMenuConfig(this);
        this.tagSelectionStorage = new TagSelectionStorage(this);
        this.settingsMenuConfig = new SettingsMenuConfig(this);
        this.questProgressStorage = new QuestProgressStorage(this);
        this.questAssignmentManager = new QuestAssignmentManager(questProgressStorage);
        this.rtpManager = new RtpManager(this, messageService);
        this.hugCommand = new HugCommand(this, messageService);
        this.sitCommand = new SitCommand(sitManager, messageService);
        this.sitSettingCommand = new SitSettingCommand(sitSettingsStorage, messageService);
        this.vaultChat = getServer().getServicesManager().getRegistration(Chat.class) != null
                ? getServer().getServicesManager().getRegistration(Chat.class).getProvider()
                : null;

        NamespacedKey tagKey = new NamespacedKey(this, "tag-id");
        NamespacedKey navigationKey = new NamespacedKey(this, "tag-menu-page");
        this.tagMenuService = new TagMenuService(tagConfig, tagMenuConfig, tagSelectionStorage,
                messageService, vaultChat, tagKey, navigationKey);
        this.settingsMenuService = new SettingsMenuService(settingsMenuConfig, paySettingsStorage, sitSettingsStorage,
                chatNotificationSettingsStorage, messageService, new NamespacedKey(this, "settings-option"));
        this.farmerQuestService = new QuestService(new QuestConfig(this, "farmer", "farmer-quests.yml"),
                questProgressStorage, balanceStorage, messageService, new NamespacedKey(this, "farmer-quest"),
                questAssignmentManager);
        this.mobQuestService = new QuestService(new QuestConfig(this, "mob", "mob-quests.yml"),
                questProgressStorage, balanceStorage, messageService, new NamespacedKey(this, "mob-quest"),
                questAssignmentManager);
        this.minerQuestService = new QuestService(new QuestConfig(this, "miner", "miner-quests.yml"),
                questProgressStorage, balanceStorage, messageService, new NamespacedKey(this, "miner-quest"),
                questAssignmentManager);
        this.questAssignmentManager.setQuestServices(
                java.util.List.of(farmerQuestService, mobQuestService, minerQuestService));
        this.questSummaryService = new QuestSummaryService(this, messageService, questAssignmentManager);

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
        if (chatNotificationSettingsStorage != null) {
            chatNotificationSettingsStorage.save();
        }
        if (tagSelectionStorage != null) {
            tagSelectionStorage.save();
        }
        if (rtpManager != null) {
            rtpManager.save();
        }
        if (questProgressStorage != null) {
            questProgressStorage.save();
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
        getCommand("sitsetting").setExecutor(sitSettingCommand);
        getCommand("sitsetting").setTabCompleter(sitSettingCommand);
        getCommand("bank").setExecutor(new BankCommand(balanceStorage, messageService));
        getCommand("invsee").setExecutor(new InvseeCommand(offlineInventoryStorage, messageService));
        getCommand("echestsee").setExecutor(new EchestseeCommand(offlineInventoryStorage, messageService));
        getCommand("tags").setExecutor(new TagMenuCommand(tagMenuService, messageService,
                getConfig().getString("chat.format", "{prefix}{name}&r %tag% &7>> {message-color}{message}")));
        getCommand("settings").setExecutor(new SettingsCommand(settingsMenuService, messageService));
        getCommand("farmerquest").setExecutor(new QuestMenuCommand(farmerQuestService, messageService));
        getCommand("mobquest").setExecutor(new QuestMenuCommand(mobQuestService, messageService));
        getCommand("minerquest").setExecutor(new QuestMenuCommand(minerQuestService, messageService));
        getCommand("quests").setExecutor(new QuestSummaryCommand(questSummaryService, messageService));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(messageService, questAssignmentManager), this);
        Bukkit.getPluginManager().registerEvents(new SitListener(sitSettingsStorage, sitManager), this);
        Bukkit.getPluginManager().registerEvents(new BankListener(balanceStorage, messageService), this);
        Bukkit.getPluginManager().registerEvents(new OfflineInventoryListener(offlineInventoryStorage), this);
        Bukkit.getPluginManager().registerEvents(new TagMenuListener(tagConfig, tagSelectionStorage, tagMenuService,
                messageService, new NamespacedKey(this, "tag-id"),
                new NamespacedKey(this, "tag-menu-page"), messageService.colorize(tagMenuConfig.getTitle()),
                getConfig().getString("chat.format", "{prefix}{name}&r %tag% &7>> {message-color}{message}")), this);
        Bukkit.getPluginManager().registerEvents(new SettingsMenuListener(settingsMenuService, paySettingsStorage,
                sitSettingsStorage, chatNotificationSettingsStorage, messageService,
                new NamespacedKey(this, "settings-option")), this);
        Bukkit.getPluginManager().registerEvents(new QuestMenuListener(java.util.Map.of(
                "farmer", farmerQuestService,
                "mob", mobQuestService,
                "miner", minerQuestService
        )), this);
        Bukkit.getPluginManager().registerEvents(new QuestProgressListener(
                java.util.List.of(farmerQuestService, mobQuestService, minerQuestService)), this);
        Bukkit.getPluginManager().registerEvents(new ChatFormatListener(tagConfig, tagSelectionStorage, messageService,
                vaultChat, getConfig().getString("chat.format", "{prefix}{name}&r %tag% &7>> {message-color}{message}")), this);
        Bukkit.getPluginManager().registerEvents(new DurabilityWarningListener(messageService), this);
        Bukkit.getPluginManager().registerEvents(new TreeFellerListener(this, getConfig()), this);
    }

    private void startChatNotifications() {
        if (getConfig().getBoolean("chat-notifications.enabled", true)) {
            chatNotificationTask = new ChatNotificationTask(this, messageService, chatNotificationSettingsStorage);
            chatNotificationTask.start();
        }
    }

    public void reloadPluginConfig() {
        ConfigUpdater.updateConfig(this, "config.yml");
        ConfigUpdater.updateConfig(this, "settings-menu.yml");
        ConfigUpdater.updateConfig(this, "farmer-quests.yml");
        ConfigUpdater.updateConfig(this, "mob-quests.yml");
        ConfigUpdater.updateConfig(this, "miner-quests.yml");
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
        this.tagMenuService = new TagMenuService(tagConfig, tagMenuConfig, tagSelectionStorage,
                messageService, vaultChat, new NamespacedKey(this, "tag-id"), new NamespacedKey(this, "tag-menu-page"));
        this.settingsMenuService = new SettingsMenuService(settingsMenuConfig, paySettingsStorage, sitSettingsStorage,
                chatNotificationSettingsStorage, messageService, new NamespacedKey(this, "settings-option"));
        this.questAssignmentManager = new QuestAssignmentManager(questProgressStorage);
        this.farmerQuestService = new QuestService(new QuestConfig(this, "farmer", "farmer-quests.yml"),
                questProgressStorage, balanceStorage, messageService, new NamespacedKey(this, "farmer-quest"),
                questAssignmentManager);
        this.mobQuestService = new QuestService(new QuestConfig(this, "mob", "mob-quests.yml"),
                questProgressStorage, balanceStorage, messageService, new NamespacedKey(this, "mob-quest"),
                questAssignmentManager);
        this.minerQuestService = new QuestService(new QuestConfig(this, "miner", "miner-quests.yml"),
                questProgressStorage, balanceStorage, messageService, new NamespacedKey(this, "miner-quest"),
                questAssignmentManager);
        this.questAssignmentManager.setQuestServices(
                java.util.List.of(farmerQuestService, mobQuestService, minerQuestService));
        this.questSummaryService = new QuestSummaryService(this, messageService, questAssignmentManager);
        registerCommands();
    }
}
