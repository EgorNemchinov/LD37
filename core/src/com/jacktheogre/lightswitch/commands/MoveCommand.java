package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.ai.Agent;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Actor;

/**
 * Created by luna on 10.12.16.
 */
public class MoveCommand extends ActorCommand {

    private int startX, startY;
    private int destinX, destinY;

    public MoveCommand(int x, int y) {
        destinX = x;
        destinY = y;
        executed = false;
    }

    public MoveCommand(float x, float y) {
        this((int) x, (int) y);
    }

    public boolean execute(Actor actor) {
        if(executed)
            return false;
        this.actor = actor;
        startX = (int) actor.getX();
        startY = (int) actor.getY();
        actor.setTarget(new Vector2(destinX, destinY));
        actor.getAgent().makePath(actor);
        executed = true;
        return true;
    }

    @Override
    public void undo() {
        actor.setTarget(new Vector2(startX, startY));
        actor.getAgent().makePath(actor);
        Gdx.app.log("MoveCommand", "Undone. Moving back to (" + startX + ", " + startY + ")");
    }

    @Override
    public void redo() {
        actor.setTarget(new Vector2(destinX, destinY));
        actor.getAgent().makePath(actor);
        Gdx.app.log("MoveCommand", "Redone. Moving to (" + destinX+ ", " + destinY + ")");
    }


    @Override
    public String toString() {
        return "MoveCommand from (" + startX+", " + startY+") to (" + destinX + ", " + destinY+"). Actor is " + actor;
    }
}
