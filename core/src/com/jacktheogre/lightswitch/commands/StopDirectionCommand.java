package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Player;

/**
 * Created by luna on 12.12.16.
 */
public class StopDirectionCommand extends ActorCommand {

    private CommandHandler commandHandler;
    private GameActor.Direction direction;

    public StopDirectionCommand(CommandHandler commandHandler, GameActor.Direction direction, Player player) {
        this(commandHandler, direction);
        this.player = player;
    }

    public StopDirectionCommand(CommandHandler commandHandler, GameActor.Direction direction) {
        this.commandHandler = commandHandler;
        this.direction = direction;
    }

    @Override
    public boolean execute(){
        if(executed)
            return false;
//        commandHandler.stopMoving(direction);
        Gdx.app.log("StopMC", "executed, dir: " + direction);
        return true;
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }

    @Override
    public String toLog() {
        return String.format("sd %s %d", direction, isEnemyCommandToInt());
    }

    @Override
    public String toString() {
        return "StopDirectionCommand. Direction:"+direction;
    }
}
