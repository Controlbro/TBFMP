package com.tbfmc.tbfmp.quests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class QuestAssignmentManager {
    private static final String QUEST_SEPARATOR = ":";
    private final QuestProgressStorage progressStorage;
    private final Random random = new Random();
    private List<QuestService> questServices = List.of();

    public QuestAssignmentManager(QuestProgressStorage progressStorage) {
        this.progressStorage = progressStorage;
    }

    public void setQuestServices(List<QuestService> questServices) {
        this.questServices = List.copyOf(questServices);
    }

    public boolean isQuestAssigned(UUID uuid, String questKey) {
        ensureAssignments(uuid);
        for (QuestType type : QuestType.values()) {
            QuestAssignments assignments = progressStorage.getAssignments(uuid, type);
            if (assignments.getQuestKeys().contains(questKey)) {
                return true;
            }
        }
        return false;
    }

    public List<AssignedQuest> getAssignedQuests(UUID uuid) {
        ensureAssignments(uuid);
        Map<QuestType, List<String>> assignmentsByType = new EnumMap<>(QuestType.class);
        for (QuestType type : QuestType.values()) {
            assignmentsByType.put(type, progressStorage.getAssignments(uuid, type).getQuestKeys());
        }
        List<AssignedQuest> assignedQuests = new ArrayList<>();
        for (QuestType type : QuestType.values()) {
            List<String> keys = assignmentsByType.getOrDefault(type, List.of());
            for (String key : keys) {
                AssignedQuest assignedQuest = resolveQuest(key);
                if (assignedQuest != null) {
                    assignedQuests.add(assignedQuest);
                }
            }
        }
        return assignedQuests;
    }

    private void ensureAssignments(UUID uuid) {
        Map<QuestType, List<String>> availableByType = getAvailableQuestKeysByType();
        long now = System.currentTimeMillis();
        for (QuestType type : QuestType.values()) {
            QuestAssignments assignments = progressStorage.getAssignments(uuid, type);
            List<String> available = availableByType.getOrDefault(type, List.of());
            int required = Math.min(requiredCount(type), available.size());
            boolean expired = now - assignments.getStartTime() >= type.getDurationMillis();
            boolean invalid = assignments.getQuestKeys().size() != required || !available.containsAll(assignments.getQuestKeys());
            if (expired || invalid) {
                List<String> newAssignments = pickRandomQuests(available, required);
                progressStorage.setAssignments(uuid, type, newAssignments, now);
                for (String questKey : newAssignments) {
                    QuestKeyParts parts = parseQuestKey(questKey);
                    progressStorage.setProgress(uuid, parts.category(), parts.questId(), 0, false, false, now);
                }
            }
        }
    }

    private Map<QuestType, List<String>> getAvailableQuestKeysByType() {
        Map<QuestType, List<String>> available = new EnumMap<>(QuestType.class);
        for (QuestType type : QuestType.values()) {
            available.put(type, new ArrayList<>());
        }
        for (QuestService service : questServices) {
            for (QuestDefinition quest : service.getQuests()) {
                available.get(quest.getType()).add(service.getQuestKey(quest));
            }
        }
        for (QuestType type : QuestType.values()) {
            available.put(type, List.copyOf(available.get(type)));
        }
        return available;
    }

    private List<String> pickRandomQuests(List<String> available, int count) {
        if (count <= 0 || available.isEmpty()) {
            return List.of();
        }
        List<String> shuffled = new ArrayList<>(available);
        Collections.shuffle(shuffled, random);
        return new ArrayList<>(shuffled.subList(0, Math.min(count, shuffled.size())));
    }

    private int requiredCount(QuestType type) {
        return switch (type) {
            case DAILY -> 1;
            case WEEKLY -> 1;
            case MONTHLY -> 2;
        };
    }

    private AssignedQuest resolveQuest(String questKey) {
        QuestKeyParts parts = parseQuestKey(questKey);
        if (parts == null) {
            return null;
        }
        for (QuestService service : questServices) {
            if (!service.getCategory().equalsIgnoreCase(parts.category())) {
                continue;
            }
            for (QuestDefinition quest : service.getQuests()) {
                if (quest.getId().equalsIgnoreCase(parts.questId())) {
                    return new AssignedQuest(service, quest);
                }
            }
        }
        return null;
    }

    private QuestKeyParts parseQuestKey(String questKey) {
        if (questKey == null) {
            return null;
        }
        int separatorIndex = questKey.indexOf(QUEST_SEPARATOR);
        if (separatorIndex <= 0 || separatorIndex >= questKey.length() - 1) {
            return null;
        }
        String category = questKey.substring(0, separatorIndex);
        String questId = questKey.substring(separatorIndex + 1);
        return new QuestKeyParts(category, questId);
    }

    private static class QuestKeyParts {
        private final String category;
        private final String questId;

        private QuestKeyParts(String category, String questId) {
            this.category = category;
            this.questId = questId;
        }

        public String category() {
            return category;
        }

        public String questId() {
            return questId;
        }
    }
}
