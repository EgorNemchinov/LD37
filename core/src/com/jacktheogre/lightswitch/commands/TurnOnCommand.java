package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.reflect.ClassReflection;
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
        if(ClassReflection.isInstance(PlayScreen.class, screen)) {
            this.screen = (PlayScreen) screen;
        }
        this.lighting = this.screen.getLighting();
    }

    public TurnOnCommand(PlayScreen screen, boolean activeBefore) {
        super(screen);
        this.screen = screen;
        this.activeBefore = activeBefore;
    }

    @Override
    public boolean execute() {
        if(executed)
            return false;
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

    @Override
    public String toLog() {
        return String.format("ton %d", activeBefore?1:0);
    }

    @Override
    public String toString() {
        return "TurnOnCommand";
    }
}
