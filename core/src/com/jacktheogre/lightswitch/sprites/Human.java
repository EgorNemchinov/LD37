package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 19.10.16.
 */
public class Human extends Actor {
    public enum State {RUNNING, STANDING}
    private State currentState;
    private State previousState;

    private final int WIDTH = 22;
    private final int HEIGHT = 26;
    public static final int MAX_SPEED = 80;

    public Human(World world, float x, float y) {
        super(world, x, y, Assets.getAssetLoader().link); // TODO: 18.10.16 get texture from atlas

        currentState = State.STANDING;
        previousState = State.STANDING;

        initGraphics();

        texture = getTexture();
        setBounds(getX(), getY(), 22, 26);
        setRegion(playerStandRight);

        initialize();
        target = new Vector2(b2body.getPosition());
    }


    protected void initGraphics() {
        Array<TextureRegion> frames = new Array<TextureRegion>();

        float frameTime = 0.1f;
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 33 + i*(WIDTH + 5), 1, WIDTH, HEIGHT));
        }
        playerRunDown = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 33 + i*(WIDTH+5), 1 + 29, WIDTH, HEIGHT));
        }
        playerRunUp = new Animation(frameTime, frames);
        frames.clear();

        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 33 + i*(WIDTH+5), 1 + 2*29, WIDTH, HEIGHT));
        }
        playerRunRight = new Animation(frameTime, frames);
        frames.clear();

        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 33 + i*(WIDTH+5), 1 + 3*29, WIDTH, HEIGHT));
        }
        playerRunLeft = new Animation(frameTime, frames);
        frames.clear();

        //set looping
        playerRunDown.setPlayMode(Animation.PlayMode.LOOP);
        playerRunRight.setPlayMode(Animation.PlayMode.LOOP);
        playerRunLeft.setPlayMode(Animation.PlayMode.LOOP);
        playerRunUp.setPlayMode(Animation.PlayMode.LOOP);

        playerStandDown = new TextureRegion(getTexture(), 1, 1, WIDTH, HEIGHT);
        playerStandUp = new TextureRegion(getTexture(), 1, 30, WIDTH, HEIGHT);
        playerStandRight = new TextureRegion(getTexture(), 1, 59, WIDTH, HEIGHT);
        playerStandLeft = new TextureRegion(getTexture(), 1, 88, WIDTH, HEIGHT);
    }

    @Override
    public void dispose() {

    }


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

    public State getState() {
        if(b2body.getLinearVelocity().x == 0 && b2body.getLinearVelocity().y == 0)
            return State.STANDING;
        else
            return State.RUNNING;
    }



    protected void initialize() {// TODO: 19.10.16 get this method to actor with x,y parameters

        BodyDef bodyDef = new BodyDef();
        CircleShape shape = new CircleShape();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.position.set(getX(), getY());
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);
        // TODO: 20.10.16 make good
        shape.setRadius(8);

        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.ACTOR_BIT;
        fixtureDef.filter.maskBits = Constants.WALLS_BIT |
                Constants.OBJECT_BIT |
                Constants.ACTOR_BIT |
                Constants.INTERACTIVE_BIT;
        filter = fixtureDef.filter;
        fixture = b2body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }

    @Override
    public String toString() {
        return "Link";
    }

    public int getSpeed() {
        return MAX_SPEED;
    }

}
