package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.ai.Node;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.screens.LevelChoosingScreen;
import com.jacktheogre.lightswitch.sprites.Player;

import java.util.Random;

/**
 * Created by luna on 11.12.16.
 */
public class TeleportCommand extends ActorCommand {

    private Teleport start, destination;

    public TeleportCommand(Teleport start, Teleport destination, Player player) {
        this.start = start;
        this.destination = destination;
        this.player = player;
    }

    @Override
    public boolean execute() {
        if(executed)
            return false;
//        player.getGameActor().moveTo(destination.getX(), destination.getY());
        if(start.isOpen() && destination.isOpen()) {
            player.getGameActor().b2body.setTransform(new Vector2(destination.getX() + 8, destination.getY() + 8), 0);
            player.getGameActor().setRemakingPath(true);
            start.close();
            destination.close();
        }
        executed = true;
//        Gdx.app.log("TeleportCommand", "executed");
        return true;
    }

    @Override
    public void undo() {
        getGameActor().b2body.setTransform(new Vector2(start.getX(), start.getY()), 0);
    }

    @Override
    public void redo() {
        getGameActor().b2body.setTransform(new Vector2(destination.getX(), destination.getY()), 0);
    }

    @Override
    public String toLog() {
        return String.format("teleport %d %d %d", start.getIndex(), destination.getIndex(), isEnemyCommandToInt());
    }

    @Override
    public String toString() {
        return "TeleportCommand";
    }
}
