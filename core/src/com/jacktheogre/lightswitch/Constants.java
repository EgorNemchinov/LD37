package com.jacktheogre.lightswitch;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.jacktheogre.lightswitch.ai.Node;

/**
 * Created by luna on 19.10.16.
 */
public class Constants {
    public static final short WALLS_BIT = 1;
    public static final short OBJECT_BIT= 2;
    public static final short PICKABLE_BIT = 4;
    public static final short BOY_BIT = 8;
    public static final short MONSTER_BIT = 16;
    public static final short TRANSPARENT_BIT = 32;
    public static final short LIGHT_BIT = 64;
    public static final short TELEPORT_BIT = 128;
    public static final short TRAP_BIT = 256;

    public static final short TELEPORT_GROUP = -1;
    public static final short TRAP_GROUP = -1;
    public static final short PICKABLE_GROUP = -1;
    public static final short BOY_GROUP = 1;
    public static final short MONSTER_GROUP = 2;

    public static final float LIGHT_DISTANCE = 90f;
    public static final int LIGHT_RAYS = 50;
    public static final float TELEPORT_INTERVAL = 3f;
    public static final float TRAP_INTERVAL = 5f;
    public static final float MONSTER_IN_TRAP = 1.5f;
    public static final float TRAP_TRIGGER_TIME = 0.1f; // double it when you are playing for monster?

    public static final float WASTE_ENERGY_PER_SWITCH = 15f;
    public static final float WASTE_ENERGY_PER_SEC = 30f;
    public static final float ADD_ENERGY_PER_SEC = 5f;

    public static float PLAYTIME = 20f;

    public static final float SYNCRONIZING_FREQUENCY_TIME = 0.5f;

    public static boolean foundDevice = false;


}
