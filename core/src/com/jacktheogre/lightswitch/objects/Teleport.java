package com.jacktheogre.lightswitch.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.commands.TeleportCommand;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.sprites.Player;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 10.12.16.
 */
public class Teleport extends InteractiveObject {

    private Teleport partner;
    private final Color REFRESH_COLOR = new Color(1, 1, 1, 0.5f);

    public Teleport(GeneratingScreen screen, int x, int y, boolean initPhysics) {
        super(screen, x, y, initPhysics);
        this.x = x;
        this.y = y;
        textureRegion = new TextureRegion(Assets.getAssetLoader().teleport);
        initGraphics();
        open = true;
        timeSinceClosure = 0f;
        stateTimer = 0f;
    }

    public static void connect(Teleport first, Teleport second) {
        first.setPartner(second);
        second.setPartner(first);
    }

    public void setPartner(Teleport partner) {
        this.partner = partner;
    }

    public void removePartner() {
        setPartner(null);
    }

    public boolean activate(Player player) {
        if(partner != null) {
            screen.getCommandHandler().addCommandPlay(new TeleportCommand(this, partner, player));
            return true;
        } else
            return false;
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
    public void render(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, float dt) {
        spriteBatch.draw(getFrame(dt), x, y, 2*bounds.radius, 2*bounds.radius);
        spriteBatch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(REFRESH_COLOR);
        shapeRenderer.arc(x + bounds.radius, y + bounds.radius, bounds.radius, 90f,
                360*(timeSinceClosure / Constants.TELEPORT_INTERVAL), (int) (60*(timeSinceClosure / Constants.TELEPORT_INTERVAL)) + 1);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        spriteBatch.begin();
    }

    @Override
    public void render(SpriteBatch spriteBatch, float dt) {
        spriteBatch.draw(getFrame(dt), x, y, 2*bounds.radius, 2*bounds.radius);
    }

    public void update(float dt) {
        super.update(dt);
        if(!open) {
            timeSinceClosure += dt;
            if(timeSinceClosure > Constants.TELEPORT_INTERVAL) {
                timeSinceClosure = 0;
                open();
            }
        }
    }

    @Override
    public void initPhysics() {
        super.initPhysics();
//        setFilter(Constants.TELEPORT_BIT, (short) (Constants.BOY_BIT | Constants.MONSTER_BIT), Constants.TELEPORT_GROUP);
    }

    @Override
    public void open() {
        super.open();
        Assets.getAssetLoader().teleportOpenSound.play();
    }

    @Override
    public void close() {
        super.close();
        Assets.getAssetLoader().teleportCloseSound.play();
    }

    @Override
    protected void setTransparency(boolean transparency) {
        if(transparency) {
            setFilter(Constants.TELEPORT_BIT, (short) 0, Constants.TELEPORT_GROUP);
        } else {
            setFilter(Constants.TELEPORT_BIT, (short) (Constants.BOY_BIT | Constants.MONSTER_BIT), Constants.TELEPORT_GROUP);
        }
    }

    @Override
    protected Filter getFilter() {
        Filter filter = new Filter();
        filter.categoryBits = Constants.TELEPORT_BIT;
        filter.maskBits = Constants.BOY_BIT | Constants.MONSTER_BIT;
        filter.groupIndex = Constants.TELEPORT_GROUP;
        return filter;
    }

    public Teleport getPartner() {
        return partner;
    }
}
