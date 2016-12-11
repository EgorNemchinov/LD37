package com.jacktheogre.lightswitch.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.commands.TeleportCommand;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Actor;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;

import java.util.Random;

/**
 * Created by luna on 10.12.16.
 */
public class Teleport extends InteractiveObject {

    private int x, y;
    private Array<Teleport> others;

    public Teleport(GeneratingScreen screen, int x, int y) {
        super(screen, x, y);
        this.x = x;
        this.y = y;
        fixture.setUserData(this);
        setCategoryFilter(Constants.INTERACTIVE_BIT);
        texture = Assets.getAssetLoader().teleport;
        others = new Array<Teleport>();
    }

    public void activate(Actor actor) {
//        Gdx.app.log("Teleport", "Activated");

        // TODO: 11.12.16 choose out of list of teleportation points
        screen.getCommandHandler().addCommand(new TeleportCommand(actor, this, randomTeleport()));
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, x, y, 2*bounds.radius, 2*bounds.radius);
    }

    public Array<Teleport> getOthers() {
        return others;
    }

    public void setOthers(Array<Teleport> others) {
        this.others = others;
    }

    public void addTeleport(Teleport tp) {
        if(tp != null)
            others.add(tp);
    }

    private Teleport randomTeleport() {
        Random random = new Random();
        return others.size > 1 ? others.get(random.nextInt(others.size - 1)) : others.get(0); // TODO: 11.12.16 or 1
    }
}
