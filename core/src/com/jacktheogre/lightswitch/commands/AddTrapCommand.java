package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.jacktheogre.lightswitch.objects.InteractiveObject;
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
        screen.getClearButton().enable();
        return true;
    }

    //// TODO: 06.02.17 undo&&redo
    @Override
    public void undo() {
        screen.getTraps().removeValue(trap, true);
        InteractiveObject.Indexer.decrement();
        if(!screen.maxTraps())
            screen.getTrapButton().enable();
        if(screen.getTeleports().size == 0 && screen.getTraps().size == 0)
            screen.getUndo().disable();
        else
            screen.getUndo().enable();

        if(screen.anyObjects())
            screen.getClearButton().enable();
        else
            screen.getClearButton().disable();
    }

    @Override
    public void redo() {
        if(!screen.maxTraps()) {
            screen.getTraps().add(trap);
            InteractiveObject.Indexer.increment();
            if(screen.maxTraps())
                screen.getTrapButton().disable();
        }
        if(screen.anyObjects())
            screen.getClearButton().enable();
        else
            screen.getClearButton().disable();
    }

    @Override
    public String toLog() {
        return "";
    }

    @Override
    public String toString() {
        return "AddTrapCommand";
    }
}
