package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.ai.Agent;
import com.jacktheogre.lightswitch.ai.GraphPathImp;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.ai.Node;

/**
 * Created by luna on 19.10.16.
 */
public abstract class GameActor extends Sprite {
    protected float teleportTime;
    protected boolean teleportReady;

    protected Fixture fixture;
    protected Agent agent;
    private boolean remakingPath;

    public enum Direction {RIGHT, LEFT, UP, DOWN;}

    protected Direction direction;
    protected Direction lastDirection;

    public Body b2body;
    protected Filter filter;

    protected World world;

    protected float stateTimer;
    protected Animation playerRunLeft;

    protected Animation playerRunRight;
    protected Animation playerRunUp;
    protected Animation playerRunDown;
    protected TextureRegion playerStandRight;
    protected TextureRegion playerStandLeft;
    protected TextureRegion playerStandUp;
    protected TextureRegion playerStandDown;
    protected Vector2 target;
    protected int currentNodePointer;

    protected GraphPathImp path;
    protected Vector2 curPosition, nextPosition;
    protected Texture texture;
    protected float xVel, yVel;

    protected boolean keyboardControl;

    protected boolean isMoving;

    public GameActor(World world, float x, float y, Texture texture) {
        super(texture);
        setPosition(x, y);
        this.world = world;
        stateTimer = 0;
        teleportTime = 0;
        teleportReady = true;

        direction = Direction.RIGHT;
        lastDirection = Direction.RIGHT;

        target = new Vector2(x, y);
        currentNodePointer = 0;
        setPath(new GraphPathImp());
        curPosition = new Vector2(x,y);
        nextPosition = curPosition;
        agent = new Agent(world);
        keyboardControl = false;
        isMoving = false;
    }

    public void update(float dt) {
        if(!teleportReady) {
            teleportTime += dt;
            if(teleportTime > Constants.TELEPORT_INTERVAL) {
                teleportTime = 0;
                teleportReady = true;
            }
        }
        if(keyboardControl) {
            if(isMoving) {
                switch(direction) {
                    case DOWN:
                        b2body.setLinearVelocity(0, -getSpeed());
                        break;
                    case UP:
                        b2body.setLinearVelocity(0, getSpeed());
                        break;
                    case RIGHT:
                        b2body.setLinearVelocity(getSpeed(), 0);
                        break;
                    case LEFT:
                        b2body.setLinearVelocity(-getSpeed(), 0);
                        break;
                }
            } else
                b2body.setLinearVelocity(Vector2.Zero);
        } else {
            curPosition = b2body.getPosition();
            if(nextPosition.cpy().sub(curPosition).len() < 2) {
                nextPosition = getNextPosition();
            }
            Vector2 step = nextPosition.cpy().sub(curPosition).nor().scl(getSpeed());
            b2body.setLinearVelocity(step);
        }
        setRegion(getFrame(dt));
        setPosition(b2body.getPosition().x - getWidth() / 2 , b2body.getPosition().y - getHeight() / 2 );
    }

    public Direction calculateDirection() {
        xVel = b2body.getLinearVelocity().x;
        yVel = b2body.getLinearVelocity().y;
        if(xVel == 0 && yVel == 0)
            return getDirection();
        if(xVel > 0) {
            if(yVel > 0 && Math.abs(yVel) > Math.abs(xVel))
                return Direction.UP;
            else if(yVel < 0 && Math.abs(yVel) > Math.abs(xVel))
                return Direction.DOWN;
            else return Direction.RIGHT;
        } else if (xVel < 0) {
            if(yVel > 0 && Math.abs(yVel) > Math.abs(xVel))
                return Direction.UP;
            else if(yVel < 0 && Math.abs(yVel) > Math.abs(xVel))
                return Direction.DOWN;
            else
            {
                return Direction.LEFT;
            }
        } else if(xVel == 0) {
            if(yVel > 0)
                return Direction.UP;
            else if(yVel < 0)
                return Direction.DOWN;
        }
        return lastDirection;
    }
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public void setVelocity(Vector2 velocity) {
        this.setVelocity(velocity);
        b2body.setLinearVelocity(velocity);
    }

    public void remakePath() {
        if(remakingPath)
        {
            currentNodePointer = 0;
            agent.makePath(this);
            setRemakingPath(false);
        }
    }

    public Vector2 getVelocity() {
        return b2body.getLinearVelocity();
    }

    private Vector2 getNextPosition() {
        if (currentNodePointer >= path.getCount() - 1) {
//            Gdx.app.log("getNextPosition",toString() + " standing, pathLength is "+path.getCount());
            return curPosition;
        }
        else {
            currentNodePointer++;
//            Gdx.app.log("getNextPosition","moving to "+currentNodePointer);
            return path.get(currentNodePointer).getPosition().scl(LevelManager.tilePixelWidth, LevelManager.tilePixelHeight).add(8, 8);
        }
    }

    public void setPath(GraphPathImp path) {
//        Gdx.app.log(toString(), "set path");
        this.path = path;
        currentNodePointer = 0;
    }

    public void setTarget(Vector2 target) {
        stop();
        this.target = target;
//        Gdx.app.log("Location", "is " + b2body.getPosition());
//        Gdx.app.log("Target", toString() + " is set to " + target);
        currentNodePointer = 0;
        agent.makePath(this);
        setMoving(false);
    }

//    protected abstract boolean isRunning();

    public void setKeyboardControl(boolean control) {
        this.keyboardControl = control;
    }

    public boolean isKeyboardControl() {
        return keyboardControl;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Vector2 getTarget() {
        return target;
    }

    public GraphPathImp getPath() {
        return path;
    }

    public abstract TextureRegion getFrame(float dt);

    protected abstract void initGraphics();

    public Agent getAgent() {
        return agent;
    }

    public abstract int getSpeed();
    protected abstract void initialize();

    public void stop() {
        target = b2body.getPosition();
        b2body.setLinearVelocity(0, 0);
        path.clear();
        curPosition = b2body.getPosition();
        nextPosition = curPosition;
        setMoving(false);
    }

    public void dispose() {

    }

    public Vector2 getCurPos() {
        return curPosition;
    }

    public Vector2 getNextPos() {
        return nextPosition;
    }

    public void setCurPosition(Vector2 curPosition) {
        this.curPosition = curPosition;
    }

    public void setNextPosition(Vector2 nextPosition) {
        this.nextPosition = nextPosition;
    }

    public void setRemakingPath(boolean remakingPath) {
        this.remakingPath = remakingPath;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public String toString() {
        return "GameActor";
    }
}
