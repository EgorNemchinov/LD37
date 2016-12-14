package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
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
        teleport = new Teleport((GeneratingScreen) screen, x, y, false);
    }

    @Override
    public boolean execute() {
        if(executed)
            return false;
        if(((GeneratingScreen)screen).addable()) {
            objects.add(teleport);
            if(!((GeneratingScreen)screen).addable())
                ((GeneratingScreen)screen).getTeleportButton().disable();
        }
//        Gdx.app.log("objects", ""+objects.size);
        return true;
    }

    @Override
    public void undo() {
        if(!((GeneratingScreen)screen).addable()) {
            ((GeneratingScreen)screen).getTeleportButton().enable();
        }
        objects.removeValue(teleport, true);
        if(((GeneratingScreen)screen).getObjects().size == 0)
            ((GeneratingScreen)screen).getUndo().disable();
    }

    @Override
    public void redo() {
        if(((GeneratingScreen)screen).addable()) {
            objects.add(teleport);
            if(!((GeneratingScreen)screen).addable())
                ((GeneratingScreen)screen).getTeleportButton().disable();
        }
    }
}
