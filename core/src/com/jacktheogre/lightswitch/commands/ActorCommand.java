package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.jacktheogre.lightswitch.sprites.EnemyPlayer;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Player;

/**
 * Created by luna on 10.12.16.
 */
public abstract class ActorCommand extends Command{

    protected Player player;

    public void setGameActor(GameActor gameActor) {
        this.player.setGameActor(gameActor);
    }
    public GameActor getGameActor() {
        return player.getGameActor();
    }
    protected int isEnemyCommandToInt() {
        return ClassReflection.isInstance(EnemyPlayer.class, player)?1:0;
    }
}
