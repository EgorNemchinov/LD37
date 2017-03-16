package com.jacktheogre.lightswitch.replay;

import com.jacktheogre.lightswitch.commands.Command;

/**
 * Created by luna on 08.02.17.
 */

public class Action {

    enum ActionType {GLOBAL_COMMAND, ACTOR_COMMAND, STATE_SUMMARY, NONE}
    private ActionType actionType;

    private Command command;
    private float time;

    public Action(String command) {
        //transforming string into command
    }

    public void execute() {

    }
}
