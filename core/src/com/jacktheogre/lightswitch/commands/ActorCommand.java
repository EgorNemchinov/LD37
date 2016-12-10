package com.jacktheogre.lightswitch.commands;

import com.jacktheogre.lightswitch.sprites.Actor;

/**
 * Created by luna on 10.12.16.
 */
public abstract class ActorCommand extends Command{

    protected Actor actor;

    public abstract boolean execute(Actor actor);
    public void setActor(Actor actor) {
        this.actor = actor;
    }

}
