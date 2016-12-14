package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Actor;

import java.util.Stack;

/**
 * Created by luna on 10.12.16.
 */
public class CommandHandler {

    // TODO: 10.12.16 if add another screen then change everything. check for type of screen and command
    private Screen screen;
    private Stack<Command> commands;
    private int pointer = 0;
    private boolean newCommands;

    public CommandHandler(Screen screen) {
        this.screen = screen;
        commands = new Stack<Command>();
    }

    public void update(float dt) {
        if(newCommands) {
            if (ClassReflection.isInstance(PlayScreen.class, screen)) {
                executeCommandsPlay();
            } else if (ClassReflection.isInstance(GeneratingScreen.class, screen)) {
                executeCommandsGenerate();
            }
        }
    }

    public void addCommand(Command command) {
        int pops = commands.size() - pointer;
//        Gdx.app.log("iterations", ""+(commands.size()-pointer));
        for (int i = 0; i < pops; i++) {
            commands.pop();
        }
        commands.push(command);
        newCommands = true;
    }

    public void executeCommandsPlay() {
        PlayScreen screen = (PlayScreen) this.screen;
        for (int i = pointer; i < commands.size(); i++) {
            if(commands.get(i) != null) {
                if(ClassReflection.isInstance(ActorCommand.class, commands.get(i))){
                    ActorCommand cmd = (ActorCommand)commands.get(i);
                    if(cmd.actor == null)
                        cmd.execute(screen.getPlayer().getActor());
                    else cmd.execute(cmd.actor);
                } else if(ClassReflection.isInstance(GlobalCommand.class, commands.get(i))){
                    ((GlobalCommand)commands.get(i)).execute();
                }
            }
        }
        if(commands.size() > 0)
            pointer = commands.size();
        newCommands = false;
    }

    public void executeCommandsGenerate() {
        GeneratingScreen screen = (GeneratingScreen) this.screen;
        for (int i = pointer; i < commands.size(); i++) {
            if(commands.get(i) != null) {
                 if(ClassReflection.isInstance(GlobalCommand.class, commands.get(i))){
                    ((GlobalCommand)commands.get(i)).execute();
                }
            }
        }
        if(commands.size() > 0)
            pointer = commands.size();
        newCommands = false;
    }

    public Stack<Command> getCommands() {
        return commands;
    }

    public void stopMoving(Actor.Direction direction) {
        for (int i = commands.size() - 1; i >= 0 ; i--) {
            Command cmd = commands.get(i);
            if(ClassReflection.isInstance(StartMovingCommand.class, cmd)) {
                if (((StartMovingCommand) cmd).getDirection() == direction) {
                    if (!((StartMovingCommand) cmd).disabled)
                        ((StartMovingCommand) cmd).disabled = true;
                }
            }
        }
        for (int i = commands.size() - 1; i >= 0 ; i--) {
            Command cmd = commands.get(i);
            if(ClassReflection.isInstance(StopCommand.class, cmd) || ClassReflection.isInstance(MoveToCommand.class, cmd)) {
                addCommand(new StopCommand());
                return;
            } else if(ClassReflection.isInstance(StartMovingCommand.class, cmd)) {
                if(((StartMovingCommand) cmd).getDirection() != direction) {
                    if(((StartMovingCommand) cmd).disabled) {
                        continue;
                    } else {
                        commands.add(new StartMovingCommand(((StartMovingCommand) cmd).getDirection()));
                        return;
                    }
                }
            }
        }
        addCommand(new StopCommand());
    }

    public boolean undo() {
        if(pointer >= 1) {
            pointer--;
//            Gdx.app.log("GameWorld", "Undoing "+commands.get(pointer));
            commands.get(pointer).undo();
            ((GeneratingScreen)screen).getRedo().enable();
//            if(pointer == 0)
//                ((GeneratingScreen)screen).getUndo().disable();
//            else
//                ((GeneratingScreen)screen).getUndo().enable();
            return true;
        } else {
//            ((GeneratingScreen)screen).getUndo().disable();
            return false;
        }
    }


    public boolean redo() {
        if(pointer < commands.size()) {
//            Gdx.app.log("GameWorld", "Redoing "+commands.get(pointer));
            commands.get(pointer).redo();
            pointer++;
            ((GeneratingScreen)screen).getUndo().enable();
            if(commands.size() == pointer)
                ((GeneratingScreen)screen).getRedo().disable();
//            if(pointer == commands.size()-1)
//                ((GeneratingScreen)screen).getRedo().disable();
//            else
//                ((GeneratingScreen)screen).getRedo().enable();
            return true;
        } else
            return false;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public boolean newCommands() {
        return newCommands;
    }

    public void setNewCommands(boolean newCommands) {
        this.newCommands = newCommands;
    }

}
