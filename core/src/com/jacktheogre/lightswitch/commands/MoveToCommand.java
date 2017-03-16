package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.sprites.EnemyPlayer;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Player;

/**
 * Created by luna on 10.12.16.
 */
public class MoveToCommand extends ActorCommand {

    private int startX, startY;
    private int destinX, destinY;
    private boolean keyboardContol;

    public MoveToCommand(int x, int y) {
        startX = 0;
        startY = 0;
        destinX = x;
        destinY = y;
        executed = false;
    }

    public MoveToCommand(float x, float y, Player player) {
        this(x, y);
        this.player = player;
    }

    public MoveToCommand(float x, float y) {
        this((int) x, (int) y);
    }

    public MoveToCommand(int startX, int startY, int destinX, int destinY, boolean keyboardContol, Player player) {
        this.startX = startX;
        this.startY = startY;
        this.destinX = destinX;
        this.destinY = destinY;
        this.keyboardContol = keyboardContol;
        this.player = player;
        generated = true;
    }

    public MoveToCommand(Player destination, Player object) {
        this(destination.getGameActor().b2body.getPosition().x, destination.getGameActor().b2body.getPosition().y, object);
    }

    // TODO: 13.02.17 works properly when generated fully(with start xy)?
    public boolean execute(){
        if(executed)
            return false;
        setGameActor(player.getGameActor());
        keyboardContol = player.getGameActor().isKeyboardControl();
        player.getGameActor().setKeyboardControl(false);
        if(!generated) {
            startX = (int) player.getGameActor().getX();
            startY = (int) player.getGameActor().getY();
        }
        player.getGameActor().setTarget(new Vector2(destinX, destinY));
        player.getGameActor().getAgent().makePath(player.getGameActor());
        executed = true;
        return true;
    }

    @Override
    public void undo() {
        getGameActor().setKeyboardControl(keyboardContol);
        getGameActor().setTarget(new Vector2(startX, startY));
        getGameActor().getAgent().makePath(getGameActor());
        Gdx.app.log("MoveToCommand", "Undone. Moving back to (" + startX + ", " + startY + ")");
    }

    @Override
    public void redo() {
        getGameActor().setKeyboardControl(false);
        getGameActor().setTarget(new Vector2(destinX, destinY));
        getGameActor().getAgent().makePath(getGameActor());
        Gdx.app.log("MoveToCommand", "Redone. Moving to (" + destinX+ ", " + destinY + ")");
    }

    // TODO: 08.02.17 consider that enemy might be controlled by moveto
    @Override
    public String toLog() {
        return String.format("mt %d %d %d %d %d %d", startX, startY, destinX, destinY, keyboardContol?1:0, isEnemyCommandToInt());
    }


    @Override
    public String toString() {
        return "MoveToCommand from (" + startX+", " + startY+") to (" + destinX + ", " + destinY+"). GameActor is " + getGameActor();
    }
}
