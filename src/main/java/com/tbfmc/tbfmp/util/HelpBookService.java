package com.tbfmc.tbfmp.util;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class HelpBookService {
    private final JavaPlugin plugin;
    private final MessageService messages;
    private final File file;
    private FileConfiguration config;

    public HelpBookService(JavaPlugin plugin, MessageService messages) {
        this.plugin = plugin;
        this.messages = messages;
        this.file = new File(plugin.getDataFolder(), "bookconfig.yml");
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            plugin.saveResource("bookconfig.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean isEnabled() {
        return config.getBoolean("join-book.enabled", false);
    }

    public boolean isOnlyFirstJoin() {
        return config.getBoolean("join-book.only-first-join", true);
    }

    public ItemStack createHelpBook(Player player) {
        String materialName = config.getString("join-book.material", "WRITTEN_BOOK");
        Material material = Material.matchMaterial(materialName == null ? "" : materialName);
        if (material == null || material == Material.AIR) {
            material = Material.WRITTEN_BOOK;
        }
        ItemStack book = new ItemStack(material);
        if (!(book.getItemMeta() instanceof BookMeta meta)) {
            return null;
        }
        String title = config.getString("join-book.title", "Welcome");
        String author = config.getString("join-book.author", "Server");
        meta.setTitle(messages.colorize(replacePlaceholders(title, player)));
        meta.setAuthor(messages.colorize(replacePlaceholders(author, player)));
        List<String> pages = config.getStringList("join-book.pages");
        if (pages != null && !pages.isEmpty()) {
            meta.setPages(pages.stream()
                    .map(page -> messages.colorize(replacePlaceholders(page, player)))
                    .toList());
        }
        book.setItemMeta(meta);
        return book;
    }

    private String replacePlaceholders(String input, Player player) {
        if (input == null) {
            return "";
        }
        return input.replace("{player}", player.getName());
    }
}
