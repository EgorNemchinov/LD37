package com.jacktheogre.lightswitch.commands;

/**
 * Created by luna on 10.12.16.
 */
public abstract class Command {
    protected boolean executed;

    public abstract void undo();
    public abstract void redo();
    public abstract String toLog();
    protected boolean generated = false;

    @Override
    public String toString() {
        return "Command";
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }
}
