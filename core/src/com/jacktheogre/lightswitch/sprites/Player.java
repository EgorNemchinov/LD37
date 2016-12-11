package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.ai.Agent;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;

/**
 * Created by luna on 18.10.16.
 */
public class Player {
    private Actor actor;
    private Agent agent;

    public Player(GeneratingScreen screen) {
        agent = new Agent(screen.getWorld());
    }

    public void update(float dt) {
        actor.update(dt);
    }

    public void setActor(Actor actor) {
        this.actor = actor;
        this.actor.agent = agent;
    }

    public Actor getActor() {
        return actor;
    }

    public void setTarget(Vector2 target) {
        actor.setTarget(target);
    }

    public void dispose(){
        actor.dispose();
    }

    public void render(SpriteBatch batch, float dt) {
        batch.draw(actor.getFrame(dt), actor.getX(), actor.getY());
    }
}
