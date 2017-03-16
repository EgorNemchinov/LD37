package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Screen;

/**
 * Created by luna on 12.02.17.
 */

public class NullGlobalCommand extends GlobalCommand {

    public NullGlobalCommand(Screen screen) {
        super(screen);
    }

    @Override
    public boolean execute() {
        return true;
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }

    @Override
    public String toLog() {
        return "";
    }
}
