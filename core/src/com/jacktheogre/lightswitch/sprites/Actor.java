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
public abstract class Actor extends Sprite {
    protected float teleportTime;
    protected boolean teleportReady;

    protected Fixture fixture;
    protected Agent agent;

    public enum Direction {RIGHT, LEFT, UP, DOWN;}
    protected Direction direction;

    protected Direction lastDirection;
    public Body b2body;

    protected Filter filter, transparent;

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

    public Actor(World world, float x, float y, Texture texture) {
        super(texture);
        setPosition(x, y);
        this.world = world;
        stateTimer = 0;
        teleportTime = 0;
        teleportReady = true;
        transparent = new Filter();
        transparent.maskBits = 0;
        transparent.categoryBits = Constants.TRANSPARENT_BIT;

        direction = Direction.RIGHT;
        lastDirection = Direction.RIGHT;

        target = new Vector2(x, y);
        currentNodePointer = 0;
        setPath(new GraphPathImp());
        curPosition = new Vector2(x,y);
        nextPosition = curPosition;
        agent = new Agent(world);
    }

    public void update(float dt) {
        if(!teleportReady)
            teleportTime += dt;

        curPosition = b2body.getPosition();
        if(nextPosition.cpy().sub(curPosition).len() < 2) {
            nextPosition = getNextPosition();
        }
        Vector2 step = nextPosition.cpy().sub(curPosition).nor().scl(getSpeed());
        b2body.setLinearVelocity(step);
        setPosition(b2body.getPosition().x - getWidth() / 2 , b2body.getPosition().y - getHeight() / 2 );
        setRegion(getFrame(dt));
    }
    public Direction getDirection() {
        xVel = b2body.getLinearVelocity().x;
        yVel = b2body.getLinearVelocity().y;
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

    public void setVelocity(Vector2 velocity) {
        this.setVelocity(velocity);
        b2body.setLinearVelocity(velocity);
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
    }

    public Vector2 getTarget() {
        return target;
    }

    public GraphPathImp getPath() {
        return path;
    }

    public void setTeleportReady(boolean ready) {
        this.teleportReady = ready;
    }

    public abstract TextureRegion getFrame(float dt);

    protected abstract void initGraphics();

    public Agent getAgent() {
        return agent;
    }

    public boolean isTeleportReady() {
        if(teleportTime > Constants.TELEPORT_INTERVAL) {
            teleportTime = 0;
            teleportReady = true;
        }
        return teleportReady;
    }

    public abstract void dispose();
    public abstract int getSpeed();
    protected abstract void initialize();


    private void setTransparent() {
        fixture.setFilterData(transparent);
    }

    private void setSolid() {
        fixture.setFilterData(filter);
    }

    public void stop() {
        b2body.setLinearVelocity(0, 0);
        path.clear();
        curPosition = b2body.getPosition();
        nextPosition = curPosition;
    }
    public String toString() {
        return "Actor";
    }
}
