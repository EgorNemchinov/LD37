package com.jacktheogre.lightswitch.commands;

import com.jacktheogre.lightswitch.sprites.Actor;

/**
 * Created by luna on 12.12.16.
 */
public class StartMovingCommand extends ActorCommand{

    boolean keyboardControl;
    private Actor.Direction pastDirection, direction;
    public boolean disabled = false;

    public StartMovingCommand(Actor.Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean execute(Actor actor) {
        if(executed)
            return false;
        keyboardControl = actor.isKeyboardControl();
        actor.setKeyboardControl(true);
        pastDirection = actor.getCurDirection();
        actor.setDirection(direction);
        return true;
    }

    public Actor.Direction getDirection() {
        return direction;
    }

    @Override
    public void undo() {
        actor.setDirection(pastDirection);
        actor.setKeyboardControl(keyboardControl);
    }

    @Override
    public void redo() {
        actor.setKeyboardControl(true);
        actor.setDirection(direction);
    }
}
