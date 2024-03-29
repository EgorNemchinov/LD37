package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by luna on 21.10.16.
 */
public class GraphImp implements IndexedGraph<Node>{
    // TODO: 21.10.16 implement default indexed
    private Array<Node> nodes = new Array<Node>();

    public GraphImp(Array<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public int getIndex(Node node) {
        return node.getIndex();
    }

    public int getNodeCount() {
        return nodes.size;
    }

    public Node getNodeByXY(int x, int y) {
        int modX = x / LevelManager.tilePixelWidth;
        int modY = (y)/ LevelManager.tilePixelHeight;
        if(modX < 0)
            modX = 0;
        if(modY < 0)
            modY = 0;
        if(modX > LevelManager.lvlTileWidth - 1)
            modX = LevelManager.lvlTileWidth - 1;
        if(modY > LevelManager.lvlTileHeight- 1)
            modY = LevelManager.lvlTileHeight- 1;

        return nodes.get(LevelManager.lvlTileWidth * modY + modX);
    }

    public Node getNodeByIndex(int ind) {
        return nodes.get(ind);
    }

    //returns null if node is closed, else returns this node
    public Node getOpenNode(int x, int y) {
        int index = x + y*LevelManager.lvlTileWidth;
        if(index >= nodes.size)
            return null;
        Node node = nodes.get(index);
        if(node.getConnections().size > 0)
            return node;
        else
            return null;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.WHITE);
        if(!shapeRenderer.isDrawing())
            shapeRenderer.begin();
        for (Node node : nodes) {
            shapeRenderer.circle(node.getWorldX(), node.getWorldY(), 1);
            for (Connection connection :
                    node.getConnections()) {
                ((ConnectionImp) connection).render(shapeRenderer);
            }
        }
        shapeRenderer.end();
    }

    @Override
    public Array<Connection<Node>> getConnections(Node fromNode) {
        for (int i = 0; i < nodes.get(fromNode.getIndex()).getConnections().size; i++) {
            if(nodes.get(fromNode.getIndex()).getConnections().get(i).getToNode() == null)
                Gdx.app.log("ConnectionToNodeNull", "From:" + fromNode);
        }
        return nodes.get(fromNode.getIndex()).getConnections();
    }

    void removeConnections(Node node) {
        for (Connection fromConnection : node.getConnections()) {
            for (Connection toConnection :((Node)fromConnection.getToNode()).getConnections()) {
                if(toConnection.getToNode() == node)
                    ((Node)fromConnection.getToNode()).getConnections().removeValue(toConnection, true);
            }
        }
        node.getConnections().clear();
    }

    void removeConnections(Node node1, Node node2) {
        for (Connection fromConnection : node1.getConnections()) {
            if(fromConnection.getToNode() == node2)
                node1.getConnections().removeValue(fromConnection, true);
        }
        for (Connection fromConnection : node2.getConnections()) {
            if(fromConnection.getToNode() == node1)
                node2.getConnections().removeValue(fromConnection, true);
        }
    }
}
