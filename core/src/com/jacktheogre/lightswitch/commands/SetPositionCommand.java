package com.jacktheogre.lightswitch.commands;

import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Player;

/**
 * Created by luna on 12.02.17.
 */

public class SetPositionCommand extends ActorCommand {

    private int startX, startY;
    private int destinX, destinY;

    public SetPositionCommand(int x, int y, Player player) {
        this.destinX = x;
        this.destinY = y;
        this.player = player;
    }

    @Override
    public boolean execute(){
        if(executed)
            return false;
        this.startX = (int) player.getGameActor().b2body.getPosition().x;
        this.startY = (int) player.getGameActor().b2body.getPosition().y;
        player.setPosition(destinX, destinY);
        return true;
    }

    @Override
    public void undo() {
        // TODO: 13.02.17 undo && redo
    }

    @Override
    public void redo() {

    }

    @Override
    public String toLog() {
        return String.format("setpos %d %d %d %d %d", startX, startY, destinX, destinY, isEnemyCommandToInt());
    }

    @Override
    public String toString() {
        return "SetPositionCommand";
    }
}
