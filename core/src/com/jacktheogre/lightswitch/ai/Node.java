package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by luna on 21.10.16.
 */
public class Node {

    private Array<Connection<Node>> connections = new Array<Connection<Node>>();
    public int type;
    public int index;

    public Node() {
        index = Node.Indexer.getIndex();
    }

    public int getIndex() {
        return index;
    }

    public Array<Connection<Node>> getConnections() {
        return connections;
    }

    public void createConnection(Node toNode, float cost) {
        connections.add(new ConnectionImp(this, toNode, cost));
    }

    public void connnectTo(Node node, float cost) {
        createConnection(node, 0);
        node.createConnection(this, 0);
    }

    public static class Indexer {
        private static int index = 0;

        public static int getIndex() {
//            Gdx.app.log("Indexer", "Index " + index);
            return index++;
        }

        public static void nullify(){
            index = 0;
        }

    }

    public float getWorldX() {
        float x = (index % LevelManager.lvlTileWidth) * LevelManager.tilePixelWidth + LevelManager.tilePixelWidth * 0.5f;
        return x;
    }

    public float getWorldY() {
        float y = (index / LevelManager.lvlTileWidth) * LevelManager.tilePixelHeight + LevelManager.tilePixelHeight * 0.5f;
        return y;
    }

    public static class Type {
        public static final int REGULAR = 1;
    }

    public String toString() {
        return "Node. Y: " + (index / LevelManager.lvlTileWidth) + " X:" + (index % LevelManager.lvlTileWidth);
    }

    public Vector2 getPosition() {
        return new Vector2(index % LevelManager.lvlTileWidth, index / LevelManager.lvlTileWidth);
    }
}
