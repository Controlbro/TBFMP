package com.tbfmc.tbfmp.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class TeleportSound {
    private TeleportSound() {
    }

    public static void play(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }
}
