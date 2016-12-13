package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.objects.InteractiveObject;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;

/**
 * Created by luna on 13.12.16.
 */
public class AddTeleportCommand extends GlobalCommand {

    private int x, y;
    private Array<InteractiveObject> objects;
    private Teleport teleport;

    public AddTeleportCommand(GeneratingScreen generatingScreen, int x, int y, Array<InteractiveObject> objects) {
        super(generatingScreen);
        this.x = x;
        this.y = y;
        this.objects = objects;
        teleport = new Teleport((GeneratingScreen) screen, x, y);
    }

    @Override
    public boolean execute() {
        if(executed)
            return false;
        objects.add(teleport);
        return true;
    }

    @Override
    public void undo() {
        objects.removeValue(teleport, true);
    }

    @Override
    public void redo() {
        objects.add(teleport);
    }
}
