package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

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
    
    public static void loadLevel(TiledMap map) {
        tiledMap = map;

        MapProperties properties = tiledMap.getProperties();
        lvlTileWidth = properties.get("width", Integer.class);
        lvlTileHeight = properties.get("height", Integer.class);
        tilePixelHeight = properties.get("tilewidth", Integer.class);
        tilePixelWidth = properties.get("tileheight", Integer.class);
        Gdx.app.log("levelmanager", "" +tilePixelWidth);
        lvlPixelWidth = tilePixelWidth * lvlTileWidth;
        lvlPixelHeight = tilePixelHeight * lvlTileHeight;

        graph = GraphGenerator.generateGraph(tiledMap);
    }

}
