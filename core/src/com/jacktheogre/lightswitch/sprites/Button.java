package com.jacktheogre.lightswitch.sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.screens.GameScreen;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;

/**
 * Created by luna on 13.12.16.
 */
public class Button extends Sprite {
    public enum State {ACTIVE, FOCUSED, PRESSED, DISABLED}
    protected State state;
    protected TextureRegion textureReg;
    protected TextureRegion activeTexture;
    protected TextureRegion focusedTexture;
    protected TextureRegion pressedTexture;
    protected TextureRegion disabledTexture;
    protected boolean singleTexture;

    protected boolean disabled = false;
    protected boolean pressed = false;
    protected boolean focused = false;
    protected boolean autoUnpress = true;

    protected int pointer;

    // TODO: 01.02.17 mb only gamescreen is needed? delete playscreen and generating
    public GeneratingScreen generatingScreen;
    public PlayScreen playScreen;
    public GameScreen screen;

    public Button(TextureRegion texture) {
        super(new TextureRegion(texture, 0, 0, texture.getRegionWidth() / 4, texture.getRegionHeight()));
        this.textureReg = texture;
        initGraphics(textureReg);
        setState(State.DISABLED);
    }

    public Button(TextureRegion textureRegion, State state) {
        this(textureRegion);
        this.state = state;
        if(state == State.DISABLED)
            disabled = true;
        this.setOrigin(0, 0);
    }

    public Button(TextureRegion textureReg, State state, GameScreen screen) {
        this(textureReg, state);
        this.screen = screen;
    }

    public Button(TextureRegion textureReg, State state, GeneratingScreen generatingScreen) {
        this(textureReg, state);
        this.generatingScreen = generatingScreen;
    }

    public Button(TextureRegion textureReg, State state, PlayScreen playScreen) {
        this(textureReg, state);
        this.playScreen = playScreen;
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
            focusedTexture = frames.get(2);
            pressedTexture = frames.get(3);
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

    public void update(float dt) {

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
            actPress();
            return true;
        } else {
            setState(State.DISABLED);
            return false;
        }
    }

    //used when button is pressed and then touch dragged outside it's bounds
    public boolean undoPressing() {
        if(disabled)
            return false;
        else {
            pressed = false;
            setState(State.ACTIVE);
            return true;
        }
    }

    //when it's dragged back
    public boolean redoPressing() {
        if(disabled)
            return false;
        else {
            pressed = true;
            setState(State.PRESSED);
            return true;
        }
    }

    public boolean unpress() {
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
        if(wasPressed)
            actUnpress();
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
            unpress();
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

    protected void actPress() {}
    protected void actUnpress() {}

    public boolean isPressed() {
        return pressed;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
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
