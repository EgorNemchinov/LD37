package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.jacktheogre.lightswitch.sprites.GameActor;

/**
 * Created by luna on 21.10.16.
 */
public class Agent implements Telegraph {

    private IndexedAStarPathFinder<Node> pathFinder;
    GraphPathImp resultPath = new GraphPathImp();
    RayCastCollisionDetectorImp detectorImp;
    PathSmootherImp pathSmoother;

    private GameActor gameActor;
    private HeuristicImp heuristicImp;

    public Agent(World world) {
        heuristicImp = new HeuristicImp();
        detectorImp = new RayCastCollisionDetectorImp(world);
        pathSmoother = new PathSmootherImp(detectorImp);
        pathFinder = new IndexedAStarPathFinder<Node>(LevelManager.graph, false);
//        pathSmoother.smoothPath(resultPath);
//        Gdx.app.log("New path:", resultPath.toString());
    }

    public void makePath(GameActor gameActor) {
        resultPath.clear();

        this.gameActor = gameActor;
        int startX = (int) gameActor.b2body.getPosition().x;
        int startY = (int) gameActor.b2body.getPosition().y;

        int endX = (int) gameActor.getTarget().x;
        int endY = (int) gameActor.getTarget().y;

        Node startNode = LevelManager.graph.getNodeByXY(startX, startY);
        Node endNode = LevelManager.graph.getNodeByXY(endX, endY);

//        Gdx.app.log("StartNode", startNode.toString());
//        Gdx.app.log("EndNode", endNode.toString());

        pathFinder.searchNodePath(startNode, endNode, heuristicImp, resultPath);
        pathSmoother.smoothPath(resultPath);
        gameActor.setPath(resultPath);
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

    public GameActor getGameActor() {
        return gameActor;
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
        gameActor.setVelocity(new Vector2(0, 0));
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
