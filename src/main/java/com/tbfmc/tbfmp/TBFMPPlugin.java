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
import com.tbfmc.tbfmp.commands.BackCommand;
import com.tbfmc.tbfmp.commands.BackWarpCommand;
import com.tbfmc.tbfmp.commands.ConfirmCommand;
import com.tbfmc.tbfmp.commands.EcoCommand;
import com.tbfmc.tbfmp.commands.CustomCommand;
import com.tbfmc.tbfmp.commands.EventCommand;
import com.tbfmc.tbfmp.commands.FlyCommand;
import com.tbfmc.tbfmp.commands.GamemodeCommand;
import com.tbfmc.tbfmp.commands.HugCommand;
import com.tbfmc.tbfmp.commands.HomeCommand;
import com.tbfmc.tbfmp.commands.HomesCommand;
import com.tbfmc.tbfmp.commands.InfoCommand;
import com.tbfmc.tbfmp.commands.MailCommand;
import com.tbfmc.tbfmp.commands.MallWarpCommand;
import com.tbfmc.tbfmp.commands.MsgCommand;
import com.tbfmc.tbfmp.commands.NickCommand;
import com.tbfmc.tbfmp.commands.EnderChestCommand;
import com.tbfmc.tbfmp.commands.RealnameCommand;
import com.tbfmc.tbfmp.commands.UnnickCommand;
import com.tbfmc.tbfmp.commands.PayCommand;
import com.tbfmc.tbfmp.commands.PayToggleCommand;
import com.tbfmc.tbfmp.commands.PlaytimeCommand;
import com.tbfmc.tbfmp.commands.PTimeCommand;
import com.tbfmc.tbfmp.commands.PWeatherCommand;
import com.tbfmc.tbfmp.commands.ResetRtpCommand;
import com.tbfmc.tbfmp.commands.RtpCommand;
import com.tbfmc.tbfmp.commands.SetHomeCommand;
import com.tbfmc.tbfmp.commands.SetMallWarpCommand;
import com.tbfmc.tbfmp.commands.SetMallWarpRegionCommand;
import com.tbfmc.tbfmp.commands.SetWarpCommand;
import com.tbfmc.tbfmp.commands.SettingsCommand;
import com.tbfmc.tbfmp.commands.SitCommand;
import com.tbfmc.tbfmp.commands.SitSettingCommand;
import com.tbfmc.tbfmp.commands.SocialSpyCommand;
import com.tbfmc.tbfmp.commands.StaffChatCommand;
import com.tbfmc.tbfmp.commands.TagMenuCommand;
import com.tbfmc.tbfmp.commands.TbfmcCommand;
import com.tbfmc.tbfmp.commands.TbfmpTabCompleter;
import com.tbfmc.tbfmp.commands.TpAcceptCommand;
import com.tbfmc.tbfmp.commands.TpCommand;
import com.tbfmc.tbfmp.commands.TpDenyCommand;
import com.tbfmc.tbfmp.commands.TpHereCommand;
import com.tbfmc.tbfmp.commands.TpaCommand;
import com.tbfmc.tbfmp.commands.TpaHereCommand;
import com.tbfmc.tbfmp.commands.TpaToggleCommand;
import com.tbfmc.tbfmp.commands.WarpCommand;
import com.tbfmc.tbfmp.commands.WarpsCommand;
import com.tbfmc.tbfmp.commands.WorkbenchCommand;
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
import com.tbfmc.tbfmp.listeners.DragonDropListener;
import com.tbfmc.tbfmp.listeners.GratuityWelcomeListener;
import com.tbfmc.tbfmp.listeners.MallWarpRestrictionListener;
import com.tbfmc.tbfmp.listeners.MallWarpSelectionListener;
import com.tbfmc.tbfmp.listeners.MiningEventListener;
import com.tbfmc.tbfmp.listeners.MiningEventPlayerListener;
import com.tbfmc.tbfmp.listeners.SettingsMenuListener;
import com.tbfmc.tbfmp.listeners.BankListener;
import com.tbfmc.tbfmp.listeners.ChatFormatListener;
import com.tbfmc.tbfmp.listeners.DurabilityWarningListener;
import com.tbfmc.tbfmp.listeners.KeepInventoryListener;
import com.tbfmc.tbfmp.listeners.PvpToggleListener;
import com.tbfmc.tbfmp.listeners.PlayerJoinListener;
import com.tbfmc.tbfmp.listeners.PlayerTrailListener;
import com.tbfmc.tbfmp.listeners.BackLocationListener;
import com.tbfmc.tbfmp.listeners.SitDamageListener;
import com.tbfmc.tbfmp.listeners.SitListener;
import com.tbfmc.tbfmp.listeners.SocialSpyListener;
import com.tbfmc.tbfmp.listeners.TabListSessionListener;
import com.tbfmc.tbfmp.listeners.SpawnListener;
import com.tbfmc.tbfmp.listeners.TagMenuListener;
import com.tbfmc.tbfmp.mallwarp.MallWarpManager;
import com.tbfmc.tbfmp.mallwarp.MallWarpSelectionManager;
import com.tbfmc.tbfmp.mallwarp.MallWarpService;
import com.tbfmc.tbfmp.mail.MailStorage;
import com.tbfmc.tbfmp.nickname.NicknameStorage;
import com.tbfmc.tbfmp.playtime.PlaytimeListener;
import com.tbfmc.tbfmp.playtime.PlaytimeRewardsConfig;
import com.tbfmc.tbfmp.playtime.PlaytimeRewardsListener;
import com.tbfmc.tbfmp.playtime.PlaytimeRewardsService;
import com.tbfmc.tbfmp.playtime.PlaytimeStorage;
import com.tbfmc.tbfmp.playtime.PlaytimeTracker;
import com.tbfmc.tbfmp.rtp.RtpManager;
import com.tbfmc.tbfmp.staff.SocialSpyManager;
import com.tbfmc.tbfmp.settings.SettingsMenuConfig;
import com.tbfmc.tbfmp.settings.SettingsMenuService;
import com.tbfmc.tbfmp.settings.KeepInventorySettingsStorage;
import com.tbfmc.tbfmp.settings.PvpSettingsStorage;
import com.tbfmc.tbfmp.sit.SitManager;
import com.tbfmc.tbfmp.sit.SitSettingsStorage;
import com.tbfmc.tbfmp.tablist.TabListService;
import com.tbfmc.tbfmp.teleport.BackLocationManager;
import com.tbfmc.tbfmp.teleport.HomeManager;
import com.tbfmc.tbfmp.teleport.TpaManager;
import com.tbfmc.tbfmp.teleport.TpaSettingsStorage;
import com.tbfmc.tbfmp.teleport.WarpManager;
import com.tbfmc.tbfmp.util.ConfigUpdater;
import com.tbfmc.tbfmp.util.CustomConfig;
import com.tbfmc.tbfmp.util.MessageService;
import com.tbfmc.tbfmp.util.MessagesConfig;
import com.tbfmc.tbfmp.util.SpawnService;
import com.tbfmc.tbfmp.util.UnifiedDataFile;
import com.tbfmc.tbfmp.storage.MySqlStorageService;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class TBFMPPlugin extends JavaPlugin {
    private UnifiedDataFile unifiedDataFile;
    private BalanceStorage balanceStorage;
    private PaySettingsStorage paySettingsStorage;
    private SitSettingsStorage sitSettingsStorage;
    private ChatNotificationSettingsStorage chatNotificationSettingsStorage;
    private EventSettingsStorage eventSettingsStorage;
    private KeepInventorySettingsStorage keepInventorySettingsStorage;
    private PvpSettingsStorage pvpSettingsStorage;
    private TpaSettingsStorage tpaSettingsStorage;
    private SitManager sitManager;
    private RtpManager rtpManager;
    private BackLocationManager backLocationManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private TpaManager tpaManager;
    private MessageService messageService;
    private MessagesConfig messagesConfig;
    private ChatNotificationTask chatNotificationTask;
    private HugCommand hugCommand;
    private SitCommand sitCommand;
    private SitSettingCommand sitSettingCommand;
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
    private BukkitTask tabListTask;
    private SpawnService spawnService;
    private CustomConfig customConfig;
    private MiningEventStorage miningEventStorage;
    private MiningEventService miningEventService;
    private MallWarpManager mallWarpManager;
    private MallWarpSelectionManager mallWarpSelectionManager;
    private SocialSpyManager socialSpyManager;
    private MailStorage mailStorage;
    private NicknameStorage nicknameStorage;
    private MySqlStorageService mysqlStorageService;
    private BukkitTask mysqlPingTask;
    private PlaytimeStorage playtimeStorage;
    private PlaytimeTracker playtimeTracker;
    private PlaytimeRewardsConfig playtimeRewardsConfig;
    private PlaytimeRewardsService playtimeRewardsService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigUpdater.updateConfig(this, "config.yml");
        ConfigUpdater.updateConfig(this, "settings-menu.yml");
        reloadConfig();
        migrateMessagesConfig();
        ConfigUpdater.updateConfig(this, "messages.yml");
        saveResourceIfMissing("tags.yml");
        saveResourceIfMissing("tag-menu.yml");
        saveResourceIfMissing("CustomConfig.yml");
        saveResourceIfMissing("playtime-rewards.yml");
        this.customConfig = new CustomConfig(this);
        this.messagesConfig = new MessagesConfig(this);
        this.messageService = new MessageService(this, messagesConfig.getConfig());
        this.mysqlStorageService = new MySqlStorageService(this);
        this.mysqlStorageService.initializeAsync();
        this.unifiedDataFile = new UnifiedDataFile(this);
        this.balanceStorage = new BalanceStorage(this, unifiedDataFile);
        this.paySettingsStorage = new PaySettingsStorage(this, unifiedDataFile);
        this.sitSettingsStorage = new SitSettingsStorage(this, unifiedDataFile);
        this.chatNotificationSettingsStorage = new ChatNotificationSettingsStorage(this, unifiedDataFile);
        this.eventSettingsStorage = new EventSettingsStorage(this, unifiedDataFile);
        this.keepInventorySettingsStorage = new KeepInventorySettingsStorage(this, unifiedDataFile);
        this.pvpSettingsStorage = new PvpSettingsStorage(this, unifiedDataFile);
        this.tpaSettingsStorage = new TpaSettingsStorage(this, unifiedDataFile);
        this.playtimeStorage = new PlaytimeStorage(this, unifiedDataFile);
        this.playtimeTracker = new PlaytimeTracker(playtimeStorage);
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            playtimeTracker.startSession(player);
        }
        this.miningEventStorage = new MiningEventStorage(this, unifiedDataFile);
        this.miningEventService = new MiningEventService(this, miningEventStorage, eventSettingsStorage);
        this.sitManager = new SitManager(messageService, this);
        this.tagConfig = new TagConfig(this);
        this.tagMenuConfig = new TagMenuConfig(this);
        this.tagSelectionStorage = new TagSelectionStorage(this, unifiedDataFile);
        this.settingsMenuConfig = new SettingsMenuConfig(this);
        this.rtpManager = new RtpManager(this, messageService, unifiedDataFile);
        this.hugCommand = new HugCommand(this, messageService);
        this.sitCommand = new SitCommand(sitManager, messageService);
        this.sitSettingCommand = new SitSettingCommand(sitSettingsStorage, messageService);
        this.backLocationManager = new BackLocationManager(this, unifiedDataFile);
        this.homeManager = new HomeManager(this, unifiedDataFile);
        this.warpManager = new WarpManager(this, unifiedDataFile);
        this.tpaManager = new TpaManager(this, messageService, tpaSettingsStorage);
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
                chatNotificationSettingsStorage, keepInventorySettingsStorage, pvpSettingsStorage, eventSettingsStorage,
                tpaSettingsStorage,
                messageService, new NamespacedKey(this, "settings-option"));
        this.spawnService = new SpawnService(this);
        MallWarpService mallWarpService = new MallWarpService(this);
        this.mallWarpManager = new MallWarpManager(this, mallWarpService, unifiedDataFile);
        this.mallWarpSelectionManager = new MallWarpSelectionManager();
        this.socialSpyManager = new SocialSpyManager(this, unifiedDataFile);
        this.mailStorage = new MailStorage(this, unifiedDataFile);
        this.nicknameStorage = new NicknameStorage(this, unifiedDataFile);
        this.playtimeRewardsConfig = new PlaytimeRewardsConfig(this);
        this.playtimeRewardsService = new PlaytimeRewardsService(playtimeRewardsConfig, playtimeStorage, playtimeTracker, messageService);

        VaultEconomyProvider economyProvider = new VaultEconomyProvider(balanceStorage);
        Bukkit.getServicesManager().register(net.milkbowl.vault.economy.Economy.class, economyProvider, this, ServicePriority.Normal);

        registerCommands();
        registerListeners();
        miningEventService.applyToOnlinePlayers();
        startChatNotifications();
        startAfkTask();
        startTabListTask();
        startMysqlUploadTask();
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
        if (keepInventorySettingsStorage != null) {
            keepInventorySettingsStorage.save();
        }
        if (pvpSettingsStorage != null) {
            pvpSettingsStorage.save();
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
        if (backLocationManager != null) {
            backLocationManager.save();
        }
        if (homeManager != null) {
            homeManager.save();
        }
        if (warpManager != null) {
            warpManager.save();
        }
        if (mallWarpManager != null) {
            mallWarpManager.save();
        }
        if (socialSpyManager != null) {
            socialSpyManager.save();
        }
        if (mailStorage != null) {
            mailStorage.save();
        }
        if (nicknameStorage != null) {
            nicknameStorage.save();
        }
        if (playtimeStorage != null) {
            playtimeTracker.flushOnlineSessions();
            playtimeStorage.save();
        }
        if (tpaSettingsStorage != null) {
            tpaSettingsStorage.save();
        }
        if (unifiedDataFile != null && unifiedDataFile.isEnabled()) {
            unifiedDataFile.save();
        }
        flushMysqlAsync();
        if (mysqlPingTask != null) {
            mysqlPingTask.cancel();
            mysqlPingTask = null;
        }
        if (afkTask != null) {
            afkTask.cancel();
            afkTask = null;
        }
        if (tabListTask != null) {
            tabListTask.cancel();
            tabListTask = null;
        }
    }

    public void flushMysqlAsync() {
        if (mysqlStorageService == null || !mysqlStorageService.isEnabled()) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(this, this::uploadMysql);
    }

    private void startMysqlUploadTask() {
        if (mysqlPingTask != null) {
            mysqlPingTask.cancel();
        }
        if (mysqlStorageService == null || mysqlStorageService.getUploadSections().isEmpty()) {
            return;
        }
        long intervalTicks = 20L * 60L * 5L;
        mysqlPingTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this,
                this::uploadMysql, intervalTicks, intervalTicks);
    }

    private void uploadMysql() {
        if (mysqlStorageService == null) {
            return;
        }
        java.util.Set<String> sections = mysqlStorageService.getUploadSections();
        if (sections.isEmpty()) {
            return;
        }
        if (!mysqlStorageService.refreshConnection()) {
            return;
        }
        mysqlStorageService.ensureTables(sections);
        if (playtimeTracker != null) {
            playtimeTracker.flushOnlineSessions();
        }
        org.bukkit.configuration.file.YamlConfiguration snapshot = new org.bukkit.configuration.file.YamlConfiguration();
        if (balanceStorage != null && sections.contains("balances")) {
            for (java.util.Map.Entry<java.util.UUID, Double> entry : balanceStorage.getAllBalances().entrySet()) {
                snapshot.set("balances." + entry.getKey(), entry.getValue());
            }
        }
        if (miningEventStorage != null && sections.contains("mining-event")) {
            for (java.util.Map.Entry<java.util.UUID, Integer> entry : miningEventStorage.getAllCounts().entrySet()) {
                snapshot.set("mining-event." + entry.getKey(), entry.getValue());
            }
        }
        if (playtimeStorage != null && sections.contains("playtime")) {
            for (java.util.Map.Entry<java.util.UUID, Long> entry : playtimeStorage.getAllPlaytimeSeconds().entrySet()) {
                snapshot.set("playtime." + entry.getKey(), entry.getValue());
            }
        }
        mysqlStorageService.saveSections(snapshot, sections);
    }

    private void registerCommands() {
        TbfmpTabCompleter tabCompleter = new TbfmpTabCompleter();
        getCommand("balance").setExecutor(new BalanceCommand(balanceStorage, messageService));
        getCommand("balance").setTabCompleter(tabCompleter);
        getCommand("balancetop").setExecutor(new BalanceTopCommand(balanceStorage, messageService));
        getCommand("balancetop").setTabCompleter(tabCompleter);
        getCommand("eco").setExecutor(new EcoCommand(balanceStorage, messageService));
        getCommand("eco").setTabCompleter(tabCompleter);
        getCommand("rtp").setExecutor(new RtpCommand(rtpManager, messageService));
        getCommand("rtp").setTabCompleter(tabCompleter);
        getCommand("confirm").setExecutor(new ConfirmCommand(rtpManager, messageService));
        getCommand("confirm").setTabCompleter(tabCompleter);
        getCommand("resetrtp").setExecutor(new ResetRtpCommand(rtpManager, messageService));
        getCommand("resetrtp").setTabCompleter(tabCompleter);
        getCommand("pay").setExecutor(new PayCommand(balanceStorage, paySettingsStorage, messageService));
        getCommand("pay").setTabCompleter(tabCompleter);
        getCommand("paytoggle").setExecutor(new PayToggleCommand(paySettingsStorage, messageService));
        getCommand("paytoggle").setTabCompleter(tabCompleter);
        getCommand("vote").setExecutor(new InfoCommand(messageService, "messages.vote"));
        getCommand("vote").setTabCompleter(tabCompleter);
        getCommand("web").setExecutor(new InfoCommand(messageService, "messages.web"));
        getCommand("web").setTabCompleter(tabCompleter);
        getCommand("discord").setExecutor(new InfoCommand(messageService, "messages.discord"));
        getCommand("discord").setTabCompleter(tabCompleter);
        getCommand("shoptut").setExecutor(new InfoCommand(messageService, "messages.shoptut"));
        getCommand("shoptut").setTabCompleter(tabCompleter);
        getCommand("rules").setExecutor(new InfoCommand(messageService, "messages.rules"));
        getCommand("rules").setTabCompleter(tabCompleter);
        getCommand("mallrules").setExecutor(new InfoCommand(messageService, "messages.mallrules"));
        getCommand("mallrules").setTabCompleter(tabCompleter);
        getCommand("hug").setExecutor(hugCommand);
        getCommand("hug").setTabCompleter(tabCompleter);
        getCommand("oakglow").setExecutor(new TbfmcCommand(this, messageService, spawnService,
                keepInventorySettingsStorage, pvpSettingsStorage, miningEventService));
        getCommand("oakglow").setTabCompleter(tabCompleter);
        getCommand("fly").setExecutor(new FlyCommand(messageService));
        getCommand("fly").setTabCompleter(tabCompleter);
        getCommand("sit").setExecutor(sitCommand);
        getCommand("sit").setTabCompleter(tabCompleter);
        getCommand("sitsetting").setExecutor(sitSettingCommand);
        getCommand("sitsetting").setTabCompleter(sitSettingCommand);
        getCommand("bank").setExecutor(new BankCommand(balanceStorage, messageService));
        getCommand("bank").setTabCompleter(tabCompleter);
        getCommand("tags").setExecutor(new TagMenuCommand(tagMenuService, messageService,
                getConfig().getString("chat.format", "{prefix}{name}&r %tag% &7>> {message-color}{message}")));
        getCommand("tags").setTabCompleter(tabCompleter);
        getCommand("settings").setExecutor(new SettingsCommand(settingsMenuService, messageService));
        getCommand("settings").setTabCompleter(tabCompleter);
        getCommand("afk").setExecutor(new AfkCommand(afkManager, messageService));
        getCommand("afk").setTabCompleter(tabCompleter);
        getCommand("custom").setExecutor(new CustomCommand(this, messageService));
        getCommand("custom").setTabCompleter(tabCompleter);
        getCommand("event").setExecutor(new EventCommand(miningEventService, messageService));
        getCommand("event").setTabCompleter(tabCompleter);
        getCommand("mallwarp").setExecutor(new MallWarpCommand(mallWarpManager, messageService));
        getCommand("mallwarp").setTabCompleter(tabCompleter);
        getCommand("setmallwarp").setExecutor(new SetMallWarpCommand(mallWarpManager, messageService));
        getCommand("setmallwarp").setTabCompleter(tabCompleter);
        getCommand("setmallwarprg").setExecutor(new SetMallWarpRegionCommand(
                mallWarpManager, mallWarpSelectionManager, messageService));
        getCommand("setmallwarprg").setTabCompleter(tabCompleter);
        getCommand("backwarp").setExecutor(new BackWarpCommand(mallWarpManager, messageService));
        getCommand("backwarp").setTabCompleter(tabCompleter);
        getCommand("mc").setExecutor(new StaffChatCommand(messageService));
        getCommand("mc").setTabCompleter(tabCompleter);
        getCommand("socialspy").setExecutor(new SocialSpyCommand(socialSpyManager, messageService));
        getCommand("socialspy").setTabCompleter(tabCompleter);
        getCommand("msg").setExecutor(new MsgCommand(messageService));
        getCommand("msg").setTabCompleter(tabCompleter);
        getCommand("mail").setExecutor(new MailCommand(mailStorage, messageService));
        getCommand("mail").setTabCompleter(tabCompleter);
        getCommand("tp").setExecutor(new TpCommand(messageService));
        getCommand("tp").setTabCompleter(tabCompleter);
        getCommand("tphere").setExecutor(new TpHereCommand(messageService));
        getCommand("tphere").setTabCompleter(tabCompleter);
        getCommand("gamemode").setExecutor(new GamemodeCommand(messageService));
        getCommand("gamemode").setTabCompleter(tabCompleter);
        getCommand("nick").setExecutor(new NickCommand(messageService, tabListService, afkManager, nicknameStorage));
        getCommand("nick").setTabCompleter(tabCompleter);
        getCommand("realname").setExecutor(new RealnameCommand(messageService));
        getCommand("realname").setTabCompleter(tabCompleter);
        getCommand("unnick").setExecutor(new UnnickCommand(messageService, tabListService, afkManager, nicknameStorage));
        getCommand("unnick").setTabCompleter(tabCompleter);
        getCommand("workbench").setExecutor(new WorkbenchCommand(messageService));
        getCommand("workbench").setTabCompleter(tabCompleter);
        getCommand("home").setExecutor(new HomeCommand(homeManager, messageService));
        getCommand("home").setTabCompleter(tabCompleter);
        getCommand("homes").setExecutor(new HomesCommand(homeManager, messageService));
        getCommand("homes").setTabCompleter(tabCompleter);
        getCommand("sethome").setExecutor(new SetHomeCommand(homeManager, messageService));
        getCommand("sethome").setTabCompleter(tabCompleter);
        getCommand("warp").setExecutor(new WarpCommand(warpManager, messageService));
        getCommand("warp").setTabCompleter(tabCompleter);
        getCommand("warps").setExecutor(new WarpsCommand(warpManager, messageService));
        getCommand("warps").setTabCompleter(tabCompleter);
        getCommand("setwarp").setExecutor(new SetWarpCommand(warpManager, messageService));
        getCommand("setwarp").setTabCompleter(tabCompleter);
        getCommand("back").setExecutor(new BackCommand(backLocationManager, messageService));
        getCommand("back").setTabCompleter(tabCompleter);
        getCommand("tpa").setExecutor(new TpaCommand(tpaManager, messageService));
        getCommand("tpa").setTabCompleter(tabCompleter);
        getCommand("tpahere").setExecutor(new TpaHereCommand(tpaManager, messageService));
        getCommand("tpahere").setTabCompleter(tabCompleter);
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(tpaManager, messageService));
        getCommand("tpaccept").setTabCompleter(tabCompleter);
        getCommand("tpdeny").setExecutor(new TpDenyCommand(tpaManager, messageService));
        getCommand("tpdeny").setTabCompleter(tabCompleter);
        getCommand("tpatoggle").setExecutor(new TpaToggleCommand(tpaSettingsStorage, messageService));
        getCommand("tpatoggle").setTabCompleter(tabCompleter);
        getCommand("enderchest").setExecutor(new EnderChestCommand(messageService));
        getCommand("enderchest").setTabCompleter(tabCompleter);
        getCommand("ptime").setExecutor(new PTimeCommand(messageService));
        getCommand("ptime").setTabCompleter(tabCompleter);
        getCommand("pweather").setExecutor(new PWeatherCommand(messageService));
        getCommand("pweather").setTabCompleter(tabCompleter);
        getCommand("playtime").setExecutor(new PlaytimeCommand(playtimeTracker, playtimeRewardsService, messageService));
        getCommand("playtime").setTabCompleter(tabCompleter);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(
                new PlayerJoinListener(this, messageService, mallWarpManager, mailStorage, nicknameStorage), this);
        Bukkit.getPluginManager().registerEvents(new AfkListener(afkManager), this);
        Bukkit.getPluginManager().registerEvents(new TabListSessionListener(tabListService), this);
        Bukkit.getPluginManager().registerEvents(new SitListener(sitSettingsStorage, sitManager), this);
        Bukkit.getPluginManager().registerEvents(new SitDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new BankListener(balanceStorage, messageService), this);
        Bukkit.getPluginManager().registerEvents(new TagMenuListener(tagConfig, tagSelectionStorage, tagMenuService,
                messageService, new NamespacedKey(this, "tag-id"),
                new NamespacedKey(this, "tag-menu-page"), messageService.colorize(tagMenuConfig.getTitle()),
                getConfig().getString("chat.format", "{prefix}{name}&r %tag% &7>> {message-color}{message}")), this);
        Bukkit.getPluginManager().registerEvents(new SettingsMenuListener(settingsMenuService, paySettingsStorage,
                sitSettingsStorage, chatNotificationSettingsStorage, keepInventorySettingsStorage, pvpSettingsStorage,
                miningEventService, tpaSettingsStorage, messageService,
                new NamespacedKey(this, "settings-option")), this);
        Bukkit.getPluginManager().registerEvents(new ChatFormatListener(tagConfig, tagSelectionStorage, messageService,
                vaultChat, getConfig().getString("chat.format", "{prefix}{name}&r %tag% &7>> {message-color}{message}")), this);
        Bukkit.getPluginManager().registerEvents(new DurabilityWarningListener(messageService), this);
        Bukkit.getPluginManager().registerEvents(new BabyFaithListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GratuityWelcomeListener(this, balanceStorage, messageService), this);
        Bukkit.getPluginManager().registerEvents(new CritParticleListener(customConfig), this);
        Bukkit.getPluginManager().registerEvents(new DeathParticleListener(this, customConfig), this);
        Bukkit.getPluginManager().registerEvents(new PlayerTrailListener(this, customConfig), this);
        Bukkit.getPluginManager().registerEvents(new SpawnListener(spawnService), this);
        Bukkit.getPluginManager().registerEvents(new DragonDropListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MiningEventListener(miningEventService), this);
        Bukkit.getPluginManager().registerEvents(new MiningEventPlayerListener(miningEventService), this);
        Bukkit.getPluginManager().registerEvents(new KeepInventoryListener(keepInventorySettingsStorage), this);
        Bukkit.getPluginManager().registerEvents(new PvpToggleListener(pvpSettingsStorage, messageService), this);
        Bukkit.getPluginManager().registerEvents(new MallWarpSelectionListener(mallWarpSelectionManager, messageService), this);
        Bukkit.getPluginManager().registerEvents(new MallWarpRestrictionListener(mallWarpManager, messageService), this);
        Bukkit.getPluginManager().registerEvents(new SocialSpyListener(socialSpyManager, messageService), this);
        Bukkit.getPluginManager().registerEvents(new BackLocationListener(backLocationManager), this);
        Bukkit.getPluginManager().registerEvents(new PlaytimeListener(playtimeTracker), this);
        Bukkit.getPluginManager().registerEvents(new PlaytimeRewardsListener(playtimeRewardsService), this);
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
        }, 20L * 60L, 20L * 60L);
    }

    private void startTabListTask() {
        if (tabListTask != null) {
            tabListTask.cancel();
        }
        tabListTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            tabListService.updateAll(afkManager::isAfk);
        }, 20L, 20L);
    }

    public void reloadPluginConfig() {
        ConfigUpdater.updateConfig(this, "config.yml");
        ConfigUpdater.updateConfig(this, "settings-menu.yml");
        reloadConfig();
        migrateMessagesConfig();
        ConfigUpdater.updateConfig(this, "messages.yml");
        if (this.messagesConfig == null) {
            this.messagesConfig = new MessagesConfig(this);
        } else {
            this.messagesConfig.reload();
        }
        this.messageService = new MessageService(this, messagesConfig.getConfig());
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
        this.tagSelectionStorage = new TagSelectionStorage(this, unifiedDataFile);
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
                chatNotificationSettingsStorage, keepInventorySettingsStorage, pvpSettingsStorage, eventSettingsStorage,
                tpaSettingsStorage,
                messageService, new NamespacedKey(this, "settings-option"));
        if (this.playtimeRewardsConfig == null) {
            this.playtimeRewardsConfig = new PlaytimeRewardsConfig(this);
        } else {
            this.playtimeRewardsConfig.reload();
        }
        this.playtimeRewardsService = new PlaytimeRewardsService(playtimeRewardsConfig, playtimeStorage, playtimeTracker, messageService);
        registerCommands();
        miningEventService.reloadSettings();
        miningEventService.applyToOnlinePlayers();
        startAfkTask();
        startTabListTask();
    }

    public void reloadCustomConfig() {
        if (customConfig != null) {
            customConfig.reload();
        }
    }

    public boolean convertLegacyData() {
        if (unifiedDataFile == null) {
            return false;
        }
        unifiedDataFile.enable();
        balanceStorage.writeToUnifiedData();
        paySettingsStorage.writeToUnifiedData();
        sitSettingsStorage.writeToUnifiedData();
        chatNotificationSettingsStorage.writeToUnifiedData();
        eventSettingsStorage.writeToUnifiedData();
        keepInventorySettingsStorage.writeToUnifiedData();
        pvpSettingsStorage.writeToUnifiedData();
        miningEventStorage.writeToUnifiedData();
        tagSelectionStorage.writeToUnifiedData();
        rtpManager.writeToUnifiedData();
        backLocationManager.writeToUnifiedData();
        homeManager.writeToUnifiedData();
        warpManager.writeToUnifiedData();
        mallWarpManager.writeToUnifiedData();
        socialSpyManager.writeToUnifiedData();
        mailStorage.writeToUnifiedData();
        nicknameStorage.writeToUnifiedData();
        tpaSettingsStorage.writeToUnifiedData();
        playtimeStorage.writeToUnifiedData();
        unifiedDataFile.save();
        moveLegacyDataFiles();
        return true;
    }

    private void migrateMessagesConfig() {
        if (getConfig().getConfigurationSection("messages") == null) {
            return;
        }
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        FileConfiguration messages = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(messagesFile);
        for (String key : getConfig().getConfigurationSection("messages").getKeys(true)) {
            String path = "messages." + key;
            messages.set(path, getConfig().get("messages." + key));
        }
        try {
            messages.save(messagesFile);
        } catch (java.io.IOException ignored) {
        }
        getConfig().set("messages", null);
        saveConfig();
    }

    private void saveResourceIfMissing(String resourcePath) {
        File target = new File(getDataFolder(), resourcePath);
        if (target.exists()) {
            return;
        }
        saveResource(resourcePath, false);
    }

    private void moveLegacyDataFiles() {
        File dataFolder = new File(getDataFolder(), "data");
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            return;
        }
        String[] files = {
                "balances.yml",
                "pay-settings.yml",
                "sit-settings.yml",
                "chat-notification-settings.yml",
                "event-settings.yml",
                "keep-inventory-settings.yml",
                "pvp-settings.yml",
                "mining-event.yml",
                "tag-selections.yml",
                "rtp-used.yml",
                "back-locations.yml",
                "homes.yml",
                "warps.yml",
                "mallwarp-state.yml",
                "socialspy.yml",
                "mail.yml",
                "nicknames.yml",
                "tpa-settings.yml",
                "playtime.yml",
                "oakglowutil-data.yml"
        };
        for (String name : files) {
            File source = new File(getDataFolder(), name);
            if (!source.exists()) {
                continue;
            }
            File target = new File(dataFolder, name);
            try {
                java.nio.file.Files.move(source.toPath(), target.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (java.io.IOException ignored) {
            }
        }
    }
}
