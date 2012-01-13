package net.citizensnpcs.api.npc;

public interface Goal {
    public int getPriority();

    public void reset();

    public boolean shouldBegin();

    public boolean shouldContinue();

    public void start();

    public void update();
}