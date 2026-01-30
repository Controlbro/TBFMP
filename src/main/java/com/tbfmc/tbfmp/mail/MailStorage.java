package com.tbfmc.tbfmp.mail;

import com.tbfmc.tbfmp.util.UnifiedDataFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MailStorage {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, List<MailMessage>> mail = new HashMap<>();

    public MailStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "mail.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public boolean hasMail(UUID playerId) {
        return getMailCount(playerId) > 0;
    }

    public int getMailCount(UUID playerId) {
        List<MailMessage> messages = mail.get(playerId);
        return messages == null ? 0 : messages.size();
    }

    public List<MailMessage> getMail(UUID playerId) {
        List<MailMessage> messages = mail.get(playerId);
        if (messages == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(messages);
    }

    public void addMail(UUID playerId, MailMessage message) {
        mail.computeIfAbsent(playerId, key -> new ArrayList<>()).add(message);
        setValue(playerId, mail.get(playerId));
        save();
    }

    public void clearMail(UUID playerId) {
        mail.remove(playerId);
        setValue(playerId, null);
        save();
    }

    public void save() {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.save();
            return;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            legacyData.save(file);
        } catch (IOException ignored) {
        }
    }

    public void reloadFromUnifiedData() {
        mail.clear();
        load();
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (Map.Entry<UUID, List<MailMessage>> entry : mail.entrySet()) {
            data.set("mail." + entry.getKey(), serialize(entry.getValue()));
        }
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            loadFromConfig(unifiedDataFile.getData());
            return;
        }
        loadFromConfig(legacyData);
    }

    private void loadFromConfig(FileConfiguration data) {
        org.bukkit.configuration.ConfigurationSection section = data.getConfigurationSection("mail");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                List<MailMessage> messages = deserialize(data.getList("mail." + key));
                if (!messages.isEmpty()) {
                    mail.put(playerId, messages);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private void setValue(UUID playerId, List<MailMessage> messages) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("mail." + playerId, messages == null ? null : serialize(messages));
            return;
        }
        legacyData.set("mail." + playerId, messages == null ? null : serialize(messages));
    }

    private List<Map<String, String>> serialize(List<MailMessage> messages) {
        List<Map<String, String>> data = new ArrayList<>();
        for (MailMessage message : messages) {
            Map<String, String> entry = new HashMap<>();
            entry.put("sender", message.sender());
            entry.put("message", message.message());
            data.add(entry);
        }
        return data;
    }

    private List<MailMessage> deserialize(List<?> entries) {
        if (entries == null) {
            return Collections.emptyList();
        }
        List<MailMessage> messages = new ArrayList<>();
        for (Object entry : entries) {
            if (entry instanceof Map<?, ?> map) {
                Object sender = map.get("sender");
                Object message = map.get("message");
                if (sender != null && message != null) {
                    messages.add(new MailMessage(sender.toString(), message.toString()));
                }
            } else if (entry instanceof String line) {
                int splitIndex = line.indexOf(':');
                if (splitIndex > 0 && splitIndex + 1 < line.length()) {
                    String sender = line.substring(0, splitIndex).trim();
                    String message = line.substring(splitIndex + 1).trim();
                    messages.add(new MailMessage(sender, message));
                }
            }
        }
        return messages;
    }
}
