package com.jacktheogre.lightswitch.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Player;

/**
 * Created by luna on 13.02.17.
 */

public class NullInteractiveObject extends InteractiveObject {
    public NullInteractiveObject(LightSwitch game) {
        super(new GeneratingScreen(game), 0, 0, false);
    }

    @Override
    public void render(SpriteBatch spriteBatch, float dt) {

    }

    @Override
    public boolean activate(Player player) {
        return false;
    }

    @Override
    protected void initGraphics() {

    }

    @Override
    protected TextureRegion getFrame(float dt) {
        return null;
    }

    @Override
    protected void setTransparency(boolean transparency) {

    }
}
