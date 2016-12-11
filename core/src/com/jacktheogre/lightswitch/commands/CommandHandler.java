package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.jacktheogre.lightswitch.screens.PlayScreen;

import java.util.Stack;

/**
 * Created by luna on 10.12.16.
 */
public class CommandHandler {

    // TODO: 10.12.16 if add another screen then change everything. check for type of screen and command
    private PlayScreen screen;
    private Stack<Command> commands;
    private int pointer = 0;
    private boolean newCommands;

    public CommandHandler(PlayScreen screen) {
        this.screen = screen;
        commands = new Stack<Command>();
    }

    public void update(float dt) {
        if(newCommands)
            executeCommands();
    }

    public void addCommand(Command command) {
        for (int i = 0; i < (commands.size() - pointer); i++) {
            commands.pop();
        }
        commands.push(command);
        newCommands = true;
    }

    public void executeCommands() {
        for (int i = pointer; i < commands.size(); i++) {
            if(commands.get(i) != null) {
                if(ActorCommand.class.isInstance(commands.get(i))){
                    ActorCommand cmd = (ActorCommand)commands.get(i);
                    if(cmd.actor == null)
                        cmd.execute(screen.getPlayer().getActor());
                    else cmd.execute(cmd.actor);
                } else if(GlobalCommand.class.isInstance(commands.get(i))){
                    ((GlobalCommand)commands.get(i)).execute();
                }
            }
        }
        if(commands.size() > 0)
            pointer = commands.size();
        newCommands = false;
    }

    public boolean undo() {
        if(pointer >= 1) {
            pointer--;
            Gdx.app.log("GameWorld", "Undoing "+commands.get(pointer));
            commands.get(pointer).undo();
            return true;
        } else
            return false;
    }


    public boolean redo() {
        if(pointer < commands.size()) {
            Gdx.app.log("GameWorld", "Redoing "+commands.get(pointer));
            commands.get(pointer).redo();
            pointer++;
            return true;
        } else
            return false;
    }

    public boolean newCommands() {
        return newCommands;
    }

    public void setNewCommands(boolean newCommands) {
        this.newCommands = newCommands;
    }

}
