package com.jacktheogre.lightswitch.commands;

import com.jacktheogre.lightswitch.sprites.Actor;

import java.util.Stack;

/**
 * Created by luna on 12.12.16.
 */
public class StopMovingCommand extends ActorCommand {

    private CommandHandler commandHandler;
    private Stack<Command> commands;
    private Actor.Direction direction;

    public StopMovingCommand(CommandHandler commandHandler, Actor.Direction direction, Actor actor) {
        this(commandHandler, direction);
        this.actor = actor;
    }

    public StopMovingCommand(CommandHandler commandHandler, Actor.Direction direction) {
        this.commandHandler = commandHandler;
        commands = commandHandler.getCommands();
        this.direction = direction;
    }

    // FIXME: 12.12.16 очень плохой пример но это временно
    @Override
    public boolean execute(Actor actor) {
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
