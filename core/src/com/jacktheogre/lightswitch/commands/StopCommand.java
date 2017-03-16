package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.ai.GraphPathImp;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Player;

/**
 * Created by luna on 12.12.16.
 */
public class StopCommand extends ActorCommand {

    Vector2 velocity, curPos, nextPos;
    GraphPathImp path;
    boolean keyboardControl;

    public StopCommand(Player player) {
        this.player = player;
    }

    // TODO: 09.02.17 implement another way
    @Override
    public boolean execute(){
        if(executed)
            return false;
        curPos = player.getGameActor().getCurPos();
        nextPos = player.getGameActor().getNextPos();
        velocity = player.getGameActor().b2body.getLinearVelocity();
        path = player.getGameActor().getPath();
        keyboardControl = player.getGameActor().isKeyboardControl();
        player.getGameActor().setKeyboardControl(false);
        player.getGameActor().stop();
        return true;
    }

    @Override
    public void undo() {
        getGameActor().setCurPosition(curPos);
        getGameActor().setNextPosition(nextPos);
        getGameActor().b2body.setLinearVelocity(velocity);
        getGameActor().setPath(path);
        getGameActor().setKeyboardControl(keyboardControl);
    }

    @Override
    public void redo() {
        curPos = getGameActor().getCurPos();
        nextPos = getGameActor().getNextPos();
        velocity = getGameActor().b2body.getLinearVelocity();
        path = getGameActor().getPath();
        keyboardControl = getGameActor().isKeyboardControl();
        getGameActor().setKeyboardControl(false);
        getGameActor().stop();
    }

    @Override
    public String toLog() {
        return String.format("stop %d", isEnemyCommandToInt());
    }

    @Override
    public String toString() {
        return "StopCommand";
    }
}
