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
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 10.12.16.
 */
public class Monster extends GameActor {
    private static final int SPEED_BASE = 64 ;

    private final int WIDTH = 22, HEIGHT = 26;

    public enum State {RUNNING, STANDING;}

    private State currentState;
    private State previousState;
    private float timeSinceTrapped = 0;

    private boolean trapped = false;
    public Monster(World world, float x, float y) {
        super(world, x, y, Assets.getAssetLoader().characters);

        currentState = State.STANDING;
        previousState = State.STANDING;

        initGraphics();

        texture = getTexture();
        setBounds(getX(), getY(), WIDTH, HEIGHT);
        setRegion(playerStandLeft);

        initialize();
        target = new Vector2(b2body.getPosition());

    }


    protected void initialize() {
        BodyDef bodyDef = new BodyDef();
        CircleShape shape = new CircleShape();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.position.set(getX() + 8, getY() + 8);
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        shape.setRadius(7);

        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.MONSTER_BIT;
        fixtureDef.filter.maskBits = Constants.WALLS_BIT |
                Constants.OBJECT_BIT |
                Constants.BOY_BIT |
                Constants.TELEPORT_BIT |
                Constants.TRAP_BIT |
                Constants.LIGHT_BIT;
        fixtureDef.filter.groupIndex = Constants.MONSTER_GROUP;
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
    public void update(float dt) {
        // TODO: 06.02.17 if AI is playing then stop() and then change nextPosition or overwrite update
        super.update(dt);
        setPosition(b2body.getPosition().x - getWidth() / 2 , b2body.getPosition().y - getHeight() / 2 + 5);
        if(trapped)
            timeSinceTrapped += dt;
        if(timeSinceTrapped > Constants.MONSTER_IN_TRAP) {
            timeSinceTrapped = 0;
            trapped = false;
            isMoving = true;
        }
    }

    @Override
    public TextureRegion getFrame(float dt) {
        currentState = getState();
        direction = keyboardControl?getDirection():calculateDirection();

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
                switch(direction.getHorizontalDirection() == HorizontalDirection.NONE ? direction.getLastHorizontalDirection() :direction.getHorizontalDirection()) {
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


    @Override
    protected void initGraphics() {
        Array<TextureRegion> frames = new Array<TextureRegion>();

        float frameTime = 0.2f;
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 29 + i*(WIDTH+2), 1, WIDTH, HEIGHT));
        }
        playerRunDown = new Animation(frameTime, frames);
        frames.clear();

        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 29 + i*(WIDTH+2), 1 + 28, WIDTH, HEIGHT));
        }
        playerRunUp = new Animation(frameTime, frames);
        frames.clear();

        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 29 + i*(WIDTH+2), 1 + 2*28, WIDTH, HEIGHT));
        }
        playerRunRight = new Animation(frameTime, frames);
        frames.clear();

        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 29 + i*(WIDTH+2), 1 + 3*28, WIDTH, HEIGHT));
        }
        playerRunLeft = new Animation(frameTime, frames);
        frames.clear();

        playerStandDown = new TextureRegion(getTexture(), 1, 1, WIDTH, HEIGHT);
        playerStandUp = new TextureRegion(getTexture(), 1, 1 + 1*28, WIDTH, HEIGHT);
        playerStandRight = new TextureRegion(getTexture(), 1, 1 + 2*28, WIDTH, HEIGHT);
        playerStandLeft = new TextureRegion(getTexture(), 1, 1 + 3*28, WIDTH, HEIGHT);

        //set looping
        playerRunDown.setPlayMode(Animation.PlayMode.LOOP);
        playerRunRight.setPlayMode(Animation.PlayMode.LOOP);
        playerRunLeft.setPlayMode(Animation.PlayMode.LOOP);
        playerRunUp.setPlayMode(Animation.PlayMode.LOOP);

    }

    public void getCaught() {
        trapped = true;
    }

    @Override
    public void setMoving(boolean moving) {
        this.isMoving = !trapped && moving;
    }

    @Override
    public String toString() {
        return "Monster";
    }

    @Override
    public void dispose() {

    }

    @Override
    public int getSpeed() {
        if(trapped)
            return 0;
        else
            return (int) (LevelManager.getMonsterSpeed()*Constants.GAME_SPEED);
    }
}
