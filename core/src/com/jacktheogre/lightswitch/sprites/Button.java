package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;

/**
 * Created by luna on 13.12.16.
 */
public class Button extends Sprite {
    public enum State {ACTIVE, FOCUSED, PRESSED, DISABLED}
    private State state;
    private TextureRegion textureReg;
    private TextureRegion activeTexture;
    private TextureRegion focusedTexture;
    private TextureRegion pressedTexture;
    private TextureRegion disabledTexture;
    private boolean singleTexture;

    private boolean disabled = false;
    private boolean pressed = false;
    private boolean focused = false;
    private boolean autoUnpress = true;

    private GeneratingScreen screen;

    public Button(TextureRegion texture) {
        super(new TextureRegion(texture, 0, 0, texture.getRegionWidth() / 4, texture.getRegionHeight()));
        this.textureReg = texture;
        initGraphics(textureReg);
        setState(State.DISABLED);
    }

    public Button(TextureRegion textureRegion, State state) {
        this(textureRegion);
        this.state = state;
    }

    public Button(TextureRegion textureRegion, State state, boolean oneTexture) {
        super(new TextureRegion(textureRegion, 0, 0, oneTexture? textureRegion.getRegionWidth() : textureRegion.getRegionWidth() / 4, textureRegion.getRegionHeight()));
        this.textureReg = textureRegion;
        this.singleTexture = oneTexture;
        initGraphics(textureReg);
        this.state = state;
    }

    public void setScreen(GeneratingScreen screen) {
        this.screen = screen;
    }

    public void initGraphics(TextureRegion textureRegion) {
        int width = textureRegion.getRegionWidth() / 4;
        Array<TextureRegion> frames = new Array<TextureRegion>();

        if(!singleTexture) {

            for (int i = 0; i < 4; i++) {
                frames.add(new TextureRegion(textureRegion, i*width, 0, width, textureRegion.getRegionHeight()));
            }
            disabledTexture = frames.get(0);
            activeTexture = frames.get(1);
            pressedTexture = frames.get(2);
            focusedTexture = frames.get(3);
        } else {
            disabledTexture = textureReg;
            activeTexture = textureReg;
            pressedTexture = textureReg;
            focusedTexture = textureReg;
        }
    }

    public TextureRegion getFrame() {
        TextureRegion region;

        switch (state) {
            case ACTIVE:
                region = activeTexture;
                break;
            case FOCUSED:
                region = focusedTexture;
                break;
            case PRESSED:
                region = pressedTexture;
                break;
            case DISABLED:
                region = disabledTexture;
                break;
            default:
                region = disabledTexture;
                break;
        }
        return region;
    }

    public void setAutoUnpress(boolean autoUnpress) {
        this.autoUnpress = autoUnpress;
    }

    public boolean isAutoUnpress() {
        return autoUnpress;
    }

    public boolean press(){
        if(pressed)
            return false;
        pressed = true;
        if(!disabled) {
            setState(State.PRESSED);
            return true;
        } else {
            setState(State.DISABLED);
            return false;
        }
    }

    public boolean touchUp() {

        boolean wasPressed = pressed;
        pressed = false;
        if(!disabled) {
            if(focused) {
                setState(State.FOCUSED);
            }
            else {
                setState(State.ACTIVE);
            }
        } else {
            setState(State.DISABLED);
        }
        return wasPressed;
    }

    public void disable() {
        disabled = true;
        setState(State.DISABLED);
    }

    public void enable() {
        disabled = false;
        setState(State.ACTIVE);
    }

    public void focused() {
        focused = true;
        if(!disabled) {
            if(!pressed)
                setState(State.FOCUSED);
            else
                setState(State.PRESSED);
        }
        else {
            setState(State.DISABLED);
        }
    }

    public void unfocused() {
        if(pressed)
            touchUp();
        if(!focused)
            return;
        focused = false;
        if(!disabled) {
            setState(State.ACTIVE);
        }
        else {
            setState(State.DISABLED);
        }
    }

    public boolean isPressed() {
        return pressed;
    }

    @Override
    public void draw(Batch batch) {
        TextureRegion region = getFrame();
        batch.draw(region, getX(), getY(), getBoundingRectangle().getWidth(), getBoundingRectangle().getHeight());
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if(!disabled)
            this.state = state;
        else
            this.state = State.DISABLED;
    }
}
