package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.sprites.Actor;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 11.12.16.
 */
public class TeleportCommand extends ActorCommand {

    private Teleport start, destination;

    public TeleportCommand(Actor actor, Teleport start, Teleport destination) {
        this.actor = actor;
        this.start = start;
        this.destination = destination;
    }

    @Override
    public boolean execute(Actor actor) {
        if(executed)
            return false;
//        actor.moveTo(destination.getX(), destination.getY());
        if(start.isOpen() && destination.isOpen()) {
            actor.b2body.setTransform(new Vector2(destination.getX(), destination.getY()), 0);
            actor.setRemakingPath(true);
            start.close();
            destination.close();
        }
        executed = true;
        return true;
    }


    // TODO: 11.12.16 take into account actor.isTeleportReady() somehow.
    @Override
    public void undo() {
        actor.b2body.setTransform(new Vector2(start.getX(), start.getY()), 0);
    }

    @Override
    public void redo() {
        actor.b2body.setTransform(new Vector2(destination.getX(), destination.getY()), 0);
    }
}
