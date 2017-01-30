package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.ai.Agent;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;

/**
 * Created by luna on 10.12.16.
 */
public class EnemyPlayer {

    private GameActor gameActor;
    private GeneratingScreen screen;
    private Agent agent;

    public EnemyPlayer(GeneratingScreen screen) {
        this.screen = screen;
        agent = new Agent(screen.getWorld());
    }

    public void update(float dt) {
        gameActor.setTarget(screen.getPlayer().getGameActor().b2body.getPosition());
        agent.makePath(gameActor);
        gameActor.update(dt);
    }

    public GameActor getGameActor() {
        return gameActor;
    }

    public void setGameActor(GameActor gameActor) {
        this.gameActor = gameActor;
        this.gameActor.agent = agent;
    }

    public void setTarget(Vector2 target) {
        gameActor.setTarget(target);
    }

    public void dispose(){
        gameActor.dispose();
    }

    public void render(SpriteBatch batch, float dt) {
        batch.draw(gameActor.getFrame(dt), gameActor.getX(), gameActor.getY());
    }
}
