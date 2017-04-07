package com.jacktheogre.lightswitch.tools.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.screens.GameOverScreen;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.MainMenuScreen;
import com.jacktheogre.lightswitch.sprites.Button;
import com.jacktheogre.lightswitch.tools.Assets;

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
        if(keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE) {
            screen.getHome().press();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.ENTER) {
            enterButton.unpress();
            if(enterButton == screen.getNext_level()) {
                LevelManager.nextLevel();
                screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
            } else {
                screen.getGame().setScreen(new GeneratingScreen(screen.getGame(), screen.getObjects()));
            }
        }
        if(keycode == Input.Keys.R) {
            screen.getReplay().unpress();
            screen.getGame().setScreen(new GeneratingScreen(screen.getGame(), screen.getObjects()));
        }
        if(keycode == Input.Keys.N) {
            screen.getNext_level().unpress();
            if(screen.getNext_level().getState() == Button.State.DISABLED) {
                LevelManager.nextLevel();
                screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
            }
        }
        if(keycode == Input.Keys.BACKSPACE) {
            screen.getHome().unpress();
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
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screen.touchDownButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screen.touchUpButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screen.touchDraggedButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.mouseMovedButtons(point.x, point.y);
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
