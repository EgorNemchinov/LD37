package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.ai.GraphPathImp;
import com.jacktheogre.lightswitch.sprites.Actor;

/**
 * Created by luna on 12.12.16.
 */
public class StopCommand extends ActorCommand {

    Vector2 velocity, curPos, nextPos;
    GraphPathImp path;
    boolean keyboardControl;

    public StopCommand(Actor actor) {
        this.actor = actor;
    }

    public StopCommand() {
    }

    @Override
    public boolean execute(Actor actor) {
        if(executed)
            return false;
        curPos = actor.getCurPos();
        nextPos = actor.getNextPos();
        velocity = actor.b2body.getLinearVelocity();
        path = actor.getPath();
        keyboardControl = actor.isKeyboardControl();
        actor.setKeyboardControl(false);
        actor.stop();
        return true;
    }

    @Override
    public void undo() {
        actor.setCurPosition(curPos);
        actor.setNextPosition(nextPos);
        actor.b2body.setLinearVelocity(velocity);
        actor.setPath(path);
        actor.setKeyboardControl(keyboardControl);
    }

    @Override
    public void redo() {
        curPos = actor.getCurPos();
        nextPos = actor.getNextPos();
        velocity = actor.b2body.getLinearVelocity();
        path = actor.getPath();
        keyboardControl = actor.isKeyboardControl();
        actor.setKeyboardControl(false);
        actor.stop();
    }

    @Override
    public String toString() {
        return "StopCommand";
    }
}
