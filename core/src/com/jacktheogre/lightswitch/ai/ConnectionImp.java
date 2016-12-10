package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by luna on 21.10.16.
 */
public class ConnectionImp implements Connection<Node> {

    private Node fromNode, toNode;
    private float cost;

    public ConnectionImp(Node fromNode, Node toNode, float cost) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.cost = cost;
    }

    public void render(ShapeRenderer shapeRenderer) {
//        Gdx.app.log("Connection", "From " + fromNode + " to " + toNode);
        shapeRenderer.line(fromNode.getWorldX(), fromNode.getWorldY(), toNode.getWorldX(), toNode.getWorldY());
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Node getFromNode() {
        return fromNode;
    }

    @Override
    public Node getToNode() {
        return toNode;
    }
}
