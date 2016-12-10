package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.PathSmootherRequest;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.ai.utils.Collision;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by luna on 23.10.16.
 */
public class PathSmootherImp extends PathSmoother<Node, Vector2> {


    /**
     * Creates a {@code PathSmoother} using the given {@link RaycastCollisionDetector}
     *
     * @param raycastCollisionDetector the raycast collision detector
     */
    public PathSmootherImp(RaycastCollisionDetector<Vector2> raycastCollisionDetector) {
        super(raycastCollisionDetector);
    }
}
