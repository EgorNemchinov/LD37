package com.jacktheogre.lightswitch.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Player;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 24.03.17.
 */

public class Shard extends InteractiveObject {

    private PlayScreen playScreen;
    private Rectangle rectBounds;

    private float alpha;
    private boolean risingAlpha = false;

    private int number;
    private boolean picked = false;

    public Shard(GeneratingScreen screen, int x, int y, int numberOfShard, int totalAmount) {
        super(screen, x, y, true);
        if(totalAmount == 3)
            this.textureRegion = Assets.getAssetLoader().threeShards[numberOfShard];
        else if(totalAmount == 2) {
            this.textureRegion = Assets.getAssetLoader().twoShards[numberOfShard];
        }
        rectBounds = new Rectangle(x, y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        initGraphics();
        stateTimer = 0f;
        number = numberOfShard;
        alpha = 0.90f - 0.25f*numberOfShard;
    }

    @Override
    public boolean activate(Player player) {
        if(picked)
            return false;
//        Gdx.app.log("Shard", "activate.start");
        Assets.getAssetLoader().shardsSounds[number].play();
        picked = true;
        playScreen.getShards().removeValue(this, true);
//        Gdx.app.log("Shard", "shard removed");
        playScreen.collectShard(number);
//        Gdx.app.log("Shard", "shard collected");
        return false;
    }

    @Override
    public void render(SpriteBatch spriteBatch, float dt) {
        Color c = spriteBatch.getColor();
        if(playScreen != null) //means if we are playing already
            spriteBatch.setColor(c.r, c.g, c.b, currentAlpha(dt));
        spriteBatch.draw(textureRegion, x, y, rectBounds.getWidth(), rectBounds.getHeight());
        spriteBatch.setColor(c);
    }

    private float currentAlpha(float dt) {
        if(alpha <= 0.3f) {
            risingAlpha = true;
        } else if(alpha >= 1f) {
            risingAlpha = false;
        }
        if(risingAlpha)
            alpha += dt * (0.6f - (number % 2)*0.2f - number * 0.1f);
        else
            alpha -= dt * (0.6f - (number % 2)*0.2f - number * 0.1f);

        if(alpha < 0) alpha = 0;
        if(alpha > 1) alpha = 1;

        return alpha;
    }

    public boolean isPicked() {
        return picked;
    }

    @Override
    protected void initGraphics() {
        /*Array<TextureRegion> frames = new Array<TextureRegion>();

        float frameTime = 0.1f;
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(textureRegion, i*16, 0, 16, 16));
        }
        openAnimation = new Animation(frameTime, frames);
        openAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        frames.reverse();

        closingAnimation = new Animation(frameTime,frames);
        closingAnimation.setPlayMode(Animation.PlayMode.NORMAL);*/
    }

    @Override
    protected TextureRegion getFrame(float dt) {
        return null;
    }

    @Override
    protected void setTransparency(boolean transparency) {
        setFilter(Constants.PICKABLE_BIT, Constants.BOY_BIT, Constants.PICKABLE_GROUP);
    }

    public void setPlayScreen(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

    @Override
    public void initPhysics() {
        FixtureDef fdef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(5.5f, 5.5f); // FIXME: 25.03.17 replace with reference to rectBounds(not initialized yet)

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x + 5.5f, y + 5.5f);

        body = world.createBody(bdef);
        fdef.shape = polygonShape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = getFilter().categoryBits;
        fdef.filter.maskBits = getFilter().maskBits;
        fdef.filter.groupIndex = getFilter().groupIndex;

        fixture = body.createFixture(fdef);
        fixture.setUserData(this);
    }

    @Override
    protected Filter getFilter() {
        Filter filter = new Filter();
        filter.categoryBits = Constants.PICKABLE_BIT;
        filter.maskBits = Constants.BOY_BIT;
        filter.groupIndex = Constants.PICKABLE_GROUP;
        return filter;
    }
}

