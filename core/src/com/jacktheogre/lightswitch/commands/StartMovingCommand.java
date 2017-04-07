package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Player;

/**
 * Created by luna on 12.12.16.
 */
public class StartMovingCommand extends ActorCommand{

    boolean keyboardControl;
    private GameActor.Direction pastDirection, direction;
    public boolean disabled = false;


    public StartMovingCommand(GameActor.Direction direction, Player player) {
        this.direction = direction;
        this.player = player;
    }

    public StartMovingCommand(GameActor.Direction pastDirection, GameActor.Direction direction, boolean keyboardControl, Player player) {
        this.pastDirection = pastDirection;
        this.direction = direction;
        this.keyboardControl = keyboardControl;
        this.player = player;
        generated = true;
    }

    @Override
    public boolean execute(){
        if(executed)
            return false;
        keyboardControl = player.getGameActor().isKeyboardControl();
        player.getGameActor().setKeyboardControl(true);
        pastDirection = player.getGameActor().getDirection();
        player.getGameActor().setDirection(direction);
        return true;
    }

    public GameActor.Direction getDirection() {
        return direction;
    }

    @Override
    public void undo() {
        getGameActor().setDirection(pastDirection);
        getGameActor().setKeyboardControl(keyboardControl);
    }

    @Override
    public void redo() {
        getGameActor().setKeyboardControl(true);
        getGameActor().setDirection(direction);
    }

    @Override
    public String toLog() {
        return String.format("sm %s %s %d %d", pastDirection, direction, keyboardControl?1:0, isEnemyCommandToInt());
    }

    @Override
    public String toString() {
        return "StartMovingCommand. Direction: "+direction;
    }
}
