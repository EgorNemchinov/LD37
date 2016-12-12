package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.tools.Lighting;

/**
 * Created by luna on 10.12.16.
 */
public class TurnOnCommand extends GlobalCommand{

    private PlayScreen screen;
    private Lighting lighting;
    private boolean activeBefore;

    public TurnOnCommand(Screen screen) {
        super(screen);
        if(PlayScreen.class.isInstance(screen)) {
            this.screen = (PlayScreen) screen;
        }
        this.lighting = this.screen.getLighting();
    }

    @Override
    public boolean execute() {
        if(executed)
            return false;
        Gdx.app.log("Space", "2");
        activeBefore = lighting.lightsOn();
        lighting.turnOn();
        executed = true;
        return true;
    }

    @Override
    public void undo() {
        if(!activeBefore)
            lighting.turnOff();
    }

    @Override
    public void redo() {
        lighting.turnOn();
    }
}
