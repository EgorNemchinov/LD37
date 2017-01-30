package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.ai.GraphPathImp;
import com.jacktheogre.lightswitch.sprites.GameActor;

/**
 * Created by luna on 12.12.16.
 */
public class StopCommand extends ActorCommand {

    Vector2 velocity, curPos, nextPos;
    GraphPathImp path;
    boolean keyboardControl;

    public StopCommand() {
    }

    public StopCommand(GameActor gameActor) {
        this.gameActor = gameActor;
    }

    @Override
    public boolean execute(GameActor gameActor) {
        if(executed)
            return false;
        curPos = gameActor.getCurPos();
        nextPos = gameActor.getNextPos();
        velocity = gameActor.b2body.getLinearVelocity();
        path = gameActor.getPath();
        keyboardControl = gameActor.isKeyboardControl();
        gameActor.setKeyboardControl(false);
        gameActor.stop();
        return true;
    }

    @Override
    public void undo() {
        gameActor.setCurPosition(curPos);
        gameActor.setNextPosition(nextPos);
        gameActor.b2body.setLinearVelocity(velocity);
        gameActor.setPath(path);
        gameActor.setKeyboardControl(keyboardControl);
    }

    @Override
    public void redo() {
        curPos = gameActor.getCurPos();
        nextPos = gameActor.getNextPos();
        velocity = gameActor.b2body.getLinearVelocity();
        path = gameActor.getPath();
        keyboardControl = gameActor.isKeyboardControl();
        gameActor.setKeyboardControl(false);
        gameActor.stop();
    }

    @Override
    public String toString() {
        return "StopCommand";
    }
}
