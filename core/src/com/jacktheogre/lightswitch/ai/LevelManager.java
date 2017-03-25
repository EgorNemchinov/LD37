package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.jacktheogre.lightswitch.tools.Assets;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by luna on 21.10.16.
 */
public class LevelManager {
    public static int lvlTileWidth;
    public static int lvlTileHeight;
    public static int lvlPixelWidth;
    public static int lvlPixelHeight;
    public static TiledMap tiledMap;
    public static GraphImp graph;
    public static int tilePixelWidth;
    public static int tilePixelHeight;

    static class Resourses {

        public Integer teleports;
        public Integer traps;
        public Resourses(Integer teleports, Integer traps) {
            this.teleports = teleports;
            this.traps = traps;
        }

        @Override
        public String toString() {
            return String.format("%d teleports & %d traps", teleports, traps);
        }

    }
    private static Map<Integer, Resourses> levelsMap;

    public static final int LEVEL_AMOUNT = 6;
    private static int levelNum = 1;

    static {
        levelsMap = new HashMap<Integer, Resourses>();
        if(!parseLevelsFile()) {
            Gdx.app.exit();
        }
    }

    public static void loadLevel(TiledMap map) {
        tiledMap = map;

        MapProperties properties = tiledMap.getProperties();
        lvlTileWidth = properties.get("width", Integer.class);
        lvlTileHeight = properties.get("height", Integer.class);
        tilePixelHeight = properties.get("tilewidth", Integer.class);
        tilePixelWidth = properties.get("tileheight", Integer.class);
        lvlPixelWidth = tilePixelWidth * lvlTileWidth;
        lvlPixelHeight = tilePixelHeight * lvlTileHeight;

        graph = GraphGenerator.generateGraph(tiledMap);
    }

    private static boolean parseLevelsFile() {
        FileHandle levels = Assets.getAssetLoader().levelHandle;
        String[] lines = levels.readString().split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            String[] columns = lines[i].split(" ");
            int levelNum, teleportsNum, trapsNum;
            try {
                levelNum = Integer.parseInt(columns[0]);
                teleportsNum = Integer.parseInt(columns[1]);
                trapsNum = Integer.parseInt(columns[2]);
                levelsMap.put(levelNum, new Resourses(teleportsNum, trapsNum));
            } catch (Exception e) {
                Gdx.app.error("LevelManager", "Error parsing file");
                return false;
            }
        }
        return true;
    }

    public static Resourses getLevelResourses(int levelNum) {
        return levelsMap.get(levelNum);
    }

    public static int getAmountOfTeleports() {
        return getLevelResourses(levelNum).teleports;
    }

    public static int getAmountOfTraps() {
        return getLevelResourses(levelNum).traps;
    }

    public static int getLevelNum() {
        return levelNum;
    }
    public static boolean isMaxLevel() {
        if(levelNum == LEVEL_AMOUNT)
            return true;
        else
            return false;
    }

    public static void nextLevel() {
        if(!isMaxLevel()) {
            LevelManager.graph = null;
            levelNum++;
        }
    }

}
