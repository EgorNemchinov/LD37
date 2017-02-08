package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.jacktheogre.lightswitch.objects.Trap;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Monster;

/**
 * Created by luna on 06.02.17.
 */

public class TrapTriggerCommand extends ActorCommand {

    private Trap trap;
    private Monster monster;

    public TrapTriggerCommand(Trap trap, Monster monster) {
        this.trap = trap;
        this.monster = monster;
    }

    @Override
    public boolean execute(GameActor gameActor) {
        if(executed)
            return false;
        trap.trigger(monster);
        Gdx.app.log("TrapTriggerCommand", "executed");
        return true;
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }

    @Override
    public String toString() {
        return "TrapTriggerCommand";
    }
}
