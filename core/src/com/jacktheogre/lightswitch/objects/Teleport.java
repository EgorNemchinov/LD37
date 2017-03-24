package com.jacktheogre.lightswitch.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.commands.TeleportCommand;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Player;
import com.jacktheogre.lightswitch.tools.Assets;

import java.util.Random;

/**
 * Created by luna on 10.12.16.
 */
public class Teleport extends InteractiveObject {

    private Teleport partner;

    public Teleport(GeneratingScreen screen, int x, int y, boolean initPhysics) {
        super(screen, x, y, initPhysics);
        this.x = x;
        this.y = y;
        texture = Assets.getAssetLoader().teleport;
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
            screen.getCommandHandler().addCommand(new TeleportCommand(this, partner, player));
            return true;
        } else
            return false;
    }

    @Override
    protected void initGraphics() {
        Array<TextureRegion> frames = new Array<TextureRegion>();

        float frameTime = 0.1f;
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(texture, i*16, 0, 16, 16));
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
