package com.tbfmc.tbfmp.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public class FirstJoinOnboardingService {
    public void send(Player player) {
        Title title = Title.title(
                Component.text("Welcome to OakGlow!", NamedTextColor.GOLD),
                Component.text("Semi-Vanilla • No TP • Player Economy", NamedTextColor.GRAY),
                Title.Times.times(ticksToDuration(10), ticksToDuration(60), ticksToDuration(20))
        );
        player.showTitle(title);

        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Click below to get started:", NamedTextColor.WHITE));
        player.sendMessage(Component.empty());

        player.sendMessage(buildOnboardingLine(
                "[Commands]", " - Opens help menu", "/help", "Click to view server commands"));
        player.sendMessage(buildOnboardingLine(
                "[Rules]", " - Read before playing", "/rules", "Click to read the rules"));
        player.sendMessage(buildOnboardingLine(
                "[Discord]", " - Join community", "/discord", "Click to join our Discord"));

        player.sendMessage(Component.empty());
    }

    private Component buildOnboardingLine(String label, String description, String command, String hoverText) {
        return Component.text(label, NamedTextColor.GOLD)
                .append(Component.text(description, NamedTextColor.GRAY))
                .clickEvent(ClickEvent.runCommand(command))
                .hoverEvent(HoverEvent.showText(Component.text(hoverText, NamedTextColor.GRAY)));
    }

    private Duration ticksToDuration(long ticks) {
        return Duration.ofMillis(ticks * 50L);
    }
}
