package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.settings.PvpSettingsStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class PvpToggleListener implements Listener {
    private final PvpSettingsStorage pvpSettingsStorage;
    private final MessageService messages;

    public PvpToggleListener(PvpSettingsStorage pvpSettingsStorage, MessageService messages) {
        this.pvpSettingsStorage = pvpSettingsStorage;
        this.messages = messages;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        Player attacker = getAttacker(event.getDamager());
        if (attacker == null) {
            return;
        }
        if (pvpSettingsStorage.isEnabled(attacker.getUniqueId())
                && pvpSettingsStorage.isEnabled(victim.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        messages.sendMessage(attacker, messages.getMessage("messages.pvp-disabled"));
    }

    private Player getAttacker(Entity damager) {
        if (damager instanceof Player player) {
            return player;
        }
        if (damager instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Player player) {
                return player;
            }
        }
        return null;
    }
}
