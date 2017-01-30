package com.jacktheogre.lightswitch.commands;

import com.jacktheogre.lightswitch.sprites.GameActor;

/**
 * Created by luna on 10.12.16.
 */
public abstract class ActorCommand extends Command{

    protected GameActor gameActor;

    public abstract boolean execute(GameActor gameActor);
    public void setGameActor(GameActor gameActor) {
        this.gameActor = gameActor;
    }

}
