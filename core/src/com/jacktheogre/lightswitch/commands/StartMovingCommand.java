package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Player;

/**
 * Created by luna on 12.12.16.
 */
public class StartMovingCommand extends ActorCommand{
    // TODO: 19.04.17 change toString and toLog. implement creating command from string

    boolean keyboardControl;
    private GameActor.Direction pastDirection, direction;
    public boolean disabled = false;

    private boolean horizontal;
    private GameActor.HorizontalDirection horizontalDirection;
    private GameActor.VerticalDirection verticalDirection;

    public StartMovingCommand(GameActor.HorizontalDirection horizontalDirection, Player player) {
        this.horizontalDirection = horizontalDirection;
        this.player = player;
        horizontal = true;
    }
    public StartMovingCommand(GameActor.VerticalDirection verticalDirection, Player player) {
        this.verticalDirection = verticalDirection;
        this.player = player;
        horizontal = false;
    }

//    public StartMovingCommand(GameActor.Direction pastDirection, GameActor.Direction direction, boolean keyboardControl, Player player) {
//        this.pastDirection = pastDirection;
//        this.direction = direction;
//        this.keyboardControl = keyboardControl;
//        this.player = player;
//        generated = true;
//    }

    @Override
    public boolean execute(){
        if(executed)
            return false;
        keyboardControl = player.getGameActor().isKeyboardControl();
        player.getGameActor().setKeyboardControl(true);
        pastDirection = player.getGameActor().getDirection();
        if(horizontal)
            player.getGameActor().getDirection().setHorizontalDirection(horizontalDirection);
        else
            player.getGameActor().getDirection().setVerticalDirection(verticalDirection);
        return true;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public GameActor.Direction getDirection() {
        return new GameActor.Direction(horizontal?horizontalDirection: GameActor.HorizontalDirection.NONE,
                horizontal? GameActor.VerticalDirection.NONE : verticalDirection);
    }

    public GameActor.HorizontalDirection getHorizontalDirection() {
        return horizontalDirection;
    }

    public GameActor.VerticalDirection getVerticalDirection() {
        return verticalDirection;
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
        return "StartMovingCommand. Direction: "+(horizontal?horizontalDirection:verticalDirection);
    }
}
