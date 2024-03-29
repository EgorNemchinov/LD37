package com.jacktheogre.lightswitch.tools.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.LevelChoosingScreen;
import com.jacktheogre.lightswitch.screens.MainMenuScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;

/**
 * Created by luna on 11.12.16.
 */
public class GenerateInputHandler implements InputProcessor{

    private GeneratingScreen screen;

    public GenerateInputHandler(GeneratingScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ENTER) {
            screen.getStart().press();
        }
        if(keycode == Input.Keys.R) {
            screen.getRedo().press();
        }
        if(keycode == Input.Keys.U) {
            screen.getUndo().press();
        }
        if(keycode == Input.Keys.T) {
            screen.getTeleportButton().press();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.ENTER) {
            if(screen.getStart().unpress()) {
                screen.getGame().setScreen(new PlayScreen(screen));
            }
        }
        if(keycode == Input.Keys.R) {
            if(screen.getRedo().unpress())
                screen.getCommandHandler().redo();
        }
        if(keycode == Input.Keys.U) {
            if(screen.getUndo().unpress())
                screen.getCommandHandler().undo();
        }
        if(keycode == Input.Keys.T) {
            if(screen.getTeleportButton().unpress())
                screen.setState(GeneratingScreen.State.SETTING_TELEPORT);
        }
        if(keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE) {
            screen.getGame().setScreen(new LevelChoosingScreen(screen.getGame(), LevelManager.getLevelNum()));
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
        Vector3 point = screen.getUiCamera().unproject(screenTouch.cpy(), screen.getGamePort().getScreenX(), screen.getGamePort().getScreenY(),
                screen.getGamePort().getScreenWidth(), screen.getGamePort().getScreenHeight());
        Vector3 worldPoint = screen.getGameCam().unproject(screenTouch.cpy(), screen.getGamePort().getScreenX(), screen.getGamePort().getScreenY(),
                screen.getGamePort().getScreenWidth(), screen.getGamePort().getScreenHeight());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.touchDownButtons(point.x, point.y, pointer);
        screen.setSelectedNode(LevelManager.graph.getNodeByXY((int) worldPoint.x, (int)worldPoint.y));
        screen.addTeleport();
        screen.addTrap();
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getUiCamera().unproject(screenTouch.cpy(), screen.getGamePort().getScreenX(), screen.getGamePort().getScreenY(),
                screen.getGamePort().getScreenWidth(), screen.getGamePort().getScreenHeight());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.touchUpButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getUiCamera().unproject(screenTouch.cpy(), screen.getGamePort().getScreenX(), screen.getGamePort().getScreenY(),
                screen.getGamePort().getScreenWidth(), screen.getGamePort().getScreenHeight());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.touchDraggedButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getUiCamera().unproject(screenTouch.cpy(), screen.getGamePort().getScreenX(), screen.getGamePort().getScreenY(),
                screen.getGamePort().getScreenWidth(), screen.getGamePort().getScreenHeight());
        Vector3 worldPoint = screen.getGameCam().unproject(screenTouch.cpy(), screen.getGamePort().getScreenX(), screen.getGamePort().getScreenY(),
                screen.getGamePort().getScreenWidth(), screen.getGamePort().getScreenHeight());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.mouseMovedButtons(point.x, point.y);
        screen.setSelectedNode(LevelManager.graph.getNodeByXY((int) worldPoint.x, (int)worldPoint.y));
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
