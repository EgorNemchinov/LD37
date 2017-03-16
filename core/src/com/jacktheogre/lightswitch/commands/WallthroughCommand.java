package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.jacktheogre.lightswitch.ai.GraphImp;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.ai.Node;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Monster;
import com.jacktheogre.lightswitch.sprites.Player;

/**
 * Created by luna on 27.02.17.
 */

public class WallthroughCommand extends ActorCommand {

    public WallthroughCommand(Player monsterPlayer) {
        this.player = monsterPlayer;
    }

    @Override
    public boolean execute() {
        if(executed)
            return false;
        if(!ClassReflection.isInstance(Monster.class, player.getGameActor()))
            return false;
        Vector2 destination = calculatePosition();
        Gdx.app.log("WallthroughCommand", player.getGameActor().b2body.getPosition() + " - " + destination);
        if(!destination.epsilonEquals(player.getGameActor().b2body.getPosition(), LevelManager.tilePixelWidth)) {
            player.getGameActor().b2body.setTransform(destination, 0);
        }
        player.getGameActor().setRemakingPath(true);
        return true;
        /*((Monster)player.getGameActor()).setTransparency(transparent);
        return executed = true;*/
    }

    private Vector2 calculatePosition() {
        Vector2 position = player.getGameActor().b2body.getPosition(), destination;
        GraphImp graph = LevelManager.graph;
        Node currentNode = graph.getNodeByXY((int) position.x, (int) position.y);
        GameActor.Direction direction = player.getGameActor().getDirection();
        int x = (int) currentNode.getPosition().x, y = (int) currentNode.getPosition().y;
        destination = new Vector2(x, y);
        boolean metWall = false;
//        Gdx.app.log("WallthroughCommand", "Initial node: " + destination);
        switch(direction) {
            case RIGHT:
                x++;
                while(x < LevelManager.lvlTileWidth - 1) {
                    if(graph.getOpenNode(x, y) != null) {
                        destination = new Vector2(x, y);
                        break;
                    }
                    metWall = true;
                    x++;
                }
                destination = destination.scl(LevelManager.tilePixelWidth, LevelManager.tilePixelHeight);
                destination.y = player.getGameActor().getY();
                break;
            case LEFT:
                x--;
                while(x > 0) {
                    if(graph.getOpenNode(x, y) != null) {
                        destination = new Vector2(x, y);
                        break;
                    }
                    metWall = true;
                    x--;
                }
                destination = destination.scl(LevelManager.tilePixelWidth, LevelManager.tilePixelHeight);
                destination.y = player.getGameActor().getY();
                break;
            case UP:
                y++;
                while(y < LevelManager.lvlTileWidth) {
                    if(graph.getOpenNode(x, y) != null) {
                        destination = new Vector2(x, y);
                        break;
                    }
                    metWall = true;
                    y++;
                }
                destination = destination.scl(LevelManager.tilePixelWidth, LevelManager.tilePixelHeight);
                destination.x = player.getGameActor().getX();
                break;
            case DOWN:
                y--;
                while(y > 0) {
                    if(graph.getOpenNode(x, y) != null) {
                        destination = new Vector2(x, y);
                        break;
                    }
                    metWall = true;
                    y--;
                }
                destination = destination.scl(LevelManager.tilePixelWidth, LevelManager.tilePixelHeight);
                destination.x = player.getGameActor().getX();
                break;
        }
//        Gdx.app.log("WallthroughCommand", "After: " + destination);
        destination.add(LevelManager.tilePixelWidth / 2, LevelManager.tilePixelHeight / 2);
        //means that if there was no wall then no wallthrough
        return metWall ? destination : player.getGameActor().b2body.getPosition();
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }
}
