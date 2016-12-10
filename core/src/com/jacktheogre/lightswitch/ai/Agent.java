package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.sprites.Actor;

/**
 * Created by luna on 21.10.16.
 */
public class Agent implements Telegraph {

    private IndexedAStarPathFinder<Node> pathFinder;
    GraphPathImp resultPath = new GraphPathImp();
    RayCastCollisionDetectorImp detectorImp;
    PathSmootherImp pathSmoother;

    private Actor actor;
    private HeuristicImp heuristicImp;

    public Agent(Actor actor, World world) {
        this.actor = actor;

        heuristicImp = new HeuristicImp();
        detectorImp = new RayCastCollisionDetectorImp(world);
        pathSmoother = new PathSmootherImp(detectorImp);
        pathFinder = new IndexedAStarPathFinder<Node>(LevelManager.graph, false);

        int startX = (int) actor.b2body.getPosition().x;
        int startY = (int) actor.b2body.getPosition().y;

        int endX = (int) actor.getTarget().x;
        int endY = (int) actor.getTarget().y;

        Node startNode = LevelManager.graph.getNodeByXY(startX, startY);
        Node endNode = LevelManager.graph.getNodeByXY(endX, endY);

        Gdx.app.log("StartNode", startNode.toString());
        Gdx.app.log("EndNode", endNode.toString());
        pathFinder.searchNodePath(startNode, endNode, heuristicImp, resultPath);
//        pathSmoother.smoothPath(resultPath);
//        Gdx.app.log("New path:", resultPath.toString());
        actor.setPath(resultPath);
    }

    public void makePath(Actor actor) {
        resultPath.clear();

        this.actor = actor;
        int startX = (int) actor.b2body.getPosition().x;
        int startY = (int) actor.b2body.getPosition().y;

        int endX = (int) actor.getTarget().x;
        int endY = (int) actor.getTarget().y;

        Node startNode = LevelManager.graph.getNodeByXY(startX, startY);
        Node endNode = LevelManager.graph.getNodeByXY(endX, endY);

//        Gdx.app.log("StartNode", startNode.toString());
//        Gdx.app.log("EndNode", endNode.toString());

        pathFinder.searchNodePath(startNode, endNode, heuristicImp, resultPath);
        pathSmoother.smoothPath(resultPath);
        actor.setPath(resultPath);
//        Gdx.app.log("Estimation", "From 12,6:" + (heuristicImp.estimate(LevelManager.graph.getNodeByIndex(LevelManager.lvlTileWidth * 6 + 12),
//                LevelManager.graph.getNodeByIndex(LevelManager.lvlTileWidth * 7 + 18)) +
//                        heuristicImp.estimate(LevelManager.graph.getNodeByIndex(LevelManager.lvlTileWidth * 6 + 11),
//                                LevelManager.graph.getNodeByIndex(LevelManager.lvlTileWidth * 6 + 12))));
//        Gdx.app.log("Estimation", "From 12,7:" + (heuristicImp.estimate(LevelManager.graph.getNodeByIndex(LevelManager.lvlTileWidth * 7 + 12),
//                LevelManager.graph.getNodeByIndex(LevelManager.lvlTileWidth * 7 + 18)) + heuristicImp.estimate(LevelManager.graph.getNodeByIndex(LevelManager.lvlTileWidth * 6 + 11),
//                        LevelManager.graph.getNodeByIndex(LevelManager.lvlTileWidth * 7 + 12))));
    }

    public GraphPathImp getResultPath() {
        return resultPath;
    }

    public Actor getActor() {
        return actor;
    }

    public void update(float dt) {
//        stateMachine.update();
    }


    public Ray<Vector2> getRayUp() {
        return detectorImp.getRayUp();
    }

    public Ray<Vector2> getRayDown() {
        return detectorImp.getRayDown();
    }
    public Ray<Vector2> getInputRay() {
        return detectorImp.getInputRay();
    }

    public void sleep() {
        actor.setVelocity(new Vector2(0, 0));
    }


//    public void startSeeking() {
//        steeringBehaviour.
//    }

    public void setSteeringBehaviour() {
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }
}
