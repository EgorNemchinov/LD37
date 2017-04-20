package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Created by luna on 21.04.17.
 */

public class ScalingMapRenderer extends OrthogonalTiledMapRenderer {
    public ScalingMapRenderer(TiledMap map) {
        super(map);
    }

    public ScalingMapRenderer(TiledMap map, Batch batch) {
        super(map, batch);
    }

    public ScalingMapRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
    }

    public ScalingMapRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
    }

    @Override
    public void renderTileLayer(TiledMapTileLayer layer) {
        super.renderTileLayer(layer);
    }

    public void setUnitScale(float scale) {
        unitScale = scale;
    }
}
