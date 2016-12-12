package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by luna on 24.10.16.
 */
public class AssetLoader {

    private AssetManager manager;

    public Texture link, zelda, teleport, background, scale, scale_fill;
    public TiledMap map;
    private TmxMapLoader mapLoader;

    public AssetLoader() {
        this.manager = new AssetManager();
        mapLoader = new TmxMapLoader();
//        manager.load();
    }

    public void load() {
        manager.load("link.png", Texture.class);
        manager.load("zelda.gif", Texture.class);
        manager.load("portal.png", Texture.class);
        manager.load("scale.png", Texture.class);
        manager.load("scale_fill.png", Texture.class);
        manager.finishLoading();
        map = mapLoader.load("big.tmx");
        zelda = manager.get("zelda.gif",Texture.class);
        link = manager.get("link.png", Texture.class);
        teleport = manager.get("portal.png", Texture.class);
        scale = manager.get("scale.png", Texture.class);
        scale_fill = manager.get("scale_fill.png", Texture.class);
    }

    public void dispose() {
        manager.dispose(); //gets rid of assetmanager itself too
    }
}
