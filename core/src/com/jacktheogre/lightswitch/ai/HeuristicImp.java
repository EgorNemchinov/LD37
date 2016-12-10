package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * Created by luna on 21.10.16.
 */


public class HeuristicImp implements Heuristic<Node> {


    @Override
    public float estimate(Node startNode, Node endNode) {
        float distance = endNode.getPosition().sub(startNode.getPosition()).len();
//        float distance = endNode.getPosition().y - startNode.getPosition().y + endNode.getPosition().x - startNode.getPosition().x;
        return distance;
    }
}
