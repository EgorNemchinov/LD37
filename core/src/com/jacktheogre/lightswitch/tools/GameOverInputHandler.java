package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.jacktheogre.lightswitch.screens.GameOverScreen;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.MainMenuScreen;
import com.jacktheogre.lightswitch.sprites.Button;

/**
 * Created by luna on 14.12.16.
 */
public class GameOverInputHandler implements InputProcessor {

    private GameOverScreen screen;
    private Button enterButton;

    public GameOverInputHandler(GameOverScreen screen) {
        this.screen = screen;
        if(screen.getNext_level().getState() == Button.State.DISABLED)
            enterButton = screen.getReplay();
        else
            enterButton = screen.getNext_level();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ENTER) {
            enterButton.press();
        }
        if(keycode == Input.Keys.R) {
            screen.getReplay().press();
        }
        if(keycode == Input.Keys.N) {
            screen.getNext_level().press();
        }
        if(keycode == Input.Keys.BACKSPACE) {
            screen.getHome().press();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.ENTER) {
            enterButton.touchUp();
            if(enterButton == screen.getNext_level())
                Assets.getAssetLoader().nextLevel();
            screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
        }
        if(keycode == Input.Keys.R) {
            screen.getReplay().touchUp();
            screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
        }
        if(keycode == Input.Keys.N) {
            screen.getNext_level().touchUp();
            if(screen.getNext_level().getState() == Button.State.DISABLED) {
                Assets.getAssetLoader().nextLevel();
                screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
            }
        }
        if(keycode == Input.Keys.BACKSPACE) {
            screen.getHome().touchUp();
            screen.getGame().setScreen(new MainMenuScreen(screen.getGame()));
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        if(screen.getReplay().getBoundingRectangle().contains(point.x, point.y))
            screen.getReplay().press();
        if(screen.getNext_level().getBoundingRectangle().contains(point.x, point.y))
            screen.getNext_level().press();
        if(screen.getHome().getBoundingRectangle().contains(point.x, point.y))
            screen.getHome().press();
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        if(screen.getReplay().getBoundingRectangle().contains(point.x, point.y)) {
            screen.getReplay().touchUp();
            screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
        }
        if(screen.getNext_level().getBoundingRectangle().contains(point.x, point.y)) {
            screen.getNext_level().touchUp();
            if(screen.getNext_level().getState() != Button.State.DISABLED) {
                Assets.getAssetLoader().nextLevel();
                screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
            }
        }
        if(screen.getHome().getBoundingRectangle().contains(point.x, point.y)) {
            screen.getHome().touchUp();
            screen.getGame().setScreen(new MainMenuScreen(screen.getGame()));
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        if(screen.getReplay().getBoundingRectangle().contains(point.x, point.y))
            screen.getReplay().focused();
        else
            screen.getReplay().unfocused();
        if(screen.getNext_level().getBoundingRectangle().contains(point.x, point.y))
            screen.getNext_level().focused();
        else
            screen.getNext_level().unfocused();
        if(screen.getHome().getBoundingRectangle().contains(point.x, point.y))
            screen.getHome().focused();
        else
            screen.getHome().unfocused();
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
