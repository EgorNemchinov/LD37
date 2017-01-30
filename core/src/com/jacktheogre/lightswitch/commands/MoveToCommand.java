package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.sprites.GameActor;

/**
 * Created by luna on 10.12.16.
 */
public class MoveToCommand extends ActorCommand {

    private int startX, startY;
    private int destinX, destinY;
    private boolean keyboardContol;

    public MoveToCommand(int x, int y) {
        destinX = x;
        destinY = y;
        executed = false;
    }

    public MoveToCommand(float x, float y, GameActor actor) {
        this(x, y);
        this.gameActor = actor;
    }

    public MoveToCommand(float x, float y) {
        this((int) x, (int) y);
    }

    public boolean execute(GameActor gameActor) {
        if(executed)
            return false;
        this.gameActor = gameActor;
        keyboardContol = gameActor.isKeyboardControl();
        gameActor.setKeyboardControl(false);
        startX = (int) gameActor.getX();
        startY = (int) gameActor.getY();
        gameActor.setTarget(new Vector2(destinX, destinY));
        gameActor.getAgent().makePath(gameActor);
        executed = true;
        return true;
    }

    @Override
    public void undo() {
        gameActor.setKeyboardControl(keyboardContol);
        gameActor.setTarget(new Vector2(startX, startY));
        gameActor.getAgent().makePath(gameActor);
        Gdx.app.log("MoveToCommand", "Undone. Moving back to (" + startX + ", " + startY + ")");
    }

    @Override
    public void redo() {
        gameActor.setKeyboardControl(false);
        gameActor.setTarget(new Vector2(destinX, destinY));
        gameActor.getAgent().makePath(gameActor);
        Gdx.app.log("MoveToCommand", "Redone. Moving to (" + destinX+ ", " + destinY + ")");
    }


    @Override
    public String toString() {
        return "MoveToCommand from (" + startX+", " + startY+") to (" + destinX + ", " + destinY+"). GameActor is " + gameActor;
    }
}
