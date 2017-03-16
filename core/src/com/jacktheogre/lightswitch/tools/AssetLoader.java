package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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

    public static final float FONT_SCALE = 1.5f;
    public static final float LETTER_WIDTH = 20f * FONT_SCALE;
    public static final float LETTER_HEIGHT = 15 * FONT_SCALE;
    public static final int LEVEL_AMOUNT = 5;
    private static int levelNum = 4;
    private AssetManager manager;
    public Texture characters, teleport, scale_fill, timer, trap;
    public Texture moon, buttons, joystick;
    public TextureRegion touchBg, touchKnob, scale;
    public TextureRegion next_level_button, home_button, replay_button, light_button;
    public TextureRegion undo_button, redo_button, start_button, teleport_button, play_button;
    public TextureRegion boy_button, monster_button, trap_button, singleplayer_button, multiplayer_button;
    public TiledMap[] maps;
    private TmxMapLoader mapLoader;
    public BitmapFont font;
    public Sound runningSound, teleportOpenSound;
    public Sound teleportCloseSound;
    public FileHandle fileHandle;

    public AssetLoader() {
        this.manager = new AssetManager();
        mapLoader = new TmxMapLoader();
        maps = new TiledMap[LEVEL_AMOUNT+1];
    }

    public void load() {
        if(manager == null)
            manager = new AssetManager();

        fileHandle = Gdx.files.local("log.txt");

        manager.load("characters.png", Texture.class);
        manager.load("portals.png", Texture.class);
        manager.load("traps.png", Texture.class);
        manager.load("scale.png", Texture.class);
        manager.load("scale_fill.png", Texture.class);
        manager.load("moon.png", Texture.class);
        manager.load("buttons.png", Texture.class);
        manager.load("timer.png", Texture.class);
        manager.load("running.mp3", Sound.class);
        manager.load("tpOpen.mp3", Sound.class);
        manager.load("tpClose.mp3", Sound.class);
        manager.load("joystick.png", Texture.class);
        manager.finishLoading();

        for (int i = 1; i <= LEVEL_AMOUNT; i++) {
            maps[i] = mapLoader.load("level"+i+".tmx");
        }
        characters = manager.get("characters.png",Texture.class);
        teleport = manager.get("portals.png", Texture.class);
        trap = manager.get("traps.png", Texture.class);
        buttons = manager.get("buttons.png",Texture.class);
        scale_fill = manager.get("scale_fill.png", Texture.class);
        moon = manager.get("moon.png",Texture.class);
        timer = manager.get("timer.png", Texture.class);
        joystick = manager.get("joystick.png", Texture.class);
        runningSound = manager.get("running.mp3", Sound.class);
        teleportOpenSound = manager.get("tpOpen.mp3", Sound.class);
        teleportCloseSound = manager.get("tpClose.mp3", Sound.class);
        
//        light_button = new TextureRegion(manager.get("light_button.png", Texture.class));
        start_button = new TextureRegion(buttons, 1, 0, 188, 27);
        undo_button = new TextureRegion(buttons, 0, 31, 156, 27);
        redo_button = new TextureRegion(buttons, 0, 62, 156, 27);
        teleport_button = new TextureRegion(buttons, 0, 93, 112, 26);
        trap_button = new TextureRegion(buttons, 0, 123, 112, 26);
        play_button = new TextureRegion(buttons, 0, 153, 492, 75);
        next_level_button = new TextureRegion(buttons, 0, 232, 156, 27);
        replay_button = new TextureRegion(buttons, 0, 263, 156, 27);
        home_button = new TextureRegion(buttons, 0, 294, 156, 27);
        light_button = new TextureRegion(buttons, 0, 325, 98, 42);
        boy_button = new TextureRegion(buttons, 0, 371, 112, 73);
        monster_button = new TextureRegion(buttons, 0, 444, 112, 73 );
        scale = new TextureRegion(buttons, 0, 521, 49, 165);
        singleplayer_button = new TextureRegion(buttons, 51, 521, 62, 26);
        multiplayer_button = new TextureRegion(buttons, 51, 551, 62, 26);
        touchBg = new TextureRegion(joystick, 0, 0, 132, 132);
        touchKnob = new TextureRegion(joystick, 130, 0, 126, 126);

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
        return 2*(levelNum % 4);
    }

    public int getAmountOfTraps() {
        return Math.max(0, (levelNum - 3));
    }

    public boolean isMaxLevel() {
        if(levelNum == LEVEL_AMOUNT)
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

    public float getLetterWidth() {
        return (LETTER_WIDTH / FONT_SCALE)*font.getScaleX();
    }

    public float getLetterHeight() {
        return (LETTER_HEIGHT/ FONT_SCALE)*font.getScaleY();
    }

    public void nullifyLevel() {
        levelNum = 0;
    }

    public void dispose() {
        manager.dispose(); //gets rid of assetmanager itself too
    }
}
