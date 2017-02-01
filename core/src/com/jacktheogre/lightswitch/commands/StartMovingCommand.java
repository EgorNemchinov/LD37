package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.jacktheogre.lightswitch.sprites.GameActor;

/**
 * Created by luna on 12.12.16.
 */
public class StartMovingCommand extends ActorCommand{

    boolean keyboardControl;
    private GameActor.Direction pastDirection, direction;
    public boolean disabled = false;

    public StartMovingCommand(GameActor.Direction direction) {
        this.direction = direction;
    }

    public StartMovingCommand(GameActor.Direction direction, GameActor actor) {
        this(direction);
        this.gameActor = actor;
    }

    @Override
    public boolean execute(GameActor gameActor) {
        if(executed)
            return false;
        keyboardControl = gameActor.isKeyboardControl();
        gameActor.setKeyboardControl(true);
        pastDirection = gameActor.getDirection();
        gameActor.setDirection(direction);
        return true;
    }

    public GameActor.Direction getDirection() {
        return direction;
    }

    @Override
    public void undo() {
        gameActor.setDirection(pastDirection);
        gameActor.setKeyboardControl(keyboardControl);
    }

    @Override
    public void redo() {
        gameActor.setKeyboardControl(true);
        gameActor.setDirection(direction);
    }


    @Override
    public String toString() {
        return "StartMovingCommand. Direction: "+direction;
    }
}
