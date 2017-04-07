package com.jacktheogre.lightswitch.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.commands.TrapTriggerCommand;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.sprites.Monster;
import com.jacktheogre.lightswitch.sprites.Player;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 05.02.17.
 */

public class Trap extends InteractiveObject {

    private float timeSinceTrigger = 0;
    private boolean triggered = false;
    private Monster target;
    // is it infinite like teleport or breakable

    public Trap(GeneratingScreen screen, int x, int y, boolean initPhysics) {
        super(screen, x, y, initPhysics);
        this.x = x;
        this.y = y;
        textureRegion = new TextureRegion(Assets.getAssetLoader().trap);
        initGraphics();
        open = true;
        timeSinceClosure = 0f;
        stateTimer = 0f;
    }

    //circle bounds?
    @Override
    public void render(SpriteBatch spriteBatch, float dt) {
        spriteBatch.draw(getFrame(dt), x, y, 2*bounds.radius, 2*bounds.radius);
    }

    @Override
    protected void initGraphics() {
        Array<TextureRegion> frames = new Array<TextureRegion>();

        float frameTime = 0.1f;
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(textureRegion, i*16, 0, 16, 16));
        }
        openAnimation = new Animation(frameTime, frames);
        openAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        frames.reverse();

        closingAnimation = new Animation(frameTime,frames);
        closingAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    @Override
    protected TextureRegion getFrame(float dt) {
        TextureRegion region;

        if(open) {
            region = openAnimation.getKeyFrame(stateTimer);
        }
        else
            region = closingAnimation.getKeyFrame(stateTimer);

        stateTimer += dt;
        return region;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if(!open) {
            timeSinceClosure += dt;
            if(timeSinceClosure > Constants.TRAP_INTERVAL) {
                timeSinceClosure = 0;
                open();
            }
        }
        if(triggered) {
            timeSinceTrigger += dt;
            if(timeSinceTrigger > Constants.TRAP_TRIGGER_TIME) {
                timeSinceTrigger = 0;
                triggered = false;
                catchMonster();
                close();
            }
        }
    }

    @Override
    public boolean activate(Player player) {
        if(!triggered)
            screen.getCommandHandler().addCommandGenerate(new TrapTriggerCommand(this, player));
        // TODO: 06.02.17 when false?
        return true;
    }

    //called when time is off
    private void catchMonster() {
        if(target.b2body.getPosition().epsilonEquals(getCenter(), 12)) {
            target.getCaught();
        }
    }

    //gets closed and sets triggered=true. called when monster contacts trap
    public boolean trigger(Monster monster) {
        if(triggered)
            return false;
        this.target = monster;
        triggered = true;
        initClose();
        return true;
    }


    @Override
    public void open() {
        super.open();
        //play sound
    }

    @Override
    public void close() {
        super.close();
        //play sound
    }

    @Override
    protected void setTransparency(boolean transparency) {
        if(transparency) {
            setFilter(Constants.TRAP_BIT, (short)0, Constants.TRAP_GROUP);
        } else {
            setFilter(Constants.TRAP_BIT, Constants.MONSTER_BIT, Constants.TRAP_GROUP);
        }
    }

    @Override
    public void initPhysics() {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x + bounds.radius, y + bounds.radius);

        body = world.createBody(bdef);

        shape.setRadius(4);
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = getFilter().categoryBits;
        fdef.filter.maskBits = getFilter().maskBits;
        fdef.filter.groupIndex = getFilter().groupIndex;
        fixture = body.createFixture(fdef);
        fixture.setUserData(this);
//        setFilter(Constants.TRAP_BIT, (short) 0, Constants.TRAP_GROUP);
    }


    @Override
    protected Filter getFilter() {
        Filter filter = new Filter();
        filter.categoryBits = Constants.TRAP_BIT;
        filter.maskBits = Constants.MONSTER_BIT;
        filter.groupIndex = Constants.TRAP_GROUP;
        return filter;
    }

    public boolean isTriggered() {
        return triggered;
    }
}
