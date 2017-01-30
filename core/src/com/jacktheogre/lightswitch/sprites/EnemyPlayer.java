package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.ai.Agent;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;

/**
 * Created by luna on 10.12.16.
 */
public class EnemyPlayer {

    private Monster monster;
    private GeneratingScreen screen;
    private Agent agent;

    public EnemyPlayer(GeneratingScreen screen) {
        this.screen = screen;
        agent = new Agent(screen.getWorld());
    }

    public void update(float dt) {
        monster.setTarget(screen.getPlayer().getGameActor().b2body.getPosition());
        agent.makePath(monster);
        monster.update(dt);
    }

    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
        this.monster.agent = agent;
    }

    public void setTarget(Vector2 target) {
        monster.setTarget(target);
    }

    public void dispose(){
        monster.dispose();
    }

    public void render(SpriteBatch batch, float dt) {
        batch.draw(monster.getFrame(dt), monster.getX(), monster.getY());
    }
}
