package com.jacktheogre.lightswitch.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
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
import com.jacktheogre.lightswitch.sprites.Player;

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

    protected int index;

    public static class Indexer {
        static int i = 0;

        public static int getIndex() {
            i++;
            return i;
        }

        public static void nullify() {
            i = 0;
        }
    }


    private boolean initialOpen = false;

    public abstract void render(SpriteBatch spriteBatch, float dt);
    public abstract boolean activate(Player player);

    public InteractiveObject(GeneratingScreen screen, int x, int y, boolean initPhysics) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.x = x;
        this.y = y;
        this.bounds = new Circle((x + LevelManager.tilePixelWidth / 2), (y + LevelManager.tilePixelHeight/ 2), LevelManager.tilePixelWidth / 2 - 1);

        if(initPhysics)
            initPhysics();
    }

    // TODO: 06.02.17 mb initPhysics(short bit)
    public void initPhysics(){

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x + bounds.radius, y + bounds.radius);

        body = world.createBody(bdef);

        shape.setRadius(bounds.radius);
        fdef.shape = shape;
        fdef.filter.categoryBits = getFilter().categoryBits;
        fdef.filter.maskBits = getFilter().maskBits;
        fdef.filter.groupIndex = getFilter().groupIndex;
        fixture = body.createFixture(fdef);
        fixture.setUserData(this);
    }

    protected Filter getFilter() {
        Filter filter = new Filter();
        filter.categoryBits = Constants.TRANSPARENT_BIT;
        filter.maskBits = 0;
        return filter;
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
        setTransparency(false);
        stateTimer = 0;
    }

    public void close() {
        setOpen(false);
        setTransparency(true);
        stateTimer = 0;
    }

    protected abstract void setTransparency(boolean transparency);

    public  void update(float dt) {
        //better be done in another way
        if(!initialOpen) {
            initOpen();
            initialOpen = true;
        }
    }

    void setFilter(short categoryBits, short maskBits, short groupIndex) {
        Filter filter = new Filter();
        filter.categoryBits = categoryBits;
        filter.maskBits = maskBits;
        filter.groupIndex = groupIndex;
        fixture.setFilterData(filter);
    }

    public void initClose() {
        setTransparency(true);
        index = Indexer.getIndex();
    }

     protected void initOpen() {
        setTransparency(false);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector2 getCenter() {
        return new Vector2(x + 8, y + 8);
    }

    public int getIndex() {
        return index;
    }
}
