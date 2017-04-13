package com.jacktheogre.lightswitch;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.commands.TurnOffCommand;
import com.jacktheogre.lightswitch.commands.TurnOnCommand;
import com.jacktheogre.lightswitch.commands.WallthroughCommand;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Button;
import com.jacktheogre.lightswitch.tools.Assets;
import com.jacktheogre.lightswitch.tools.ColorLoader;

/**
 * Created by luna on 20.10.16.
 */
public class Hud implements Disposable{

    public Stage stage;
    private Viewport viewport;

    private Label timeLabel;
    private PlayScreen screen;
    private Sprite energyBar, fill, timer;
    private Sprite[] shards;
    private Button lightButton, wallthroughButton;
    private float energyBarScale = 1f;

    private Touchpad touchpad;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;

    private boolean[] shardsCollected;

    public Hud(PlayScreen screen) {
        this.screen = screen;
        viewport = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT);
        stage = new Stage(viewport, screen.getGame().batch);

        shards = new Sprite[LevelManager.getAmountOfShardsOnLevel()];
        shardsCollected = new boolean[shards.length];
        for (int i = 0; i < shardsCollected.length; i++) {
            shardsCollected[i] = false;
        }

        initializeGraphicElements();

        timeLabel = new Label(Constants.PLAYTIME+"", new Label.LabelStyle(Assets.getAssetLoader().font, ColorLoader.colorMap.get("TIMER_LABEL_COLOR")));
        timeLabel.setFontScale(0.5f);
        timeLabel.setBounds(timer.getX() + 3, timer.getY() + 2, timer.getWidth() - 6, timer.getHeight() - 4);
//        timeLabel.setWrap(true);
        timeLabel.setAlignment(Align.center);

        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            initializeTouchpad();
        }
    }

    private void initializeTouchpad() {
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", Assets.getAssetLoader().touchBg);
        //Set knob image
        touchpadSkin.add("touchKnob", Assets.getAssetLoader().touchKnob);
        //Create TouchPad Style
        touchpadStyle = new TouchpadStyle();
        touchpadStyle.background = touchpadSkin.getDrawable("touchBackground");
        touchpadStyle.knob = touchpadSkin.getDrawable("touchKnob");
        touchpad = new Touchpad(15, touchpadStyle);
        touchpad.setBounds(viewport.getScreenWidth() - 470, 70 , 500, 500);

        screen.getInputHandler().addActor(touchpad);
    }


    private void initializeGraphicElements() {
        initializeMoonShards();
        energyBar = new Sprite(Assets.getAssetLoader().scale);
        energyBar.setPosition(energyBar.getWidth() / 2 - 10, 40);
        energyBar.setScale(1f);
        fill = new Sprite(Assets.getAssetLoader().scale_fill);
        fill.setPosition(energyBar.getX() + 2, energyBar.getY() + 2);
        fill.setSize(energyBar.getWidth() - 4, energyBar.getHeight() - 4);
        fill.setOrigin(fill.getWidth() / 2, 0);
        timer = new Sprite(Assets.getAssetLoader().timer);
        timer.setPosition((viewport.getWorldWidth() - timer.getWidth()) / 2, viewport.getWorldHeight() - timer.getHeight());
        if(Gdx.app.getType() == Application.ApplicationType.Android ) {
            if(LightSwitch.isPlayingHuman()) {
                lightButton = new Button(Assets.getAssetLoader().light_button, Button.State.ACTIVE, screen) {
                    @Override
                    protected void actPress() {
                        if(!playScreen.getLighting().lightsOn())
                            playScreen.getCommandHandler().addCommandPlay(new TurnOnCommand(playScreen));
                    }

                    @Override
                    protected void actUnpress() {
                        if(playScreen.getLighting().lightsOn())
                            playScreen.getCommandHandler().addCommandPlay(new TurnOffCommand(playScreen));
                    }

                    @Override
                    public void update(float dt) {
                        if(playScreen.getEnergy() >= Constants.WASTE_ENERGY_PER_SWITCH) {
//                            setState(State.DISABLED);
                            enable();
                        }
                        // FIXME: 04.04.17 why is turning off
                        if(playScreen.getLighting().lightsOn() && !isPressed())
                            playScreen.getCommandHandler().addCommandPlay(new TurnOffCommand(playScreen));
                    }

                    @Override
                    public boolean press() {
                        if(pressed)
                            return false;
                        pressed = true;
                        if(disabled)
                            Gdx.app.log("Hud", "disabled is true");
                        if(!disabled) {
                            if(playScreen.getEnergy() < Constants.WASTE_ENERGY_PER_SWITCH)
                                return true;
                            setState(State.PRESSED);
                            actPress();
                        } else {
                            setState(State.DISABLED);
                        }
                        return true;
                    }

                    @Override
                    public void initGraphics(TextureRegion textureRegion) {
                        int width = textureRegion.getRegionWidth() / 2;
                        Array<TextureRegion> frames = new Array<TextureRegion>();

                        for (int i = 0; i < 2; i++) {
                            frames.add(new TextureRegion(textureRegion, i*width, 0, width, textureRegion.getRegionHeight()));
                        }
                        disabledTexture = frames.get(0);
                        activeTexture = frames.get(0);
                        focusedTexture = frames.get(0);
                        pressedTexture = frames.get(1);

                        this.setSize(width, this.getHeight());
                    }
                };
                energyBar.setPosition(energyBar.getX(), energyBar.getY() + lightButton.getHeight() / 2 );
                fill.setPosition(energyBar.getX() + 2, energyBar.getY() + 2);
                lightButton.setPosition(energyBar.getX(), energyBar.getY() - lightButton.getHeight() - 5);
            } else {
                wallthroughButton = new Button(Assets.getAssetLoader().light_button, Button.State.ACTIVE, screen) {
                    @Override
                    protected void actPress() {
                        playScreen.getCommandHandler().addCommandPlay(new WallthroughCommand(playScreen.getPlayer()));
                    }

                    @Override
                    protected void actUnpress() {
                        playScreen.getCommandHandler().addCommandPlay(new WallthroughCommand(playScreen.getPlayer()));
                    }

                    @Override
                    public void update(float dt) {
//                        if(playScreen.getLighting().lightsOn() && !isPressed())
//                            playScreen.getCommandHandler().addCommandPlay(new TurnOffCommand(playScreen));
                    }

                    @Override
                    public boolean press() {
                        if(pressed)
                            return false;
                        pressed = true;
                        if(!disabled) {
//                            if(playScreen.getEnergy() < Constants.WASTE_ENERGY_PER_SWITCH)
//                                return false;
                            setState(State.PRESSED);
                            actPress();
                            return true;
                        } else {
                            setState(State.DISABLED);
                            return false;
                        }
                    }

                    @Override
                    public void initGraphics(TextureRegion textureRegion) {
                        int width = textureRegion.getRegionWidth() / 2;
                        Array<TextureRegion> frames = new Array<TextureRegion>();

                        for (int i = 0; i < 2; i++) {
                            frames.add(new TextureRegion(textureRegion, i*width, 0, width, textureRegion.getRegionHeight()));
                        }
                        disabledTexture = frames.get(0);
                        activeTexture = frames.get(0);
                        focusedTexture = frames.get(0);
                        pressedTexture = frames.get(1);

                        this.setSize(width, this.getHeight());
                    }
                };
                energyBar.setPosition(energyBar.getX(), energyBar.getY() + wallthroughButton.getHeight() / 2 );
                fill.setPosition(energyBar.getX() + 2, energyBar.getY() + 2);
                wallthroughButton.setPosition(energyBar.getX(), energyBar.getY() - wallthroughButton.getHeight() - 5);
            }
        }
    }

    public Touchpad getTouchpad() {
        return touchpad;
    }

    private void initializeMoonShards() {
        float scale = 2f;
        Vector2 leftBottomCorner = new Vector2(viewport.getWorldWidth() - 50, viewport.getWorldHeight() - 50);
        if(shards.length == 3) {
            for (int i = 0; i < 3; i++) {
                shards[i] = new Sprite(Assets.getAssetLoader().threeShards[i]);
            }
            shards[0].setPosition(leftBottomCorner.x - 2*scale, leftBottomCorner.y + 2*scale);
            shards[1].setPosition(leftBottomCorner.x + 4*scale, leftBottomCorner.y - 4*scale);
            shards[2].setPosition(leftBottomCorner.x + 7*scale, leftBottomCorner.y + 5*scale);
        } else if(shards.length == 2) {
            for (int i = 0; i < 2; i++) {
                shards[i] = new Sprite(Assets.getAssetLoader().twoShards[i]);
            }
            shards[0].setPosition(leftBottomCorner.x + 0*scale, leftBottomCorner.y + 0*scale);
            shards[1].setPosition(leftBottomCorner.x + 0.5f*scale, leftBottomCorner.y - 1.5f*scale);
        }

        for (Sprite shard :shards) {
            shard.setOriginCenter();
            shard.scale(scale);
        }
    }

    private void renderMoonShards() {
        if(!screen.getGame().batch.isDrawing())
            screen.getGame().batch.begin();
        for (int i = 0; i < shards.length; i++) {
            Color c = shards[i].getColor();
            if(shardsCollected[i]) {
                shards[i].setColor(c.r, c.g, c.b, 0.7f);
                shards[i].draw(screen.getGame().batch);
            } else {
//                threeShards[i].setColor(c.r, c.g, c.b, 0.2f);
//                screen.getGame().batch.setColor(color.r, color.g, color.b, 0.2f);
//                threeShards[i].draw(screen.getGame().batch);
            }
        }
//        screen.getGame().batch.setColor(color);
    }

    public void render(float dt) {
        update(dt);
        screen.getGame().batch.enableBlending();
        screen.getGame().batch.begin();
        screen.getGame().batch.setProjectionMatrix(stage.getCamera().combined);
//        if(LightSwitch.isPlayingHuman()) {
            fill.draw(screen.getGame().batch);
            energyBar.draw(screen.getGame().batch);
//        }
        timer.draw(screen.getGame().batch);
        timeLabel.draw(screen.getGame().batch, 1f);
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            //draws touchPad
            screen.getInputHandler().act(dt);
            screen.getGame().batch.setProjectionMatrix(screen.getInputHandler().getCamera().combined);
            screen.getGame().batch.enableBlending();
            touchpad.draw(screen.getGame().batch, 0.7f);
            Color color = screen.getGame().batch.getColor();
            screen.getGame().batch.setColor(color.r, color.g, color.b, 1);
            screen.renderButtons(stage.getCamera());
        }
        renderMoonShards();
        if(screen.getGame().batch.isDrawing())
            screen.getGame().batch.end();

        /*ShapeRenderer sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        sr.setProjectionMatrix(stage.getCamera().combined);
        sr.begin();
        sr.rect(arrowsRect.x, arrowsRect.y,
                arrowsRect.getWidth(), arrowsRect.getHeight());
        sr.end();*/
    }

    public Camera getCamera() {
        return stage.getCamera();
    }

    public void update(float dt) {
        timeLabel.setText(((int)(Constants.PLAYTIME - screen.getRunTime()))+"");
        interpolateScale(dt);
        fill.setScale(energyBar.getScaleX(), energyBarScale);
    }

    private void interpolateScale(float dt) {
        float lerp = 7f;
        energyBarScale += ((screen.getEnergy() / 100f) - energyBarScale) * dt * lerp * energyBar.getScaleY();
    }

    public void dispose() {
        //nothing to dispose
    }

    public void collectShard(int number) {
        shardsCollected[number] = true;
    }

    public int getAmountOfCollectedShards() {
        int count = 0;
        for (int i = 0; i < shardsCollected.length; i++) {
            if(shardsCollected[i])
                count++;
        }
        return count;
    }

    public String collectedShardsAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < shardsCollected.length; i++) {
            stringBuilder.append(shardsCollected[i] ? "1":"0");
        }
        return stringBuilder.toString();
    }

    public Button getWallthroughButton() {
        return wallthroughButton;
    }

    public Button getLightButton() {
        if(lightButton == null)
            Gdx.app.log("", "lightbutton is null");
        return lightButton;
    }
}
