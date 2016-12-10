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

    public Texture link;
    public TiledMap map;
    private TmxMapLoader mapLoader;

    public AssetLoader() {
        this.manager = new AssetManager();
        mapLoader = new TmxMapLoader();
//        manager.load();
    }

    public void load() {
        manager.load("link.png", Texture.class);
        manager.finishLoading();
        map = mapLoader.load("labyrinth.tmx");
        link = manager.get("link.png", Texture.class);
    }

    public void dispose() {
        manager.dispose(); //gets rid of assetmanager itself too
    }
}
