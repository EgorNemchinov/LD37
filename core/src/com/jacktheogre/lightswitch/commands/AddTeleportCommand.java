package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;

/**
 * Created by luna on 13.12.16.
 */
public class AddTeleportCommand extends GlobalCommand {

    private int x, y;
    private Teleport teleport;
    private GeneratingScreen screen;

    public AddTeleportCommand(GeneratingScreen generatingScreen, int x, int y, Teleport partner) {
        super(generatingScreen);
        this.screen = generatingScreen;
        this.x = x;
        this.y = y;
        teleport = new Teleport(screen, x, y, false);
        if(partner != null) {
            Teleport.connect(teleport, partner);
            screen.setUnpairedTeleport(null);
        } else {
            screen.setUnpairedTeleport(teleport);
        }
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

    // TODO: 18.03.17 implement pairing-unpairing
    @Override
    public void undo() {
        if(!screen.maxTeleports()) {
            screen.getTeleportButton().enable();
        }
        screen.getTeleports().removeValue(teleport, true);
        if(teleport.getPartner() != null) {
            teleport.getPartner().removePartner();
            screen.setUnpairedTeleport(teleport.getPartner());
        } else {
            screen.setUnpairedTeleport(null);
        }
        if(screen.getTeleports().size == 0 && screen.getTraps().size == 0)
            screen.getUndo().disable();
    }

    @Override
    public void redo() {
        if(screen.maxTeleports()) {
            screen.getTeleports().add(teleport);
            if(teleport.getPartner() != null) {
                Teleport.connect(teleport, teleport.getPartner());
                screen.setUnpairedTeleport(null);
            } else {
                screen.setUnpairedTeleport(teleport);
            }
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
