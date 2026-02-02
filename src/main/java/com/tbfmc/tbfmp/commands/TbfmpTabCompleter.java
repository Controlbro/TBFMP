package com.tbfmc.tbfmp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TbfmpTabCompleter implements TabCompleter {
    private static final List<String> ECO_ACTIONS = List.of("give", "take", "set");
    private static final List<String> EVENT_ACTIONS = List.of("show", "hide");
    private static final List<String> CUSTOM_ACTIONS = List.of("reload");
    private static final List<String> GAMEMODE_ACTIONS = List.of("survival", "creative", "spectator");
    private static final List<String> MAIL_ACTIONS = List.of("send", "read");
    private static final List<String> OAKGLOW_ACTIONS = List.of(
            "reload",
            "setspawn",
            "dragondropset",
            "convert",
            "keepinvtoggle",
            "pvptoggle",
            "resetevent",
            "toggleleaderboard"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String name = command.getName().toLowerCase();
        if (args.length == 0) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return switch (name) {
                case "eco" -> filter(ECO_ACTIONS, args[0]);
                case "event" -> filter(EVENT_ACTIONS, args[0]);
                case "custom" -> filter(CUSTOM_ACTIONS, args[0]);
                case "oakglow" -> filter(OAKGLOW_ACTIONS, args[0]);
                case "gamemode", "gm" -> filter(GAMEMODE_ACTIONS, args[0]);
                case "mail" -> filter(MAIL_ACTIONS, args[0]);
                case "balance", "pay", "hug", "resetrtp", "tphere", "msg", "tp", "tpa", "tpahere" ->
                        onlinePlayers(args[0]);
                default -> Collections.emptyList();
            };
        }
        if (args.length == 2) {
            return switch (name) {
                case "eco" -> onlinePlayers(args[1]);
                case "pay" -> Collections.emptyList();
                case "gamemode", "gm", "msg" -> onlinePlayers(args[1]);
                case "mail" -> "send".equalsIgnoreCase(args[0]) ? onlinePlayers(args[1]) : Collections.emptyList();
                case "oakglow" -> Collections.emptyList();
                default -> Collections.emptyList();
            };
        }
        return Collections.emptyList();
    }

    private List<String> onlinePlayers(String input) {
        List<String> matches = new ArrayList<>();
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            if (StringUtil.startsWithIgnoreCase(name, input)) {
                matches.add(name);
            }
        }
        return matches;
    }

    private List<String> filter(List<String> options, String input) {
        List<String> matches = new ArrayList<>();
        StringUtil.copyPartialMatches(input, options, matches);
        return matches;
    }
}
