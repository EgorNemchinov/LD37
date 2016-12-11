package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 10.12.16.
 */
public class Enemy extends Actor {
    private static final int MAX_SPEED = 64;

    public enum State {RUNNING, STANDING}
    private State currentState;
    private State previousState;


    public Enemy(World world, float x, float y) {
        super(world, x, y, Assets.getAssetLoader().zelda);

        currentState = State.STANDING;
        previousState = State.STANDING;

        initGraphics();

        texture = getTexture();
        playerStandRight = new TextureRegion(texture, 0, 0, 21, 21);
        setBounds(getX(), getY(), 21, 21);
        setRegion(playerStandRight);

        initialize();
        target = new Vector2(b2body.getPosition());

    }

    protected void initialize() {
        BodyDef bodyDef = new BodyDef();
        CircleShape shape = new CircleShape();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.position.set(getX(), getY());
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        shape.setRadius(8);

        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.ACTOR_BIT;
        fixtureDef.filter.maskBits = Constants.WALLS_BIT |
                Constants.OBJECT_BIT |
                Constants.ACTOR_BIT | Constants.INTERACTIVE_BIT;
        filter = fixtureDef.filter;
        fixture = b2body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }

    public State getState() {
        if(b2body.getLinearVelocity().x == 0 && b2body.getLinearVelocity().y == 0)
            return State.STANDING;
        else
            return State.RUNNING;
    }


    @Override
    public TextureRegion getFrame(float dt) {
        currentState = getState();
        direction = getDirection();

        TextureRegion region;
        switch (currentState) {
            case RUNNING:
                switch (direction) {
                    case LEFT:
                        region = playerRunLeft.getKeyFrame(stateTimer);
                        break;
                    case UP:
                        region = playerRunUp.getKeyFrame(stateTimer);
                        break;
                    case DOWN:
                        region = playerRunDown.getKeyFrame(stateTimer);
                        break;
                    case RIGHT:
                    default:
                        region = playerRunRight.getKeyFrame(stateTimer);
                        break;
                }
                break;
            case STANDING:
            default:
                switch (direction) {
                    case LEFT:
                        region = playerStandLeft;
                        break;
                    case UP:
                        region = playerStandUp;
                        break;
                    case DOWN:
                        region = playerStandDown;
                        break;
                    case RIGHT:
                    default:
                        region = playerStandRight;
                        break;
                }
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        lastDirection = direction;

        return region;
    }


    @Override
    protected void initGraphics() {
        Array<TextureRegion> frames = new Array<TextureRegion>();

        float frameTime = 0.2f;
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(getTexture(), i*20, 0, 20, 21));
        }
        playerRunDown = new Animation(frameTime, frames);
        frames.clear();

        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(getTexture(), 40 + i*20, 0, 20, 21));
        }
        playerRunRight = new Animation(frameTime, frames);
        frames.clear();

        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(getTexture(), 40 + i*20, 0, 20, 21));
            frames.get(i).flip(true, false);
        }
        playerRunLeft = new Animation(frameTime, frames);
        frames.clear();

        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(getTexture(), 115 + i*20, 0, 20, 21));
        }
        playerRunUp = new Animation(frameTime, frames);
        frames.clear();


        //set looping
        playerRunDown.setPlayMode(Animation.PlayMode.LOOP);
        playerRunRight.setPlayMode(Animation.PlayMode.LOOP);
        playerRunLeft.setPlayMode(Animation.PlayMode.LOOP);
        playerRunUp.setPlayMode(Animation.PlayMode.LOOP);

        playerStandDown = new TextureRegion(getTexture(), 0, 0, 20, 21);
        playerStandUp = new TextureRegion(getTexture(), 115, 0, 20, 21);
        playerStandRight = new TextureRegion(getTexture(), 40, 0, 20, 21);
        playerStandLeft = new TextureRegion(playerStandRight);
        playerStandLeft.flip(true, false);
    }

    @Override
    public String toString() {
        return "Enemy";
    }

    @Override
    public void dispose() {

    }

    @Override
    public int getSpeed() {
        return MAX_SPEED;
    }
}
