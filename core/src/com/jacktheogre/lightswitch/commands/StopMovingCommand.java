package com.jacktheogre.lightswitch.commands;

import com.jacktheogre.lightswitch.sprites.GameActor;

import java.util.Stack;

/**
 * Created by luna on 12.12.16.
 */
public class StopMovingCommand extends ActorCommand {

    private CommandHandler commandHandler;
    private Stack<Command> commands;
    private GameActor.Direction direction;

    public StopMovingCommand(CommandHandler commandHandler, GameActor.Direction direction, GameActor gameActor) {
        this(commandHandler, direction);
        this.gameActor = gameActor;
    }

    public StopMovingCommand(CommandHandler commandHandler, GameActor.Direction direction) {
        this.commandHandler = commandHandler;
        commands = commandHandler.getCommands();
        this.direction = direction;
    }

    @Override
    public boolean execute(GameActor gameActor) {
        if(executed)
            return false;
        commandHandler.stopMoving(direction);
        return true;
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }


    @Override
    public String toString() {
        return "StopMovingCommand. Direction:"+direction;
    }
}
