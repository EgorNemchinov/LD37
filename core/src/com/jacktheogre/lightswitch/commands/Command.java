package com.jacktheogre.lightswitch.commands;

import com.jacktheogre.lightswitch.sprites.Actor;

/**
 * Created by luna on 10.12.16.
 */
public abstract class Command {
    protected boolean executed;

    public abstract void undo();
    public abstract void redo();

    @Override
    public String toString() {
        return "Command";
    }

}
