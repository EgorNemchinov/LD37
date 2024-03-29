package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.jacktheogre.lightswitch.ai.LevelManager;

/**
 * Created by luna on 24.10.16.
 */
public class AssetLoader {

    public static final float FONT_SCALE = 1f;
    private AssetManager manager;
    public Texture characters, teleport, scale_fill, timer, trap;
    public Texture moon, buttons, joystick, shards_sheet;
    public TextureRegion touchBg, touchKnob, scale;
    public TextureRegion next_level_button, home_button, replay_button, light_button;
    public TextureRegion undo_button, redo_button, start_button, teleport_button, clear_button, play_button;
    public TextureRegion boy_button, monster_button, trap_button, singleplayer_button, multiplayer_button;
    public TextureRegion ok_button;
    public TextureRegion[] twoShards, threeShards;
    public TiledMap[] maps;
    private TmxMapLoader mapLoader;
    public BitmapFont font;
    public Sound runningSound, teleportOpenSound;
    public Sound teleportCloseSound;
    public Sound[] shardsSounds;
    public FileHandle logHandle, levelHandle;
    public ShaderProgram shaderProgram;

    private Preferences collectedShards;

    public AssetLoader() {
        this.manager = new AssetManager();
        mapLoader = new TmxMapLoader();
        maps = new TiledMap[LevelManager.LEVEL_AMOUNT+1];
        twoShards = new TextureRegion[3];
        threeShards = new TextureRegion[3];
        shardsSounds = new Sound[3];
        collectedShards = Gdx.app.getPreferences("ShardsCollected");
    }

    public void load() {
        if(manager == null)
            manager = new AssetManager();

        logHandle = Gdx.files.local("data/log.txt");
        levelHandle = Gdx.files.internal("data/levels/levels.txt");

        manager.load("data/textures/characters.png", Texture.class);
        manager.load("data/textures/portals.png", Texture.class);
        manager.load("data/textures/traps.png", Texture.class);
        manager.load("data/textures/scale.png", Texture.class);
        manager.load("data/textures/scale_fill.png", Texture.class);
        manager.load("data/textures/moon.png", Texture.class);
        manager.load("data/textures/buttons.png", Texture.class);
        manager.load("data/textures/timer.png", Texture.class);
        manager.load("data/sounds/running.mp3", Sound.class);
        manager.load("data/sounds/tpOpen.mp3", Sound.class);
        manager.load("data/sounds/tpClose.mp3", Sound.class);
        manager.load("data/textures/joystick.png", Texture.class);
        manager.load("data/textures/shards.png", Texture.class);
        manager.load("data/sounds/shards1.mp3", Sound.class);
        manager.load("data/sounds/shards2.mp3", Sound.class);
        manager.load("data/sounds/shards3.mp3", Sound.class);
        manager.finishLoading();

        for (int i = 1; i <= LevelManager.LEVEL_AMOUNT; i++) {
            maps[i] = mapLoader.load("data/levels/level"+i+".tmx");
        }
        characters = manager.get("data/textures/characters.png",Texture.class);
        teleport = manager.get("data/textures/portals.png", Texture.class);
        trap = manager.get("data/textures/traps.png", Texture.class);
        buttons = manager.get("data/textures/buttons.png",Texture.class);
        scale_fill = manager.get("data/textures/scale_fill.png", Texture.class);
        moon = manager.get("data/textures/moon.png",Texture.class);
        timer = manager.get("data/textures/timer.png", Texture.class);
        joystick = manager.get("data/textures/joystick.png", Texture.class);
        shards_sheet = manager.get("data/textures/shards.png", Texture.class);
        runningSound = manager.get("data/sounds/running.mp3", Sound.class);
        teleportOpenSound = manager.get("data/sounds/tpOpen.mp3", Sound.class);
        teleportCloseSound = manager.get("data/sounds/tpClose.mp3", Sound.class);
        for (int i = 1; i <= 3; i++) {
            shardsSounds[i-1] = manager.get("data/sounds/shards"+i+".mp3", Sound.class);
        }
        
//        light_button = new TextureRegion(manager.get("light_button.png", Texture.class));
        start_button = new TextureRegion(buttons, 1, 0, 212, 27);
        undo_button = new TextureRegion(buttons, 0, 31, 128, 27);
        redo_button = new TextureRegion(buttons, 0, 62, 128, 27);
        teleport_button = new TextureRegion(buttons, 0, 93, 112, 26);
        trap_button = new TextureRegion(buttons, 0, 123, 112, 26);
        play_button = new TextureRegion(buttons, 0, 153, 492, 75);
        next_level_button = new TextureRegion(buttons, 0, 232, 156, 27);
        replay_button = new TextureRegion(buttons, 0, 263, 156, 27);
        home_button = new TextureRegion(buttons, 0, 294, 156, 27);
        light_button = new TextureRegion(buttons, 0, 325, 98, 42);
        boy_button = new TextureRegion(buttons, 0, 371, 112, 73);
        monster_button = new TextureRegion(buttons, 0, 445, 112, 73 );
        scale = new TextureRegion(buttons, 0, 521, 49, 165);
        singleplayer_button = new TextureRegion(buttons, 51, 521, 62, 26);
        multiplayer_button = new TextureRegion(buttons, 51, 551, 62, 26);
        touchBg = new TextureRegion(joystick, 0, 0, 132, 132);
        touchKnob = new TextureRegion(joystick, 130, 0, 126, 126);
        clear_button = new TextureRegion(buttons, 0, 690, 128, 27);
        ok_button = new TextureRegion(buttons, 0, 721, 128, 27);

        for (int i = 0; i < 2; i++) {
            twoShards[i] = new TextureRegion(shards_sheet, 1 + 15*i, 52, 14, 14);
        }
        for (int i = 0; i < 3; i++) {
            threeShards[i] = new TextureRegion(shards_sheet, 1 + 15*i, 19, 11, 11);
        }

        String vertexShader = Gdx.files.internal("data/vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("data/shader.glsl").readString();
        shaderProgram = new ShaderProgram(vertexShader,fragmentShader);

        font = new BitmapFont(Gdx.files.internal("data/manaspace.fnt"));
        font.getData().setScale(FONT_SCALE,FONT_SCALE);
    }

    public TiledMap getMap() {
        return maps[LevelManager.getLevelNum()];
    }

    public Preferences getCollectedShards() {
        return collectedShards;
    }

    public void dispose() {
        manager.dispose(); //gets rid of assetmanager itself too
    }
}
