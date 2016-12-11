package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jacktheogre.lightswitch.ai.Agent;
import com.jacktheogre.lightswitch.screens.PlayScreen;

/**
 * Created by luna on 10.12.16.
 */
public class EnemyPlayer {

    private Enemy enemy;
    private PlayScreen screen;
    private Agent agent;

    public EnemyPlayer(PlayScreen screen) {
        this.screen = screen;
        agent = new Agent(screen.getWorld());
    }

    public void update(float dt) {
        enemy.setTarget(screen.getPlayer().getActor().b2body.getPosition());
        agent.makePath(enemy);
        enemy.update(dt);
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public void setTarget(Vector2 target) {
        enemy.setTarget(target);
    }

    public void dispose(){
        enemy.dispose();
    }

    public void render(SpriteBatch batch, float dt) {
        batch.draw(enemy.getFrame(dt), enemy.getX(), enemy.getY());
    }
}
