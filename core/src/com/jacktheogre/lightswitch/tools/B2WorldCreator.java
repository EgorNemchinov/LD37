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
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.sprites.Monster;
import com.jacktheogre.lightswitch.sprites.Human;

/**
 * Created by luna on 18.10.16.
 */
public class B2WorldCreator {

    private World world;
    private TiledMap map;

    public B2WorldCreator(GeneratingScreen screen) {
        world = screen.getWorld();
        map = Assets.getAssetLoader().getMap();

        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        //walls
        for (MapObject object : map.getLayers().get(3).getObjects()) {
            if (!ClassReflection.isInstance(RectangleMapObject.class, object))
                continue;
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);

            body = world.createBody(bodyDef);

            shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Constants.WALLS_BIT;
            fixtureDef.filter.maskBits = Constants.WALLS_BIT |
                    Constants.OBJECT_BIT |
                    Constants.BOY_BIT |
                    Constants.MONSTER_BIT;
//            fixtureDef.filter.maskBits |= Constants.LIGHT_BIT;
            body.createFixture(fixtureDef);
        }

        //static objects
        for (MapObject object : map.getLayers().get(4).getObjects()) {
            if (!ClassReflection.isInstance(RectangleMapObject.class, object))
                continue;
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);

            body = world.createBody(bodyDef);

            shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Constants.OBJECT_BIT;
            fixtureDef.filter.maskBits = Constants.WALLS_BIT |
                    Constants.OBJECT_BIT |
                    Constants.BOY_BIT |
                    Constants.MONSTER_BIT;
//            fixtureDef.filter.maskBits |= Constants.LIGHT_BIT;
            body.createFixture(fixtureDef);
        }

        //5th layer is
        //human
        for (MapObject object : map.getLayers().get(5).getObjects()) {
            if (!ClassReflection.isInstance(RectangleMapObject.class, object))
                continue;
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();
            if (LightSwitch.isPlayingHuman())
                screen.getPlayer().setGameActor(new Human(world, bounds.getX(), bounds.getY()));
            else
                screen.getEnemyPlayer().setGameActor(new Human(world, bounds.getX(), bounds.getY()));
        }

        //monster
        for (MapObject object : map.getLayers().get(6).getObjects()) {
            if (!ClassReflection.isInstance(RectangleMapObject.class, object))
                continue;
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();
            if (LightSwitch.isPlayingHuman())
                screen.getEnemyPlayer().setGameActor(new Monster(world, bounds.getX(), bounds.getY()));
            else
                screen.getPlayer().setGameActor(new Monster(world, bounds.getX(), bounds.getY()));
        }

        //lights
        for (MapObject object : map.getLayers().get(7).getObjects()) {
            if (!ClassReflection.isInstance(RectangleMapObject.class, object))
                continue;
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();

            screen.getLighting().addLight(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);
        }

    }

}
