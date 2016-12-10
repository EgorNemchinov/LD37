package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.utils.Collision;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by luna on 23.10.16.
 */
public class RayCastCollisionDetectorImp implements RaycastCollisionDetector<Vector2> {

    private final Vector2 dlt = new Vector2(0.5f, 0.5f);

    World world;
    Box2dRaycastCallback callback;
    Box2dRaycastCallback callbackD;
    Box2dRaycastCallback callbackU;
    Ray<Vector2> rayUp;
    Ray<Vector2> rayDown;
    Ray<Vector2> inputRay;


    public RayCastCollisionDetectorImp (World world) {
        this(world, new Box2dRaycastCallback());
    }

    public RayCastCollisionDetectorImp (World world, Box2dRaycastCallback callback) {
        this.world = world;
        this.callback = callback;
        this.callbackU = new Box2dRaycastCallback();
        this.callbackD = new Box2dRaycastCallback();
        rayUp = new Ray<Vector2>(new Vector2(0, 0),new Vector2(0, 0));;
        rayDown = new Ray<Vector2>(new Vector2(0, 0),new Vector2(0, 0));
    }

    @Override
    public boolean collides (Ray<Vector2> ray) {
        return findCollision(null, ray);
    }

    @Override
    public boolean findCollision (Collision<Vector2> outputCollision, Ray<Vector2> inputRay) {
        inputRay.start.add(dlt);
        inputRay.end.add(dlt);
        this.inputRay = inputRay;
        callback.collided = false;
        callbackD.collided = false;
        callbackU.collided = false;
        moveRays(inputRay);
        if (!inputRay.start.epsilonEquals(inputRay.end, MathUtils.FLOAT_ROUNDING_ERROR)) {
            callback.outputCollision = outputCollision;
            transformToWorld(inputRay);
            transformToWorld(rayUp);
            transformToWorld(rayDown);
            if(inputRay.start.x < inputRay.start.y) {
                world.rayCast(callback, inputRay.start, inputRay.end);
                world.rayCast(callbackD, rayDown.start, rayDown.end);
                world.rayCast(callbackU, rayUp.start, rayUp.end);
            }
            else {
                world.rayCast(callback, inputRay.end, inputRay.start);
                world.rayCast(callbackD, rayDown.end, rayDown.start);
                world.rayCast(callbackU, rayUp.end, rayUp.start);
            }
//            Gdx.app.log("RayCastCD", "inputRay " + inputRay.start + " to " + inputRay.end);
        }
//        if(callback.collided|| callbackD.collided || callbackU.collided)
//            Gdx.app.log("RayCastCD", "found collision");
        return callback.collided || callbackD.collided || callbackU.collided;
    }

    private void moveRays(Ray<Vector2> inputRay) {
        Vector2 delta = inputRay.end.cpy().sub(inputRay.start);
        delta = new Vector2(-delta.y, delta.x).nor().scl(0.5f); // FIXME: 24.10.16 set right scale. draw rays to debug
//        delta = new Vector2(0,0);
//        rayUp = inputRay;
        rayUp.start = inputRay.start.cpy().add(delta);
        rayUp.end = inputRay.end.cpy().add(delta);
        rayDown.start = inputRay.start.cpy().sub(delta);
        rayDown.end= inputRay.end.cpy().sub(delta);
    }

    private void transformToWorld(Vector2 x) {
        x.scl(LevelManager.tilePixelWidth, LevelManager.tilePixelHeight);
    }

    private void transformToWorld(Ray<Vector2> ray){
        transformToWorld(ray.start);
        transformToWorld(ray.end);
    }

    public Ray<Vector2> getRayUp() {
        return rayUp;
    }

    public Ray<Vector2> getRayDown() {
        return rayDown;
    }

    public Ray<Vector2> getInputRay() {
        return inputRay;
    }


    public static class Box2dRaycastCallback implements RayCastCallback {
        public Collision<Vector2> outputCollision;
        public boolean collided;

        public Box2dRaycastCallback () {
        }

        @Override
        public float reportRayFixture (Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (outputCollision != null) outputCollision.set(point, normal);
            collided = true;
            return fraction;
        }
    }
}
