package com.tbfmc.tbfmp.quests;

public class QuestReward {
    private final double money;
    private final int diamonds;
    private final int iron;

    public QuestReward(double money, int diamonds, int iron) {
        this.money = money;
        this.diamonds = diamonds;
        this.iron = iron;
    }

    public double getMoney() {
        return money;
    }

    public int getDiamonds() {
        return diamonds;
    }

    public int getIron() {
        return iron;
    }
}
