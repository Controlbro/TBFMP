package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.quests.AssignedQuest;
import com.tbfmc.tbfmp.quests.QuestAssignmentManager;
import com.tbfmc.tbfmp.quests.QuestProgress;
import com.tbfmc.tbfmp.quests.QuestService;
import com.tbfmc.tbfmp.quests.QuestType;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {
    private final MessageService messages;
    private final QuestAssignmentManager assignmentManager;

    public PlayerJoinListener(MessageService messages, QuestAssignmentManager assignmentManager) {
        this.messages = messages;
        this.assignmentManager = assignmentManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        String joinKey = event.getPlayer().hasPlayedBefore() ? "messages.join" : "messages.first-join";
        String joinMessage = messages.getMessage(joinKey)
                .replace("{player}", name);
        event.setJoinMessage(messages.formatMessage(joinMessage));
        messages.sendMessage(event.getPlayer(), messages.getMessage("messages.motd"));
        sendQuestNotifications(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        String leaveMessage = messages.getMessage("messages.leave")
                .replace("{player}", name);
        event.setQuitMessage(messages.formatMessage(leaveMessage));
    }

    private void sendQuestNotifications(org.bukkit.entity.Player player) {
        List<AssignedQuest> assignedQuests = assignmentManager.getAssignedQuests(player.getUniqueId());
        boolean hasUnclaimed = false;
        String dailyQuestName = null;
        for (AssignedQuest assignedQuest : assignedQuests) {
            QuestService service = assignedQuest.getQuestService();
            QuestProgress progress = service.getProgress(player.getUniqueId(), assignedQuest.getQuestDefinition());
            if (assignedQuest.getQuestDefinition().getType() == QuestType.DAILY && dailyQuestName == null) {
                dailyQuestName = assignedQuest.getQuestDefinition().getName();
            }
            if (progress.isCompleted() && !progress.isClaimed()) {
                hasUnclaimed = true;
            }
        }
        if (dailyQuestName != null) {
            String dailyMessage = messages.getMessage("messages.quest-daily")
                    .replace("{quest}", dailyQuestName);
            messages.sendMessage(player, dailyMessage);
        }
        if (hasUnclaimed) {
            messages.sendMessage(player, messages.getMessage("messages.quest-unclaimed"));
        }
    }
}
