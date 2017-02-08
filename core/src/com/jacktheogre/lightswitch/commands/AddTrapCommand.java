package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.jacktheogre.lightswitch.objects.Trap;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;

/**
 * Created by luna on 05.02.17.
 */

public class AddTrapCommand extends GlobalCommand {

    private int x, y;
    private Trap trap;
    private GeneratingScreen screen;

    public AddTrapCommand(GeneratingScreen screen, int x, int y) {
        super(screen);
        this.screen = screen;
        this.x = x;
        this.y = y;
        trap = new Trap(screen, x, y, false);
    }

    @Override
    public boolean execute() {
        if(executed)
            return false;
        if(!screen.maxTraps()) {
            if(screen.existsTrap(x, y))
                return false;

            screen.getTraps().add(trap);
            if(screen.maxTraps())
                screen.getTrapButton().disable();
        }
        return true;
    }

    //// TODO: 06.02.17 undo&&redo
    @Override
    public void undo() {
        if(!screen.maxTraps())
            screen.getUndo().enable();
        screen.getTraps().removeValue(trap, true);
        if(screen.getTeleports().size == 0 && screen.getTraps().size == 0)
            screen.getUndo().disable();

    }

    @Override
    public void redo() {
        if(!screen.maxTraps()) {
            screen.getTraps().add(trap);
            if(screen.maxTraps())
                screen.getTrapButton().disable();
        }
    }

    @Override
    public String toString() {
        return "AddTrapCommand";
    }
}
