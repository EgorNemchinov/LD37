package com.jacktheogre.lightswitch.commands;

import com.jacktheogre.lightswitch.sprites.GameActor;

/**
 * Created by luna on 12.02.17.
 */

public class NullActorCommand extends ActorCommand {
    @Override
    public boolean execute(){
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
        return "";
    }
}

