package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.Iterator;

/**
 * Created by luna on 21.10.16.
 */
public class GraphPathImp implements GraphPath<Node>, SmoothableGraphPath<Node, Vector2> {

    private Array<Node> nodes = new Array<Node>();

    @Override
    public int getCount() {
        return nodes.size;
    }

    @Override
    public Node get(int index) {
        return nodes.get(index);
    }

    @Override
    public void add(Node node) {
        nodes.add(node);
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public void reverse() {
        nodes.reverse();
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        if(!shapeRenderer.isDrawing())
            shapeRenderer.begin();
        for (int i = 0; i < nodes.size - 1; i++) {
            shapeRenderer.line(nodes.get(i).getWorldX(), nodes.get(i).getWorldY(), nodes.get(i + 1).getWorldX(), nodes.get(i + 1).getWorldY());
        }
        shapeRenderer.end();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < nodes.size; i++) {
            sb.append(i).append(" - ").append(nodes.get(i).getPosition()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public Vector2 getNodePosition(int index) {
        return nodes.get(index).getPosition();
    }

    @Override
    public void swapNodes(int index1, int index2) {
        nodes.swap(index1, index2);
    }

    @Override
    public void truncatePath(int newLength) {
        nodes.setSize(newLength);
    }

}
