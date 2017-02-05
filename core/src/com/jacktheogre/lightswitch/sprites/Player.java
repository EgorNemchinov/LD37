package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.ai.Agent;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;

/**
 * Created by luna on 18.10.16.
 */
public class Player {
    private GameActor gameActor;
    private Agent agent;

    public Player(GeneratingScreen screen) {
        agent = new Agent(screen.getWorld());
    }

    public void update(float dt) {
        gameActor.update(dt);
    }

    public void setGameActor(GameActor gameActor) {
        this.gameActor = gameActor;
        this.gameActor.agent = agent;
    }

    public GameActor getGameActor() {
        return gameActor;
    }

    public void dispose(){
        gameActor.dispose();
    }

    public void render(SpriteBatch batch, float dt) {
        batch.draw(gameActor.getFrame(dt), gameActor.getX(), gameActor.getY());
    }
    /*
    public void setPosition(Vector2 position) {
        gameActor.setPosition(position.x, position.y);
    }

    public void setPosition(float x, float y) {
        gameActor.setPosition(x, y);
    }*/
}
