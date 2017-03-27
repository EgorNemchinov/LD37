package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.objects.Shard;
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
                    Constants.MONSTER_BIT |
                    Constants.LIGHT_BIT;
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

        int number = 0;
        Array<Vector2> shardsPositions = new Array<Vector2>();
        for (MapObject object : map.getLayers().get(7).getObjects()) {
            if (!ClassReflection.isInstance(RectangleMapObject.class, object))
                continue;
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();
            if(number > 2) {
                Gdx.app.error("", "More than 3 shards on a level.");
                break;
            }
            shardsPositions.add(new Vector2(bounds.getX(), bounds.getY()));

            number++;
        }
        if(number < 2)
            Gdx.app.error("", "Less than 2 moonshards");;
        int offset = 0;
        if(number == 2)
            offset = 1;
        else if(number == 3)
            offset = 3;
        for (int i = 0; i < number; i++) {
            screen.getShards().add(new Shard(screen, (int)shardsPositions.get(i).x + offset, (int)shardsPositions.get(i).y + offset, i, number));
        }
        LevelManager.setAmountOfShards(number);

        //lights
        for (MapObject object : map.getLayers().get(8).getObjects()) {
            if (!ClassReflection.isInstance(RectangleMapObject.class, object))
                continue;
            Rectangle bounds = ((RectangleMapObject) object).getRectangle();

            screen.getLighting().addLight(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);
        }

    }

}
