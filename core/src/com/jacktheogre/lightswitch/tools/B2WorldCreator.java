package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.objects.InteractiveObject;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Actor;
import com.jacktheogre.lightswitch.sprites.Enemy;
import com.jacktheogre.lightswitch.sprites.Human;

/**
 * Created by luna on 18.10.16.
 */
public class B2WorldCreator {

    public Array<InteractiveObject> objects;
    private World world;
    private TiledMap map;

    public B2WorldCreator(PlayScreen screen) {
        world = screen.getWorld();
        map = Assets.getAssetLoader().map;
        objects = new Array<InteractiveObject>();

        int actorCount = 0;
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        //walls
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);

            body = world.createBody(bodyDef);

            shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Constants.WALLS_BIT;
            fixtureDef.filter.maskBits = Constants.WALLS_BIT |
                    Constants.OBJECT_BIT |
                    Constants.ACTOR_BIT ;
//            fixtureDef.filter.maskBits |= Constants.LIGHT_BIT;
            body.createFixture(fixtureDef);
        }

        //static objects
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();

            objects.add(new Teleport(screen, (int) bounds.getX(), (int) bounds.getY()));

            /*bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);

            body = world.createBody(bodyDef);

            shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Constants.OBJECT_BIT;
            fixtureDef.filter.maskBits = Constants.WALLS_BIT |
                    Constants.OBJECT_BIT |
                    Constants.ACTOR_BIT;
//            fixtureDef.filter.maskBits |= Constants.LIGHT_BIT;
            body.createFixture(fixtureDef);*/
        }
        makeTeleportConnections();


        //actors
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();
            screen.getPlayer().setActor(new Human(world, bounds.getX(), bounds.getY()));
        }

        //enemies
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();
            screen.getEnemyPlayer().setEnemy(new Enemy(world, bounds.getX(), bounds.getY()));
        }

        //lights
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();

            screen.getLighting().addLight(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);
        }

        screen.setObjects(objects);
    }

    private void makeTeleportConnections() {

        ((Teleport)objects.get(0)).addTeleport((Teleport)objects.get(1));
        ((Teleport)objects.get(1)).addTeleport((Teleport)objects.get(0));
        /*for (int i = 0; i < objects.size; i++) {
            if(Teleport.class.isInstance(objects.get(i))) {
                Teleport tp = (Teleport) objects.get(i);
                for (int j = 0; j < objects.size && j != i; j++) {
                    if(Teleport.class.isInstance(objects.get(j))) {
                        Teleport tpIns = (Teleport) objects.get(j);
                        tp.addTeleport(tpIns);
                    }
                }
            }
        }*/
    }
}
