package com.tbfmc.tbfmp.quests;

public class AssignedQuest {
    private final QuestService questService;
    private final QuestDefinition questDefinition;

    public AssignedQuest(QuestService questService, QuestDefinition questDefinition) {
        this.questService = questService;
        this.questDefinition = questDefinition;
    }

    public QuestService getQuestService() {
        return questService;
    }

    public QuestDefinition getQuestDefinition() {
        return questDefinition;
    }
}
