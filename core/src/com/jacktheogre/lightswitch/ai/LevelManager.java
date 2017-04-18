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

    private static int[] amountOfShards;
    private static int[] totalAmountOfShardsBefore;
    private static int totalShardsCollected = 0;

    public static boolean isOpenLevel(int levelNumber) {
        if(levelNumber == 1)
            return true;
//        if(getAmountOfCollectedShards(levelNumber - 1) == countAmountOfShards(levelNumber - 1))
//            return true;
        if(totalShardsCollected >= getAmountOfShardsToUnlock(levelNumber))
            return true;
        else
            return false;
    }

    public static int countAmountOfShards(int levelNumber) {
        return Assets.getAssetLoader().maps[levelNumber].getLayers().get(7).getObjects().getCount();
    }

    public static void recountShards() {
        int collected = 0;
        for (int i = 1; i <= LEVEL_AMOUNT; i++) {
            collected += getAmountOfCollectedShards(i);
        }
        totalShardsCollected = collected;
    }

    static class Resourses {

        int teleports;
        int traps;
        int monsterSpeed;
        int shardsToUnlock;

        Resourses(int teleports, int traps, int monsterSpeed, int shardsToUnlock) {
            this.teleports = teleports;
            this.traps = traps;
            this.monsterSpeed = monsterSpeed;
            this.shardsToUnlock = shardsToUnlock;
        }

        @Override
        public String toString() {
            return String.format("%d teleports & %d traps & %d is monster speed", teleports, traps, monsterSpeed);
        }

    }
    private static Map<Integer, Resourses> levelsMap;

    public static final int LEVEL_AMOUNT = 8;
    private static int levelNum = 1;

    static {
        levelsMap = new HashMap<Integer, Resourses>();
        if(!parseLevelsFile()) {
            Gdx.app.exit();
        }
        amountOfShards = new int[LEVEL_AMOUNT+1];
//        totalAmountOfShardsBefore = new int[LEVEL_AMOUNT + 1];
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
            int levelNum, teleportsNum, trapsNum, monsterSpeed, shardsToUnlock;
            try {
                levelNum = Integer.parseInt(columns[0]);
                teleportsNum = Integer.parseInt(columns[1]);
                trapsNum = Integer.parseInt(columns[2]);
                monsterSpeed = Integer.parseInt(columns[3]);
                shardsToUnlock = Integer.parseInt(columns[4]);
                levelsMap.put(levelNum, new Resourses(teleportsNum, trapsNum, monsterSpeed, shardsToUnlock));
            } catch (Exception e) {
                Gdx.app.error("LevelManager", "Error parsing file");
                // TODO: 09.04.17 dialog mb?
                Gdx.app.exit();
                return false;
            }
        }
        return true;
    }

    public static void collectShards(int levelNum, String collectedShards) {
        //// FIXME: 09.04.17 we can just add the difference to totalAmountOfShardsBefore
        Assets.getAssetLoader().getCollectedShards().putString(levelNum+"", collectedShards);
        Assets.getAssetLoader().getCollectedShards().flush();
        LevelManager.recountShards();
    }

    public static void collectShards(String collectedShards) {
        collectShards(levelNum, collectedShards);
    }

    public static String getCollectedShardsString(int levelNum) {
        return Assets.getAssetLoader().getCollectedShards().getString(levelNum+"", "");
    }

    public static String getCollectedShardsString() {
        return Assets.getAssetLoader().getCollectedShards().getString(levelNum+"", "");
    }

    public static int getAmountOfCollectedShards() {
        String shardsStr = getCollectedShardsString();
        int count = 0;
        for (int i = 0; i < shardsStr.length(); i++) {
            if (shardsStr.charAt(i) == '1')
                count++;
        }
        return count;
    }


    public static int getAmountOfCollectedShards(int levelNum) {
        String shardsStr = getCollectedShardsString(levelNum);
        int count = 0;
        for (int i = 0; i < shardsStr.length(); i++) {
            if (shardsStr.charAt(i) == '1')
                count++;
        }
        return count;
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

    public static int getAmountOfTeleports(int levelNum) {
        return getLevelResourses(levelNum).teleports;
    }

    public static int getAmountOfShardsToUnlock(int levelNum) {
        return getLevelResourses(levelNum).shardsToUnlock;
    }

    public static int getAmountOfShardsToUnlock() {
        return getAmountOfShardsToUnlock(levelNum);
    }

        public static int getAmountOfTraps(int levelNum) {
        return getLevelResourses(levelNum).traps;
    }

    public static int getMonsterSpeed() {
        return getLevelResourses(levelNum).monsterSpeed;
    }

    public static int getLevelNum() {
        return levelNum;
    }

    public static int getAmountOfShardsOnLevel() {
        return amountOfShards[levelNum];
    }

    public static int getAmountOfShardsOnLevel(int levelNum) {
        return amountOfShards[levelNum];
    }

    public static void setAmountOfShards(int amountOfShards) {
        LevelManager.amountOfShards[levelNum] = amountOfShards;
    }

    public static void setLevelNum(int levelNum) {
        if(levelNum != LevelManager.levelNum)
            LevelManager.graph = null;
        LevelManager.levelNum = levelNum;
    }

    public static int getTotalShardsCollected() {
        return totalShardsCollected;
    }

    public static boolean isMaxLevel() {
        return levelNum == LEVEL_AMOUNT;
    }

    public static boolean isMaxLevel(int level) {
        return level == LEVEL_AMOUNT;
    }

    public static void nextLevel() {
        if(!isMaxLevel()) {
            LevelManager.graph = null;
            levelNum++;
        }
    }

    public static void setLevel(int levelNumber) {
        if(1 <= levelNumber && levelNumber <= LEVEL_AMOUNT ) {
            LevelManager.graph = null;
            levelNum = levelNumber;
        }
    }

}
