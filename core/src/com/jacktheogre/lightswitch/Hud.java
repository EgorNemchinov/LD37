package com.jacktheogre.lightswitch;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.commands.TurnOffCommand;
import com.jacktheogre.lightswitch.commands.TurnOnCommand;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Button;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 20.10.16.
 */
public class Hud implements Disposable{

    public Stage stage;
    private Viewport viewport;

    private Label timeLabel;
    private PlayScreen screen;
    private Sprite energyBar, fill, timer;
    private Button lightButton;
    private float energyBarScale = 1f;

    private Touchpad touchpad;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;


    public Hud(PlayScreen screen) {
        this.screen = screen;
        viewport = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT);
        stage = new Stage(viewport, screen.getGame().batch);

        initializeGraphicElements();

        timeLabel = new Label(Constants.PLAYTIME+"", new Label.LabelStyle(Assets.getAssetLoader().font, new Color(0xb6/255F, 0XFf/255f, 0xcb/255f, 1f)));
        timeLabel.setFontScale(0.6f);
        timeLabel.setBounds(timer.getX() + 5, timer.getY() + 2, timer.getWidth() - 6, timer.getHeight() - 4);
        timeLabel.setWrap(true);
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
        touchpad.setBounds(viewport.getScreenWidth() - 420, 20 , 400, 400);

        screen.getInputHandler().addActor(touchpad);
    }

    private void initializeGraphicElements() {
        energyBar = new Sprite(Assets.getAssetLoader().scale);
        energyBar.setPosition(energyBar.getWidth() / 2, 40);
        energyBar.setScale(1f);
        fill = new Sprite(Assets.getAssetLoader().scale_fill);
        fill.setPosition(energyBar.getX() + 2, energyBar.getY() + 2);
        fill.setSize(energyBar.getWidth() - 4, energyBar.getHeight() - 4);
        fill.setOrigin(fill.getWidth() / 2, 0);
        timer = new Sprite(Assets.getAssetLoader().timer);
        timer.setPosition((viewport.getWorldWidth() - timer.getWidth()) / 2, viewport.getWorldHeight() - timer.getHeight());
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            lightButton = new Button(Assets.getAssetLoader().light_button, Button.State.ACTIVE, screen) {
                @Override
                protected void actPress() {
                    if(!playScreen.getLighting().lightsOn())
                        playScreen.getCommandHandler().addCommand(new TurnOnCommand(playScreen));
                }

                @Override
                protected void actUnpress() {
                    if(playScreen.getLighting().lightsOn())
                        playScreen.getCommandHandler().addCommand(new TurnOffCommand(playScreen));
                }

                @Override
                public boolean press() {
                    if(pressed)
                        return false;
                    pressed = true;
                    if(!disabled) {
                        if(playScreen.getEnergy() < Constants.WASTE_ENERGY_PER_SWITCH)
                            return false;
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
            energyBar.setPosition(energyBar.getX(), energyBar.getY() + lightButton.getHeight() / 2 );
            fill.setPosition(energyBar.getX() + 2, energyBar.getY() + 2);
            lightButton.setPosition(energyBar.getX(), energyBar.getY() - lightButton.getHeight() - 5);
        }
    }

    public Touchpad getTouchpad() {
        return touchpad;
    }

    public void render(float dt) {
        update(dt);
        screen.getGame().batch.begin();
        screen.getGame().batch.setProjectionMatrix(stage.getCamera().combined);
        fill.draw(screen.getGame().batch);
        energyBar.draw(screen.getGame().batch);
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
        }
        screen.renderButtons(stage.getCamera());
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

    public Button getLightButton() {
        return lightButton;
    }
}
