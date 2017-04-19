package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 19.10.16.
 */
public class Human extends GameActor {
    public static final int MAX_SPEED = 50;

    private final int WIDTH = 22;
    private final int HEIGHT = 26;

    public enum State {RUNNING, STANDING}
    private State currentState;
    private State previousState;

    public Human(World world, float x, float y) {
        super(world, x, y, Assets.getAssetLoader().characters);

        currentState = State.STANDING;
        previousState = State.STANDING;

        initGraphics();

        texture = getTexture();
        setBounds(getX(), getY(), WIDTH, HEIGHT);
        setRegion(playerStandRight);

        initialize();
        target = new Vector2(b2body.getPosition());
    }


    protected void initGraphics() {
        Array<TextureRegion> frames = new Array<TextureRegion>();

        float frameTime = 0.1f;
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 29 + i*(WIDTH+2), 113, WIDTH, HEIGHT));
        }
        playerRunDown = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 29 + i*(WIDTH+2), 113 + 28, WIDTH, HEIGHT));
        }
        playerRunUp = new Animation(frameTime, frames);
        frames.clear();

        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 29 + i*(WIDTH+2), 113 + 2*28, WIDTH, HEIGHT));
        }
        playerRunRight = new Animation(frameTime, frames);
        frames.clear();

        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 29 + i*(WIDTH+2), 113 + 3*28, WIDTH, HEIGHT));
        }
        playerRunLeft = new Animation(frameTime, frames);
        frames.clear();

        //set looping
        playerRunDown.setPlayMode(Animation.PlayMode.LOOP);
        playerRunRight.setPlayMode(Animation.PlayMode.LOOP);
        playerRunLeft.setPlayMode(Animation.PlayMode.LOOP);
        playerRunUp.setPlayMode(Animation.PlayMode.LOOP);

        playerStandDown = new TextureRegion(getTexture(), 1, 113, WIDTH, HEIGHT);
        playerStandUp = new TextureRegion(getTexture(), 1, 141, WIDTH, HEIGHT);
        playerStandRight = new TextureRegion(getTexture(), 1, 169, WIDTH, HEIGHT);
        playerStandLeft = new TextureRegion(getTexture(), 1, 197, WIDTH, HEIGHT);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(b2body.getPosition().x - getWidth() / 2 , b2body.getPosition().y - getHeight() / 2 + 5);
    }

    @Override
    public TextureRegion getFrame(float dt) {
        currentState = getState();
        direction = getDirection();

        TextureRegion region = null;
        switch (currentState) {
            case RUNNING:
                switch (direction.getVerticalDirection() == VerticalDirection.NONE ? direction.getLastVerticalDirection() :direction.getVerticalDirection()) {
                    case UP:
                        region = playerRunUp.getKeyFrame(stateTimer);
                        break;
                    case DOWN:
                        region = playerRunDown.getKeyFrame(stateTimer);
                        break;
                    default:
                    case NONE:
                }
                switch(direction.getHorizontalDirection() == HorizontalDirection.NONE && region == null ?
                        direction.getLastHorizontalDirection() :direction.getHorizontalDirection()) {
                    case LEFT:
                        region = playerRunLeft.getKeyFrame(stateTimer);
                        break;
                    case RIGHT:
                        region = playerRunRight.getKeyFrame(stateTimer);
                        break;
                    default:
                    case NONE:
                }
                if(region == null) {
                    region = playerStandRight;
                }
                break;
            case STANDING:
            default:
                switch (direction.getVerticalDirection() == VerticalDirection.NONE ? direction.getLastVerticalDirection() :direction.getVerticalDirection()) {
                    case UP:
                        region = playerStandUp;
                        break;
                    case DOWN:
                        region = playerStandDown;
                        break;
                    default:
                    case NONE:
                }
                switch(direction.getHorizontalDirection() == HorizontalDirection.NONE ? direction.getLastHorizontalDirection() :direction.getHorizontalDirection()) {
                    case LEFT:
                        region = playerStandLeft;
                        break;
                    case RIGHT:
                        region = playerStandRight;
                        break;
                    default:
                    case NONE:
                }
                if(region == null) {
                    region = playerStandRight;
                }
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        lastDirection = direction;

        return region;
    }

    public State getState() {
        if(b2body.getLinearVelocity().x == 0 && b2body.getLinearVelocity().y == 0) {
//            Assets.getAssetLoader().runningSound.pause();
            return State.STANDING;
        }
        else {
//            if(previousState == State.STANDING)
//                Assets.getAssetLoader().runningSound.setVolume(Assets.getAssetLoader().runningSound.loop(), 0.3f);
            return State.RUNNING;
        }
    }

    protected void initialize() {

        BodyDef bodyDef = new BodyDef();
        CircleShape shape = new CircleShape();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.position.set(getX()+8, getY()+8);
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);
        // TODO: 31.01.17 fix physical model
        shape.setRadius(7);

        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.BOY_BIT;
        //obivously if add more boys then boy also
        fixtureDef.filter.maskBits = Constants.WALLS_BIT |
                Constants.OBJECT_BIT |
                Constants.MONSTER_BIT |
                Constants.TELEPORT_BIT |
                Constants.PICKABLE_BIT;
        fixtureDef.filter.groupIndex = Constants.BOY_GROUP;
        filter = fixtureDef.filter;
        fixtureDef.friction = 0;
        fixture = b2body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }



    @Override
    public String toString() {
        return "Hero";
    }

    public int getSpeed() {
        return (int) (MAX_SPEED*Constants.GAME_SPEED);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
