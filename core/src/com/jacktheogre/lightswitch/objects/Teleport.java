package com.jacktheogre.lightswitch.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.commands.TeleportCommand;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.sprites.Actor;
import com.jacktheogre.lightswitch.tools.Assets;

import java.util.Random;

/**
 * Created by luna on 10.12.16.
 */
public class Teleport extends InteractiveObject {

    private int x, y;
    private Array<Teleport> others;

    public Teleport(GeneratingScreen screen, int x, int y, boolean initPhysics) {
        super(screen, x, y, initPhysics);
        this.x = x;
        this.y = y;
        texture = Assets.getAssetLoader().teleport;
        initGraphics();
        others = new Array<Teleport>();
        open = true;
        timeSinceClosure = 0f;
        stateTimer = 0f;
    }



    public void activate(Actor actor) {
        if(others.size > 0){
            Teleport dest =randomTeleport();
            if(dest != null)
                screen.getCommandHandler().addCommand(new TeleportCommand(actor, this, randomTeleport()));
        }
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
        if(!open) {
            timeSinceClosure += dt;
            if(timeSinceClosure > Constants.TELEPORT_INTERVAL) {
                timeSinceClosure = 0;
                open();
            }
        }
    }

    public Array<Teleport> getOthers() {
        return others;
    }

    public void setOthers(Array<Teleport> others) {
        this.others = others;
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

    public void addTeleport(Teleport tp) {
        if(tp != null)
            others.add(tp);
    }

    private Teleport randomTeleport() {
        Random random = new Random();
        Teleport tp = null;
        if(others.size > 0) {
            for (int i = 0; i < others.size; i++) {
                if(others.get(i).isOpen()) {
                    tp = others.get(i);
                    if(random.nextBoolean())
                        break;
                }
            }
        }
        return tp;
    }
}
