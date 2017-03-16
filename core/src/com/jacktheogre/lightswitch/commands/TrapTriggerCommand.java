package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.jacktheogre.lightswitch.objects.Trap;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Monster;
import com.jacktheogre.lightswitch.sprites.Player;

/**
 * Created by luna on 06.02.17.
 */

public class TrapTriggerCommand extends ActorCommand {

    private Trap trap;

    public TrapTriggerCommand(Trap trap, Player player) {
        this.trap = trap;
        this.player = player;
    }

    @Override
    public boolean execute(){
        if(executed)
            return false;
        if(!ClassReflection.isInstance(Monster.class, player.getGameActor())) {
            Gdx.app.error("TrapTriggerCommand", "Triggering human.");
            return false;
        }
        trap.trigger((Monster) player.getGameActor());
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
        return String.format("trapt %d %d", trap.getIndex(), isEnemyCommandToInt());
    }

    @Override
    public String toString() {
        return "TrapTriggerCommand";
    }
}
