package com.jacktheogre.lightswitch.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/**
 * Created by luna on 21.10.16.
 */
public enum AgentState implements State<Agent> {

    STANDING() {
        @Override
        public void update(Agent agent) {
            agent.sleep();
        }
    },

    PATROLING() {

    },

    FOLLOWING() {

    }
    ;

    @Override
    public void enter(Agent entity) {

    }

    @Override
    public void update(Agent entity) {

    }

    @Override
    public void exit(Agent entity) {

    }

    @Override
    public boolean onMessage(Agent entity, Telegram telegram) {
        return false;
    }
}
