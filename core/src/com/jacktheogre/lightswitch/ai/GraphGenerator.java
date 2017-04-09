package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.utils.Array;

/**
 * Created by luna on 21.10.16.
 */
public class GraphGenerator {


    public static GraphImp generateGraph(TiledMap map) {
        // TODO: 11.12.16 if need to generate again, nullify indexes
        if(LevelManager.graph != null)
            return LevelManager.graph;
        Array<Node> nodes = new Array<Node>();
        TiledMapTileLayer tiles = (TiledMapTileLayer) map.getLayers().get(1); // FIXME: 21.10.16 layer num
        int mapHeight = LevelManager.lvlTileHeight;
        int mapWidth = LevelManager.lvlTileWidth;

        //left to right, down to up
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {

                Node node = new Node();
                node.type = Node.Type.REGULAR;
                nodes.add(node);
            }
        }

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                Cell target = tiles.getCell(x, y);// TODO: 22.10.16 ADD DIAGONAL ONLY IF TWO NEAR ARE FREE! FIXME!
                Array<Cell> cells = new Array<Cell>();
                Cell cell;
                Node targetNode = nodes.get(mapWidth * y + x);
                for (int x1 = x-1; x1 <= x+1; x1++) {
                    for (int y1 = y-1; y1 <= y+1 ; y1++) {
                        if(x1 == x && y1 == y) continue;
                        cell = tiles.getCell(x1, y1);
                        if(x1>=0 && x1 < mapWidth && y1 >= 0 && y1 < mapHeight) {
                            Node node = nodes.get(mapWidth * y1 + x1);
                            targetNode.createConnection(node, (float)Math.sqrt((x1-x)*(x1-x) + (y1-y)*(y1-y)));
                        }
                        cells.add(cell);

                    }
                }
            }
        }

        GraphImp graph = new GraphImp(nodes);

        //walls diagonal removed
        tiles = (TiledMapTileLayer) map.getLayers().get(1);
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                Cell target = tiles.getCell(x, y);
                Node targetNode = nodes.get(mapWidth * y + x);
                if(target != null) {

                    graph.removeConnections(targetNode);
                    //deleting diagonal connections if object is an obstacle
                    if(y < mapHeight - 1) {
                        if(x > 0) {
                            graph.removeConnections(nodes.get(mapWidth * (y+1) + x), nodes.get(mapWidth * y + x-1)); //x y+1 and x-1 y
                        }
                        if(x < mapWidth - 1) {
                            graph.removeConnections(nodes.get(mapWidth * (y+1) + x), nodes.get(mapWidth * y + x+1)); //x y+1 and x+1 y
                        }
                    }
                    if(y > 0) {
                        if(x > 0) {
                            graph.removeConnections(nodes.get(mapWidth * (y-1) + x), nodes.get(mapWidth * y + x-1)); //x y-1 and x-1 y
                        }
                        if(x < mapWidth - 1) {
                            graph.removeConnections(nodes.get(mapWidth * (y-1) + x), nodes.get(mapWidth * y + x+1)); //x y-1 and x+1 y
                        }
                    }
                }
            }
        }

        //static objects
        tiles = (TiledMapTileLayer) map.getLayers().get(2);
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                Cell target = tiles.getCell(x, y);
                Node targetNode = nodes.get(mapWidth * y + x);
                if(target != null) {

                    graph.removeConnections(targetNode);
                    //deleting diagonal connections if object is an obstacle
                    if(y < mapHeight - 1) {
                        if(x > 0) {
                            graph.removeConnections(nodes.get(mapWidth * (y+1) + x), nodes.get(mapWidth * y + x-1)); //x y+1 and x-1 y
                        }
                        if(x < mapWidth - 1) {
                            graph.removeConnections(nodes.get(mapWidth * (y+1) + x), nodes.get(mapWidth * y + x+1)); //x y+1 and x+1 y
                        }
                    }
                    if(y > 0) {
                        if(x > 0) {
                            graph.removeConnections(nodes.get(mapWidth * (y-1) + x), nodes.get(mapWidth * y + x-1)); //x y-1 and x-1 y
                        }
                        if(x < mapWidth - 1) {
                            graph.removeConnections(nodes.get(mapWidth * (y-1) + x), nodes.get(mapWidth * y + x+1)); //x y-1 and x+1 y
                        }
                    }
                }
            }
        }


        return graph;
    }
}
