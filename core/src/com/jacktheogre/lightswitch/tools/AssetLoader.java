package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by luna on 24.10.16.
 */
public class AssetLoader {

    public static final float FONT_SCALE = 1.5f;
    public static final float LETTER_WIDTH = 15f * FONT_SCALE;
    public static final float LETTER_HEIGHT = 15 * FONT_SCALE;
    private AssetManager manager;
    public Texture link, ghost, teleport, scale, scale_fill, replay_button;
    public Texture moon, buttons;
    public TextureRegion undo_button, redo_button, start_button, teleport_button, play_button;
    public TiledMap map;
    private TmxMapLoader mapLoader;
    public BitmapFont font;

    public AssetLoader() {
        this.manager = new AssetManager();
        mapLoader = new TmxMapLoader();

//        manager.load();
    }

    public void load() {
        manager.load("char.png", Texture.class);
        manager.load("ghost.png", Texture.class);
        manager.load("portal.png", Texture.class);
        manager.load("scale.png", Texture.class);
        manager.load("replay_button.png", Texture.class);
        manager.load("scale_fill.png", Texture.class);
        manager.load("moon.png", Texture.class);
        manager.load("allbuttons.png", Texture.class);
        manager.finishLoading();

        map = mapLoader.load("big.tmx");
        ghost = manager.get("ghost.png",Texture.class);
        link = manager.get("char.png", Texture.class);
        teleport = manager.get("portal.png", Texture.class);
        replay_button = manager.get("replay_button.png", Texture.class);
        buttons = manager.get("allbuttons.png",Texture.class);
        scale = manager.get("scale.png", Texture.class);
        scale_fill = manager.get("scale_fill.png", Texture.class);
        moon = manager.get("moon.png",Texture.class);

        start_button = new TextureRegion(buttons, 0, 0, 344, 22);
        redo_button = new TextureRegion(buttons, 0, 26, 148, 22);
        undo_button = new TextureRegion(buttons, 0, 52, 148, 22);
        teleport_button = new TextureRegion(buttons, 0, 78, 96, 22);
        play_button = new TextureRegion(buttons, 0, 104, 472, 65);

        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        font.getData().setScale(FONT_SCALE,FONT_SCALE);
    }

    public void dispose() {
        manager.dispose(); //gets rid of assetmanager itself too
    }
}
