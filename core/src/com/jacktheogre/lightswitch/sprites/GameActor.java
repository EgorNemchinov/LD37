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

    public enum VerticalDirection {
        UP('U'), NONE('N'), DOWN('D');

        private char letter;

        VerticalDirection(char letter) {
            this.letter = letter;
        }
        public String toString() {
            return String.valueOf(letter);
        }
        public static VerticalDirection getByLetter(char c) {
            if(UP.toString().equals(c))
                return UP;
            else if(DOWN.toString().equals(c))
                return DOWN;
            else if(NONE.toString().equals(c))
                return NONE;
            else {
                Gdx.app.log("VerticalDirection", "Called getByLetter("+c+")");
                return NONE;
            }
        }
    }
    public enum HorizontalDirection {
        LEFT('L'), NONE('N'), RIGHT('R');

        private char letter;

        HorizontalDirection(char letter) {
            this.letter = letter;
        }
        public String toString() {
            return String.valueOf(letter);
        }
        public static HorizontalDirection getByLetter(char c) {
            if(LEFT.toString().equals(c))
                return LEFT;
            else if(RIGHT.toString().equals(c))
                return RIGHT;
            else if(NONE.toString().equals(c))
                return NONE;
            else {
                Gdx.app.log("HorizontalDirection", "Called getByLetter("+c+")");
                return NONE;
            }
        }
    }

    public static class Direction {
        private HorizontalDirection horizontalDirection, lastHorizontalDirection = HorizontalDirection.RIGHT;
        private VerticalDirection verticalDirection, lastVerticalDirection = VerticalDirection.DOWN;

        public Direction(String string) {
            if(string.length() != 2) {
                Gdx.app.error("Direction","Attempt to create Direction with string "+ string);
                Gdx.app.exit();
            }
            horizontalDirection = HorizontalDirection.getByLetter(string.charAt(0));
            verticalDirection = VerticalDirection.getByLetter(string.charAt(1));
        }
        public Direction(HorizontalDirection horizontalDirection, VerticalDirection verticalDirection) {
            this.horizontalDirection = horizontalDirection;
            this.verticalDirection = verticalDirection;
        }
        public Direction() {
            this(HorizontalDirection.NONE, VerticalDirection.NONE);
        }

        public static Direction getZero() {
            return new Direction(HorizontalDirection.NONE, VerticalDirection.NONE);
        }

        public String toString() {
            return horizontalDirection.toString() + verticalDirection.toString();
        }

        public HorizontalDirection getHorizontalDirection() {
            return horizontalDirection;
        }

        public void setHorizontalDirection(HorizontalDirection horizontalDirection) {
            if(this.horizontalDirection != HorizontalDirection.NONE) {
                lastHorizontalDirection = this.horizontalDirection;
            }
            this.horizontalDirection = horizontalDirection;
        }

        public VerticalDirection getVerticalDirection() {
            return verticalDirection;
        }

        public void setVerticalDirection(VerticalDirection verticalDirection) {
            if(this.verticalDirection != VerticalDirection.NONE) {
                this.lastVerticalDirection = this.verticalDirection;
            }
            this.verticalDirection = verticalDirection;
        }

        public HorizontalDirection getLastHorizontalDirection() {
            return lastHorizontalDirection;
        }

        public VerticalDirection getLastVerticalDirection() {
            return lastVerticalDirection;
        }
    }

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

    protected boolean isMoving;//// TODO: 19.04.17 remove it

    public GameActor(World world, float x, float y, Texture texture) {
        super(texture);
        setPosition(x, y);
        this.world = world;
        stateTimer = 0;

        direction = Direction.getZero();
        lastDirection = Direction.getZero();

        target = new Vector2(x, y);
        currentNodePointer = 0;
        setPath(new GraphPathImp());
        curPosition = new Vector2(x,y);
        nextPosition = curPosition;
        agent = new Agent(world);
        keyboardControl = false;
        isMoving = false;
    }

    private HorizontalDirection lastHorizontalDirection;
    private VerticalDirection lastVerticalDirection;
    public void update(float dt) {
        if(!teleportReady) {
            teleportTime += dt;
            if(teleportTime > Constants.TELEPORT_INTERVAL) {
                teleportTime = 0;
                teleportReady = true;
            }
        }
        if(keyboardControl) {
            Vector2 velocity = new Vector2(0, 0);
            switch (direction.horizontalDirection) {
                case LEFT:
                    velocity.x = -1;
                    break;
                case RIGHT:
                    velocity.x = 1;
                    break;
                default:
                case NONE:
            }
            switch (direction.verticalDirection) {
                case UP:
                    velocity.y = 1;
                    break;
                case DOWN:
                    velocity.y = -1;
                    break;
                default:
                case NONE:
            }
//            Gdx.app.log("Direction",direction.toString());
            b2body.setLinearVelocity(velocity.nor().scl(getSpeed()));
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
//        Gdx.app.log("Actor", "Speed is "+ b2body.getLinearVelocity());
    }


    public Direction calculateDirection() {
        xVel = b2body.getLinearVelocity().x;
        yVel = b2body.getLinearVelocity().y;
        if(xVel == 0 && yVel == 0)
            return Direction.getZero();
        Direction direction = new Direction();
        if(xVel < 0)
            direction.horizontalDirection = HorizontalDirection.LEFT;
        else if(xVel > 0)
            direction.horizontalDirection = HorizontalDirection.RIGHT;
        else
            direction.horizontalDirection = HorizontalDirection.NONE;

        if(yVel < 0)
            direction.verticalDirection = VerticalDirection.DOWN;
        else if(yVel > 0)
            direction.verticalDirection = VerticalDirection.DOWN;
        else
            direction.verticalDirection = VerticalDirection.NONE;
/*

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
*/
        return direction;
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

    void setFilter(short categoryBits, short maskBits, short groupIndex) {
        Filter filter = new Filter();
        filter.categoryBits = categoryBits;
        filter.maskBits = maskBits;
        filter.groupIndex = groupIndex;
        fixture.setFilterData(filter);
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
