package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.sprites.GameActor;

/**
 * Created by luna on 11.12.16.
 */
public class TeleportCommand extends ActorCommand {

    private Teleport start, destination;

    public TeleportCommand(GameActor gameActor, Teleport start, Teleport destination) {
        this.gameActor = gameActor;
        this.start = start;
        this.destination = destination;
    }

    @Override
    public boolean execute(GameActor gameActor) {
        if(executed)
            return false;
//        gameActor.moveTo(destination.getX(), destination.getY());
        if(start.isOpen() && destination.isOpen()) {
            gameActor.b2body.setTransform(new Vector2(destination.getX(), destination.getY()), 0);
            gameActor.setRemakingPath(true);
            start.close();
            destination.close();
        }
        executed = true;
//        Gdx.app.log("TeleportCommand", "executed");
        return true;
    }


    @Override
    public void undo() {
        gameActor.b2body.setTransform(new Vector2(start.getX(), start.getY()), 0);
    }

    @Override
    public void redo() {
        gameActor.b2body.setTransform(new Vector2(destination.getX(), destination.getY()), 0);
    }

    @Override
    public String toString() {
        return "TeleportCommand";
    }
}
