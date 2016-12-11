package com.jacktheogre.lightswitch.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Actor;

/**
 * Created by luna on 10.12.16.
 */
public abstract class InteractiveObject {

    protected int x, y;
    protected Texture texture;
    protected World world;
    protected Circle bounds;
    protected Body body;
    protected GeneratingScreen screen;
    protected Fixture fixture;

    // TODO: 10.12.16 may be animation
    public abstract void render(SpriteBatch spriteBatch);
    public abstract void activate(Actor actor);

    public InteractiveObject(GeneratingScreen screen, int x, int y) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.x = x;
        this.y = y;
        this.bounds = new Circle((x + LevelManager.tilePixelWidth / 2), (y + LevelManager.tilePixelHeight/ 2), LevelManager.tilePixelWidth / 2);

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x + bounds.radius, y + bounds.radius);

        body = world.createBody(bdef);

        shape.setRadius(bounds.radius);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);
    }

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        filter.maskBits = Constants.ACTOR_BIT;
        fixture.setFilterData(filter);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
