package com.jacktheogre.lightswitch;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.jacktheogre.lightswitch.ai.Node;

/**
 * Created by luna on 19.10.16.
 */
public class Constants {
    public static final short WALLS_BIT = 1;
    public static final short OBJECT_BIT= 2;
    public static final short ACTOR_BIT = 4;
//    public static final short ACTOR_BIT = 4;
    public static final short TRANSPARENT_BIT = 16;
    public static final short LIGHT_BIT = 32;
    public static final short INTERACTIVE_BIT = 64;

    public static final float LIGHT_DISTANCE = 90f;
    public static final int LIGHT_RAYS = 50;

    public static float TELEPORT_INTERVAL = 5f;
}
