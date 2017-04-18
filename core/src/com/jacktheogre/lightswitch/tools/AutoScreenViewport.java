package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by luna on 18.04.17.
 */

public class AutoScreenViewport extends ScreenViewport{
    private final int OFFSET_X = 50;
    private float worldWidth, worldHeight;

    public AutoScreenViewport(float worldWidth, float worldHeight) {
        this(worldWidth, worldHeight, new OrthographicCamera());
    }

    public AutoScreenViewport(float worldWidth, float worldHeight, Camera camera) {
        super(camera);
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    // FIXME: 18.04.17 make centerCamera better
    @Override
    public void update(int screenWidth, int screenHeight, boolean centerCamera) {
        float xRatio = worldWidth / screenWidth;
        float yRatio = worldHeight / screenHeight;
        setUnitsPerPixel(Math.max(xRatio, yRatio));
        super.update(screenWidth, screenHeight, centerCamera);
    }

}
