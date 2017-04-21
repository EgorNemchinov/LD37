package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.screens.LevelChoosingScreen;
import com.jacktheogre.lightswitch.tools.ScalingMapRenderer;

/**
 * Created by luna on 21.04.17.
 */

public class MapActor extends Actor {

    private ScalingMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Viewport benchmarkViewport;

    float initialScale;
    private float opacity = 1f;

    public MapActor(float scale, TiledMap map, Viewport benchmarkViewport) {
        initialScale = scale;
        this.benchmarkViewport = benchmarkViewport;
        LevelManager.loadLevel(map);
        mapRenderer = new ScalingMapRenderer(map, initialScale);
        this.setSize(LevelManager.lvlPixelWidth*scale, LevelManager.lvlPixelHeight*scale);
        camera = new OrthographicCamera(benchmarkViewport.getWorldWidth(), benchmarkViewport.getWorldHeight());
        //fixme: is it the same as gameport.getWorldWidth() etc.?
        camera.position.set(benchmarkViewport.getCamera().position.x - getX(), benchmarkViewport.getCamera().position.y - getY(), 0);
    }

    public void centerAt(float x, float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    @Override
    public void setScale(float scaleXY) {
        mapRenderer.setUnitScale(initialScale*scaleXY);
        super.setScale(scaleXY);
    }

    @Override
    public void act(float delta) {
        syncCamera((int)getX(), (int)getY());
        mapRenderer.setView(camera);
    }

    public ScalingMapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public void render() {
        if(opacity != 1) {
            if(!mapRenderer.getBatch().isBlendingEnabled())
                mapRenderer.getBatch().enableBlending();
        }
        mapRenderer.getBatch().setColor(mapRenderer.getBatch().getColor().r, mapRenderer.getBatch().getColor().g,
                    mapRenderer.getBatch().getColor().b, opacity);
        mapRenderer.render();
    }

    private void syncCamera(int x, int y) {
        if(camera != benchmarkViewport.getCamera()) {
            camera.position.x = benchmarkViewport.getCamera().position.x - x;
            camera.position.y = benchmarkViewport.getCamera().position.y - y;
        }
        camera.update();
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

}

