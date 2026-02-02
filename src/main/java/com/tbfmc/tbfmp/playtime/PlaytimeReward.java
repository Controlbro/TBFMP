package com.tbfmc.tbfmp.playtime;

import org.bukkit.Material;

import java.util.List;

public record PlaytimeReward(
        String id,
        int slot,
        long requiredSeconds,
        Material material,
        String name,
        List<String> lore,
        List<String> commands
) {
}
