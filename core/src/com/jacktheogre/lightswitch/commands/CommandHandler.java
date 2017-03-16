package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.multiplayer.MessageHandler;
import com.jacktheogre.lightswitch.replay.Logger;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Player;

import java.util.Stack;

/**
 * Created by luna on 10.12.16.
 */
public class CommandHandler {

    public enum ScreenState {
        GENERATINGSCREEN, PLAYSCREEN
    }
    private ScreenState screenState;
    private Screen screen;
    private Stack<Command> commands;
    private int pointer = 0;
    private boolean newCommands;
    private Logger logger;
    private float timeSinceSynchronizing = 0;

    public CommandHandler(Screen screen) {
        this.screen = screen;
        commands = new Stack<Command>();
        logger = new Logger();
        screenState = ScreenState.GENERATINGSCREEN;
    }

    public void update(float dt) {
        timeSinceSynchronizing += dt;
        if(timeSinceSynchronizing > Constants.SYNCRONIZING_FREQUENCY_TIME) {
            if(screenState == ScreenState.PLAYSCREEN)
                synchronizePosition();
            timeSinceSynchronizing %= Constants.SYNCRONIZING_FREQUENCY_TIME;
        }
        if(newCommands) {
            if (screenState == ScreenState.PLAYSCREEN) {
                executeCommandsPlay();
            } else if (screenState == ScreenState.GENERATINGSCREEN) {
                executeCommandsGenerate();
            }
        }
    }

    private void synchronizePosition() {
        Player player = ((PlayScreen) screen).getPlayer();
        SetPositionCommand cmd = new SetPositionCommand((int) player.getGameActor().b2body.getPosition().x,
                (int) player.getGameActor().b2body.getPosition().y, player);
        logger.logStringToFile(cmd.toLog());
//        MessageHandler.sendMessage(cmd.toLog());
        // TODO: 13.02.17  log and\or send message with setposition command
    }

    public void addCommand(Command command) {
        int pops = commands.size() - pointer;
        for (int i = 0; i < pops; i++) {
            commands.pop();
        }
        commands.push(command);
        newCommands = true;
    }

    public void executeCommandsPlay() {
        for (int i = pointer; i < commands.size(); i++) {
            if(commands.get(i) != null) {
                if(ClassReflection.isInstance(ActorCommand.class, commands.get(i))){
                    ActorCommand cmd = (ActorCommand)commands.get(i);
                    if(cmd.player!= null) {
                        if(cmd.execute())
                            logger.logStringToFile(cmd.toLog());
                    }
                } else if(ClassReflection.isInstance(GlobalCommand.class, commands.get(i))){
                    GlobalCommand cmd = (GlobalCommand)commands.get(i);
                    if(cmd.execute())
                        logger.logStringToFile(cmd.toLog());
                }
            }
        }
        if(commands.size() > 0)
            pointer = commands.size();
        newCommands = false;
    }

    public void executeCommandsGenerate() {
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

    public void stopMoving(GameActor.Direction direction) {
        logger.logStringToFile("sd " + direction.toString());
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
                addCommand(new StopCommand(((PlayScreen) screen).getPlayer()));
                return;
            } else if(ClassReflection.isInstance(StartMovingCommand.class, cmd)) {
                if(((StartMovingCommand) cmd).getDirection() != direction) {
                    if(((StartMovingCommand) cmd).disabled) {
                        continue;
                    } else {
                        addCommand(new StartMovingCommand(((StartMovingCommand) cmd).getDirection()));
                        return;
                    }
                }
            }
        }
        addCommand(new StopCommand(((PlayScreen) screen).getPlayer()));
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

    public ScreenState getScreenState() {
        return screenState;
    }

    public void setScreenState(ScreenState screenState) {
        this.screenState = screenState;
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
