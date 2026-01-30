package com.tbfmc.tbfmp.storage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class MySqlStorageService {
    private static final int DATA_VERSION = 1;

    private final JavaPlugin plugin;
    private final boolean enabled;
    private final boolean uploadBalances;
    private final boolean uploadMiningEvent;
    private final boolean logConnectionTests;
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final List<SectionTable> tables = new ArrayList<>();
    private boolean connectionValid;
    private String lastErrorMessage;

    public MySqlStorageService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfig().getBoolean("storage.mysql.enabled", false);
        this.uploadBalances = plugin.getConfig().getBoolean("storage.mysql.upload-balances", true);
        this.uploadMiningEvent = plugin.getConfig().getBoolean("storage.mysql.upload-event", true);
        this.logConnectionTests = plugin.getConfig().getBoolean("storage.mysql.log-connection-tests", true);
        String host = plugin.getConfig().getString("storage.mysql.host", "localhost");
        int port = plugin.getConfig().getInt("storage.mysql.port", 3306);
        String database = plugin.getConfig().getString("storage.mysql.database", "oakglow");
        boolean useSsl = plugin.getConfig().getBoolean("storage.mysql.use-ssl", false);
        int connectTimeoutMs = plugin.getConfig().getInt("storage.mysql.connect-timeout-ms", 5000);
        int socketTimeoutMs = plugin.getConfig().getInt("storage.mysql.socket-timeout-ms", 5000);
        this.username = plugin.getConfig().getString("storage.mysql.username", "root");
        this.password = plugin.getConfig().getString("storage.mysql.password", "");
        this.jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=" + useSsl
                + "&connectTimeout=" + connectTimeoutMs
                + "&socketTimeout=" + socketTimeoutMs
                + "&allowPublicKeyRetrieval=true"
                + "&useUnicode=true&characterEncoding=utf8";
        registerTables();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public java.util.Set<String> getUploadSections() {
        if (!enabled) {
            return java.util.Set.of();
        }
        java.util.Set<String> sections = new java.util.HashSet<>();
        if (uploadBalances) {
            sections.add("balances");
        }
        if (uploadMiningEvent) {
            sections.add("mining-event");
        }
        return java.util.Collections.unmodifiableSet(sections);
    }

    public boolean isConnectionValid() {
        return connectionValid;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public boolean initialize() {
        if (!enabled) {
            return false;
        }
        connectionValid = testConnection();
        if (connectionValid) {
            ensureTables();
        }
        return connectionValid;
    }

    public void initializeAsync() {
        if (!enabled) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            connectionValid = testConnection();
            if (connectionValid) {
                ensureTables();
            }
        });
    }

    public void pingAndEnsureTables() {
        if (!enabled) {
            return;
        }
        boolean valid = testConnection();
        if (valid) {
            ensureTables();
        }
    }

    public void loadTo(FileConfiguration data) {
        if (!enabled || !connectionValid) {
            return;
        }
        for (SectionTable table : tables) {
            loadSection(data, table);
        }
        data.set("data-version", DATA_VERSION);
    }

    public void saveFrom(FileConfiguration data) {
        if (!enabled || !connectionValid) {
            return;
        }
        for (SectionTable table : tables) {
            saveSection(data, table);
        }
    }

    public void saveSections(FileConfiguration data, Collection<String> sections) {
        if (!enabled || !connectionValid) {
            return;
        }
        for (SectionTable table : tables) {
            if (!sections.contains(table.sectionPath)) {
                continue;
            }
            saveSection(data, table);
        }
    }

    private void loadSection(FileConfiguration data, SectionTable table) {
        String sql = "SELECT entry_key, entry_value FROM " + table.tableName;
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String key = resultSet.getString("entry_key");
                String serialized = resultSet.getString("entry_value");
                Object value = deserializeValue(serialized);
                if (value != null) {
                    data.set(table.sectionPath + "." + key, value);
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to load MySQL data for " + table.tableName, ex);
            connectionValid = false;
            lastErrorMessage = ex.getMessage();
        }
    }

    private void saveSection(FileConfiguration data, SectionTable table) {
        String deleteSql = "DELETE FROM " + table.tableName;
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(deleteSql);
            ConfigurationSection section = data.getConfigurationSection(table.sectionPath);
            if (section == null) {
                return;
            }
            String insertSql = "INSERT INTO " + table.tableName + " (entry_key, entry_value) VALUES (?, ?)";
            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                for (String key : section.getKeys(false)) {
                    Object value = section.get(key);
                    String serialized = serializeValue(value);
                    insert.setString(1, key);
                    insert.setString(2, serialized);
                    insert.addBatch();
                }
                insert.executeBatch();
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to save MySQL data for " + table.tableName, ex);
            connectionValid = false;
            lastErrorMessage = ex.getMessage();
        }
    }

    private boolean testConnection() {
        try (Connection connection = getConnection()) {
            boolean valid = connection != null && connection.isValid(2);
            connectionValid = valid;
            if (valid) {
                lastErrorMessage = null;
            }
            if (logConnectionTests) {
                plugin.getLogger().info(valid ? "MySQL connection test succeeded." : "MySQL connection test failed.");
            }
            return valid;
        } catch (SQLException ex) {
            connectionValid = false;
            lastErrorMessage = ex.getMessage();
            if (logConnectionTests) {
                plugin.getLogger().log(Level.WARNING, "MySQL connection test failed.", ex);
            }
            return false;
        }
    }

    private void ensureTables() {
        for (SectionTable table : tables) {
            String sql = "CREATE TABLE IF NOT EXISTS " + table.tableName + " ("
                    + "entry_key VARCHAR(64) NOT NULL,"
                    + "entry_value LONGTEXT NOT NULL,"
                    + "PRIMARY KEY (entry_key)"
                    + ")";
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute(sql);
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.WARNING, "Failed to ensure MySQL table " + table.tableName, ex);
                connectionValid = false;
                lastErrorMessage = ex.getMessage();
            }
        }
    }

    public void ensureTables(Collection<String> sections) {
        for (SectionTable table : tables) {
            if (!sections.contains(table.sectionPath)) {
                continue;
            }
            String sql = "CREATE TABLE IF NOT EXISTS " + table.tableName + " ("
                    + "entry_key VARCHAR(64) NOT NULL,"
                    + "entry_value LONGTEXT NOT NULL,"
                    + "PRIMARY KEY (entry_key)"
                    + ")";
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute(sql);
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.WARNING, "Failed to ensure MySQL table " + table.tableName, ex);
                connectionValid = false;
                lastErrorMessage = ex.getMessage();
            }
        }
    }

    public boolean refreshConnection() {
        if (!enabled) {
            return false;
        }
        return testConnection();
    }

    public TableCheckResult checkTables() {
        if (!enabled) {
            return new TableCheckResult(false, List.of(), "MySQL storage is not enabled.");
        }
        if (!refreshConnection()) {
            String error = lastErrorMessage == null ? "MySQL connection failed." : lastErrorMessage;
            return new TableCheckResult(false, List.of(), error);
        }
        List<String> missing = new ArrayList<>();
        try (Connection connection = getConnection()) {
            for (SectionTable table : tables) {
                try (ResultSet resultSet = connection.getMetaData().getTables(
                        connection.getCatalog(), null, table.tableName, new String[]{"TABLE"})) {
                    if (!resultSet.next()) {
                        missing.add(table.tableName);
                    }
                }
            }
        } catch (SQLException ex) {
            connectionValid = false;
            lastErrorMessage = ex.getMessage();
            return new TableCheckResult(false, List.of(), ex.getMessage());
        }
        return new TableCheckResult(missing.isEmpty(), missing, null);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    private String serializeValue(Object value) {
        YamlConfiguration temp = new YamlConfiguration();
        temp.set("value", value);
        return temp.saveToString();
    }

    private Object deserializeValue(String serialized) {
        if (serialized == null) {
            return null;
        }
        YamlConfiguration temp = new YamlConfiguration();
        try {
            temp.loadFromString(serialized);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to parse MySQL data entry.", ex);
            return null;
        }
        return temp.get("value");
    }

    private void registerTables() {
        if (uploadBalances) {
            tables.add(new SectionTable("balances", "balances"));
        }
        if (uploadMiningEvent) {
            tables.add(new SectionTable("mining-event", "mining_event"));
        }
    }

    private record SectionTable(String sectionPath, String tableName) {
    }

    public record TableCheckResult(boolean success, List<String> missingTables, String errorMessage) {
    }
}
