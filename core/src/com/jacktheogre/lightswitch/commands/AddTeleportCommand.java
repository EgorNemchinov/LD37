package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;

/**
 * Created by luna on 13.12.16.
 */
public class AddTeleportCommand extends GlobalCommand {

    private Array<Teleport> teleports;
    private int x, y;
    private Teleport teleport;
    private GeneratingScreen screen;

    public AddTeleportCommand(GeneratingScreen generatingScreen, int x, int y, Array<Teleport> teleports) {
        super(generatingScreen);
        this.screen = generatingScreen;
        this.x = x;
        this.y = y;
        this.teleports = teleports;
        teleport = new Teleport(screen, x, y, false);
    }

    @Override
    public boolean execute() {
        if(executed)
            return false;
        if(!screen.maxTeleports()) {
            if(screen.existsTeleport(x, y))
                return false;

            screen.getTeleports().add(teleport);
            if(screen.maxTeleports())
                screen.getTeleportButton().disable();
        }
        return true;
    }

    @Override
    public void undo() {
        if(!screen.maxTeleports()) {
            screen.getTeleportButton().enable();
        }
        teleports.removeValue(teleport, true);
        if(screen.getTeleports().size == 0 && screen.getTraps().size == 0)
            screen.getUndo().disable();
    }

    @Override
    public void redo() {
        if(screen.maxTeleports()) {
            teleports.add(teleport);
            if(screen.maxTeleports())
                screen.getTeleportButton().disable();
        }
    }

    @Override
    public String toLog() {
        return "";
    }

    @Override
    public String toString() {
        return "AddTeleportCommand";
    }
}
