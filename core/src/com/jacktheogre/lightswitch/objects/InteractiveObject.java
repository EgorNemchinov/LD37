package com.jacktheogre.lightswitch.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    protected  boolean open;
    protected float timeSinceClosure, stateTimer;
    protected Animation openAnimation, closingAnimation;

    public abstract void render(SpriteBatch spriteBatch, float dt);
    public abstract void activate(Actor actor);

    public InteractiveObject(GeneratingScreen screen, int x, int y) {
        this(screen, x, y, true);
    }

    public InteractiveObject(GeneratingScreen screen, int x, int y, boolean initPhysics) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.x = x;
        this.y = y;
        this.bounds = new Circle((x + LevelManager.tilePixelWidth / 2), (y + LevelManager.tilePixelHeight/ 2), LevelManager.tilePixelWidth / 2 - 1);

        if(initPhysics)
            initPhysics();
    }

    public void initPhysics(){

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x + bounds.radius, y + bounds.radius);

        body = world.createBody(bdef);

        shape.setRadius(bounds.radius);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);
        fixture.setUserData(this);
        setCategoryFilter(Constants.INTERACTIVE_BIT);
    }

    protected abstract void initGraphics();
    protected abstract TextureRegion getFrame(float dt);

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }



    public void open() {
        setOpen(true);
        Filter filter = fixture.getFilterData();
        filter.categoryBits = Constants.INTERACTIVE_BIT;
        filter.maskBits = Constants.ACTOR_BIT;
        fixture.setFilterData(filter);
        stateTimer = 0;
    }

    public void close() {
        setOpen(false);
        Filter filter = fixture.getFilterData();
        filter.categoryBits = Constants.TRANSPARENT_BIT;
        filter.maskBits = 0;
        fixture.setFilterData(filter);
        stateTimer = 0;
    }

    public abstract void update(float dt);

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
