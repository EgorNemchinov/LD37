package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.Agent;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;

/**
 * Created by luna on 10.12.16.
 */
public class EnemyPlayer extends Player{

    private GeneratingScreen screen;

    public EnemyPlayer(GeneratingScreen screen) {
        super(screen);
        this.screen = screen;
    }

    public void update(float dt) {
        //mb delegate to commands?
        //&& not multiplayer
        gameActor.setKeyboardControl(true);
        if(LightSwitch.getState() == LightSwitch.State.SINGLEPLAYER) {
            if(LightSwitch.isPlayingHuman()) {
                gameActor.setTarget(screen.getPlayer().getGameActor().b2body.getPosition());
                agent.makePath(gameActor);
                gameActor.setKeyboardControl(false);
            }
        }
        gameActor.update(dt);
    }

    public GameActor getGameActor() {
        return gameActor;
    }

    public void setGameActor(GameActor gameActor) {
        this.gameActor = gameActor;
        this.gameActor.agent = agent;
    }

    public void setPosition(Vector2 position) {
        gameActor.setPosition(position.x, position.y);
    }

    public void setPosition(float x, float y) {
        gameActor.setPosition(x, y);
    }

    public void dispose(){
        gameActor.dispose();
    }

}
