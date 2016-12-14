package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.jacktheogre.lightswitch.ai.LevelManager;

/**
 * Created by luna on 24.10.16.
 */
public class AssetLoader {

    public static final float FONT_SCALE = 1f;
    public static final float LETTER_WIDTH = 15f * FONT_SCALE;
    public static final float LETTER_HEIGHT = 15 * FONT_SCALE;
    public static final int LEVEL_AMOUNT = 2;
    private AssetManager manager;
    public Texture link, ghost, teleport, scale, scale_fill, replay_button, timer;
    public Texture next_level_button;
    public Texture moon, buttons;
    public TextureRegion undo_button, redo_button, start_button, teleport_button, play_button;
    public TiledMap[] maps;
    private TmxMapLoader mapLoader;
    public BitmapFont font;
    public Sound runningSound, teleportOpenSound;
    public Sound teleportCloseSound;
    private static int levelNum = 1;

    public AssetLoader() {
        this.manager = new AssetManager();
        mapLoader = new TmxMapLoader();
        maps = new TiledMap[LEVEL_AMOUNT];
//        manager.load();
    }

    public void load() {
        manager.load("char.png", Texture.class);
        manager.load("ghost.png", Texture.class);
        manager.load("portals.png", Texture.class);
        manager.load("scale.png", Texture.class);
        manager.load("replay_button.png", Texture.class);
        manager.load("scale_fill.png", Texture.class);
        manager.load("moon.png", Texture.class);
        manager.load("allbuttons.png", Texture.class);
        manager.load("timer.png", Texture.class);
        manager.load("running.mp3", Sound.class);
        manager.load("tpOpen.mp3", Sound.class);
        manager.load("tpClose.mp3", Sound.class);
        manager.load("next_level_button.png", Texture.class);
        manager.finishLoading();

        for (int i = 0; i < LEVEL_AMOUNT; i++) {
            maps[i] = mapLoader.load(String.format("level%d.tmx", i));
        }
        ghost = manager.get("ghost.png",Texture.class);
        link = manager.get("char.png", Texture.class);
        teleport = manager.get("portals.png", Texture.class);
        replay_button = manager.get("replay_button.png", Texture.class);
        buttons = manager.get("allbuttons.png",Texture.class);
        scale = manager.get("scale.png", Texture.class);
        scale_fill = manager.get("scale_fill.png", Texture.class);
        moon = manager.get("moon.png",Texture.class);
        timer = manager.get("timer.png", Texture.class);
        runningSound = manager.get("running.mp3", Sound.class);
        teleportOpenSound = manager.get("tpOpen.mp3", Sound.class);
        teleportCloseSound = manager.get("tpClose.mp3", Sound.class);
        next_level_button = manager.get("next_level_button.png", Texture.class);

        start_button = new TextureRegion(buttons, 0, 0, 344, 22);
        redo_button = new TextureRegion(buttons, 0, 26, 148, 22);
        undo_button = new TextureRegion(buttons, 0, 52, 148, 22);
        teleport_button = new TextureRegion(buttons, 0, 78, 96, 22);
        play_button = new TextureRegion(buttons, 0, 104, 472, 65);

        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        font.getData().setScale(FONT_SCALE,FONT_SCALE);
    }

    public TiledMap getMap() {
        return maps[levelNum];
    }

    public static int getLevelNum() {
        return levelNum;
    }

    // TODO: 14.12.16 to each level
    public int getAmountOfTeleports() {
        return 2*(levelNum);
    }

    public boolean isMaxLevel() {
        if(levelNum == LEVEL_AMOUNT - 1)
            return true;
        else
            return false;
    }
    public void nextLevel() {
        if(!isMaxLevel()) {
            LevelManager.graph = null;
            levelNum++;
        }
    }

    public void nullifyLevel() {
        levelNum = 0;
    }

    public void dispose() {
        manager.dispose(); //gets rid of assetmanager itself too
    }
}
