package com.jacktheogre.lightswitch.replay;

import com.badlogic.gdx.utils.Array;

/**
 * Created by luna on 08.02.17.
 */

public class Enterpreter {

    private Array<Action> actions;
    private String[] strings;
    private int pointer;
    private float runTime;

    public Enterpreter() {
    }

    public void load() {
        //load log, transform into Action[]. set runTime
    }

    public void update(float dt) {
        //runTime+=dt, while > action.time => execute
    }
}
