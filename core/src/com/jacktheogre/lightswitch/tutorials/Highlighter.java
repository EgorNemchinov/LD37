package com.jacktheogre.lightswitch.tutorials;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.jacktheogre.lightswitch.screens.GameScreen;
import com.jacktheogre.lightswitch.sprites.Button;
import com.jacktheogre.lightswitch.tools.Assets;

import java.util.ArrayList;

/**
 * Created by luna on 14.04.17.
 */

public class Highlighter implements InputProcessor{
    private final float DARKENING_OPACITY = 0.7f;
    private final float OPACITY_PER_SEC = DARKENING_OPACITY*2;
    private float currentOpacity = 0;
    private GameScreen screen;

    public enum State {WAITING, ACTIVE, FINISHING}
    private State state;
    protected float showTime = -1f;
    protected float timeSinceBegin;

    protected Label label;
    private String targetText;
    private int pointer = 0;
    private float timeSinceLastLetter = 0;
    private boolean textFinished = false;

    private ArrayList<Button> workingButtons;

    private InputProcessor inputProcessor;

    protected Camera camera;

    protected Button okButton;

    // TODO: 14.04.17 also draw button "OK"

    public Highlighter(GameScreen screen) {
        this.screen = screen;
        state = State.WAITING;
        timeSinceBegin = 0;
        targetText = "";
        okButton = new Button(Assets.getAssetLoader().ok_button, Button.State.ACTIVE) {
            @Override
            protected void actUnpress() {
                end();
            }
        };
        okButton.setScale(1.5f);
        okButton.setPosition(screen.getGamePort().getWorldWidth() / 2 - okButton.getBoundingRectangle().getWidth() / 2, 0);
        workingButtons = new ArrayList<Button>();
        workingButtons.add(okButton);
        initializeLabel();
        onCreate();
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

    protected void onCreate() {

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

    protected void update(float dt) {

    }

    public void render(float dt) {
        update(dt);
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
        if(!screen.getSpriteBatch().isBlendingEnabled())
            screen.getSpriteBatch().enableBlending();
        screen.getSpriteBatch().setProjectionMatrix(camera == null ? screen.getGameCam().combined : camera.combined);
        screen.getSpriteBatch().begin();
        Color color = screen.getSpriteBatch().getColor();
        screen.getSpriteBatch().setColor(color.r, color.g, color.b, calculateOpacity());
        renderItems(dt);
        label.draw(screen.getSpriteBatch(), 1f);
        screen.getSpriteBatch().setColor(color);
        okButton.draw(screen.getSpriteBatch());
        screen.getSpriteBatch().end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    protected float calculateOpacity() {
        if(state == State.WAITING)
            return 1f;
        return (float) Math.abs(Math.cos(timeSinceBegin));
    }

    protected void renderItems(float dt) {

    }

    protected void renderButtons() {
        for (int i = 0; i < workingButtons.size(); i++) {
            workingButtons.get(i).draw(screen.getSpriteBatch());
        }
    }

    private void darkenScreen() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        screen.getShapeRenderer().setProjectionMatrix(camera == null ? screen.getGameCam().combined : camera.combined);
        if(!screen.getShapeRenderer().isDrawing())
            screen.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        screen.getShapeRenderer().setColor(new Color(0, 0, 0, currentOpacity));
        screen.getFillGamePort().apply();
        int screenWidth = screen.getFillGamePort().getScreenWidth(), screenHeight = screen.getFillGamePort().getScreenHeight();
        screen.getShapeRenderer().rect(-screenWidth, -screenHeight,
                3*screenWidth, 2*screenHeight);
        screen.getShapeRenderer().end();
        screen.getGamePort().apply();
    }

    public void begin() {
        if(state == State.FINISHING)
            Gdx.app.error("Highlighter", "Called begin before ending.");
        state = State.ACTIVE;
        timeSinceBegin = 0;
        timeSinceLastLetter = 0;
        textFinished = false;
        inputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(this);
        beginAction();
    }

    protected void beginAction() {
    }

    public void end() {
        if(state == State.WAITING)
            Gdx.app.error("Highlighter", "Called end before beginning.");
        state = State.FINISHING;
        screen.setInputHAndling(true);
        Gdx.input.setInputProcessor(inputProcessor);
        finishAction();
    }

    protected void finishAction() {

    }

    protected void addButton(Button button) {
        workingButtons.add(button);
    }

    public State getState() {
        return state;
    }

    private Vector3 calculatePosition(int screenX, int screenY) {
        Camera cam = camera == null ? screen.getGameCam() : camera;
        return cam.unproject(new Vector3(screenX, screenY, 0));
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.SPACE) {
            okButton.press();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.SPACE) {
            okButton.unpress();
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldPoint = calculatePosition(screenX, screenY);
        for (int i = 0; i < workingButtons.size(); i++) {
            Button currentButton = workingButtons.get(i);
            if(currentButton.getBoundingRectangle().contains(worldPoint.x, worldPoint.y)) {
                if(currentButton.press())
                    currentButton.setPointer(pointer);
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 worldPoint = calculatePosition(screenX, screenY);
        for (int i = 0; i < workingButtons.size(); i++) {
            Button currentButton = workingButtons.get(i);
            if(currentButton.getBoundingRectangle().contains(worldPoint.x, worldPoint.y)
                    && currentButton.getPointer() == pointer && currentButton.isAutoUnpress()) {
                currentButton.unpress();
            }
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 worldPoint = calculatePosition(screenX, screenY);
        for (int i = 0; i < workingButtons.size(); i++) {
            Button currentButton = workingButtons.get(i);
            if(currentButton.getPointer() == pointer) {
                if(!currentButton.getBoundingRectangle().contains(worldPoint.x, worldPoint.y)
                        && currentButton.isAutoUnpress() && currentButton.isPressed())
                    currentButton.undoPressing();
                else if(currentButton.getBoundingRectangle().contains(worldPoint.x, worldPoint.y)
                        && currentButton.isAutoUnpress() && !currentButton.isPressed())
                    currentButton.redoPressing();
            }
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 worldPoint = calculatePosition(screenX, screenY);
        for (int i = 0; i < workingButtons.size(); i++) {
            Button currentButton = workingButtons.get(i);
            if(currentButton.getBoundingRectangle().contains(worldPoint.x, worldPoint.y)) {
                currentButton.focused();
            } else {
                currentButton.unfocused();
            }
        }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
