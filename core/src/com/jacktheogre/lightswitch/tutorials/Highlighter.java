package com.jacktheogre.lightswitch.tutorials;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.jacktheogre.lightswitch.screens.GameScreen;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 14.04.17.
 */

public class Highlighter {
    private final float DARKENING_OPACITY = 0.7f;
    private final float OPACITY_PER_SEC = DARKENING_OPACITY*2;
    private float currentOpacity = 0;
    private GameScreen screen;

    enum State {WAITING, ACTIVE, FINISHING}
    private State state;
    protected float showTime = -1f;
    protected float timeSinceBegin;

    protected Label label;
    private String targetText;
    private int pointer = 0;
    private float timeSinceLastLetter = 0;
    private boolean textFinished = false;

    // TODO: 14.04.17 also draw button "OK"

    public Highlighter(GameScreen screen) {
        this.screen = screen;
        state = State.WAITING;
        timeSinceBegin = 0;
        targetText = "";
        initializeLabel();
    }

    public Highlighter(GameScreen screen, float showTime, String targetText) {
        this(screen);
        this.showTime = showTime;
        this.targetText = targetText;
    }

    protected void initializeLabel() {
        label = new Label("", new Label.LabelStyle(Assets.getAssetLoader().font, Color.WHITE));
        label.setWrap(false);
        label.setWidth(screen.getGamePort().getWorldWidth() / 3);
        label.setFontScale(0.3f);
        label.setText("");
    }

    private boolean nextLetter() {
        if(pointer >= targetText.length())
            return false;
        else {
            label.setText(label.getText().toString() + targetText.charAt(pointer));
//            label.setText(label.getText().append(targetText.charAt(pointer)).toString());
            pointer++;
            timeSinceLastLetter = 0;
        }
        return true;
    }

    public void render(float dt) {
        if(showTime != -1f) {
            timeSinceBegin += dt;
        }
        switch (state) {
            case ACTIVE:
                darkenScreen();
                if(currentOpacity < DARKENING_OPACITY)
                    currentOpacity += OPACITY_PER_SEC*dt;
                if(currentOpacity > DARKENING_OPACITY)
                    currentOpacity = DARKENING_OPACITY;
                timeSinceLastLetter += dt;
                if(timeSinceLastLetter > 0.05f) {
                    if(!nextLetter())
                        textFinished = true; //mb +1 or 2 sec? meh, anyway gonna wait for button press.
                }
                renderAbove(dt);
                if(showTime != -1f && timeSinceBegin > showTime && textFinished) {
                    end();
                }
                break;
            case FINISHING:
                darkenScreen();
                currentOpacity -= OPACITY_PER_SEC*dt;
                if(currentOpacity <= 0) {
                    currentOpacity = 0;
                    state = State.WAITING;
                }
                renderAbove(dt);
                break;
            case WAITING:
            default:
        }
    }

    private void renderAbove(float dt) {
        boolean wasDrawing = screen.getSpriteBatch().isDrawing();
        if(wasDrawing)
            screen.getSpriteBatch().end();
        screen.getSpriteBatch().begin();
        Color color = screen.getSpriteBatch().getColor();
        screen.getSpriteBatch().setColor(color.r, color.g, color.b, calculateOpacity());
        renderItems(dt);
        label.draw(screen.getSpriteBatch(), 1f);
        screen.getSpriteBatch().setColor(color);
        screen.getSpriteBatch().end();
    }

    private float calculateOpacity() {
        float seconds = (System.currentTimeMillis() % 1000000) / 1000f;
        return (float) Math.abs(Math.sin(seconds));
    }

    protected void renderItems(float dt) {

    }

    private void darkenScreen() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        screen.getShapeRenderer().setProjectionMatrix(screen.getGameCam().combined);
        if(!screen.getShapeRenderer().isDrawing())
            screen.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        screen.getShapeRenderer().setColor(new Color(0, 0, 0, currentOpacity));
        screen.getFillGamePort().apply();
        int screenWidth = screen.getFillGamePort().getScreenWidth(), screenHeight = screen.getFillGamePort().getScreenHeight();
        screen.getShapeRenderer().rect(-screenWidth, -screenHeight,
                3*screenWidth, 2*screenHeight);
        screen.getShapeRenderer().end();
        screen.getGamePort().apply();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void begin() {
        if(state == State.FINISHING)
            Gdx.app.error("Highlighter", "Called begin before ending.");
        state = State.ACTIVE;
        timeSinceBegin = 0;
        timeSinceLastLetter = 0;
        textFinished = false;
        beginAction();
    }

    protected void beginAction() {

    }

    public void end() {
        if(state == State.WAITING)
            Gdx.app.error("Highlighter", "Called end before beginning.");
        state = State.FINISHING;
        finishAction();
    }

    protected void finishAction() {

    }
}
