package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Screen;
import com.jacktheogre.lightswitch.screens.PlayScreen;

/**
 * Created by luna on 10.12.16.
 */
public abstract class GlobalCommand extends Command {
    protected Screen screen;

    public GlobalCommand(Screen screen) {
        this.screen = screen;
    }

    public abstract boolean execute();

    public void setScreen(Screen screen) {
        this.screen = screen;
    }
}
