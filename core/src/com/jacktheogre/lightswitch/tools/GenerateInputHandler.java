package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.ai.Node;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.MainMenuScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Button;

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
            if(screen.getStart().touchUp()) {
                screen.getGame().setScreen(new PlayScreen(screen));
            }
        }
        if(keycode == Input.Keys.R) {
            if(screen.getRedo().touchUp())
                screen.getCommandHandler().redo();
        }
        if(keycode == Input.Keys.U) {
            if(screen.getUndo().touchUp())
                screen.getCommandHandler().undo();
        }
        if(keycode == Input.Keys.T) {
            if(screen.getTeleportButton().touchUp())
                screen.setState(GeneratingScreen.State.SETTING_TELEPORT);
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO: 11.12.16 screenX instead of GDx.inout.getX()
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        if(screen.getUndo().getBoundingRectangle().contains(point.x, point.y)) {
            screen.getUndo().press();
        }
        if(screen.getRedo().getBoundingRectangle().contains(point.x, point.y))
            screen.getRedo().press();
        if(screen.getStart().getBoundingRectangle().contains(point.x, point.y))
            screen.getStart().press();
        if(screen.getTeleportButton().getBoundingRectangle().contains(point.x, point.y)) {
            if(screen.getTeleportButton().press())
                screen.setState(GeneratingScreen.State.SETTING_TELEPORT);
        }
        screen.setSelectedNode(LevelManager.graph.getNodeByXY((int) point.x, (int)point.y));
        screen.addTeleport();
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        if(screen.getUndo().isAutoUnpress() && screen.getUndo().getBoundingRectangle().contains(point.x, point.y))
            if(screen.getUndo().touchUp())
                screen.getCommandHandler().undo();
        if(screen.getRedo().isAutoUnpress() && screen.getRedo().getBoundingRectangle().contains(point.x, point.y))
            if(screen.getRedo().touchUp()) {
                screen.getCommandHandler().redo();
            }
        if(screen.getStart().isAutoUnpress() && screen.getStart().getBoundingRectangle().contains(point.x, point.y))
            if(screen.getStart().touchUp()) {
                screen.getGame().setScreen(new PlayScreen(screen));
            }
        if(screen.getTeleportButton().isAutoUnpress() && screen.getTeleportButton().getBoundingRectangle().contains(point.x, point.y)) {
            // TODO: 15.12.16 make it work
        }
//            if(screen.getTeleportButton().touchUp()) {
//                screen.setState(GeneratingScreen.State.SETTING_TELEPORT);
//            }
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
        if(screen.getUndo().getBoundingRectangle().contains(point.x, point.y))
            screen.getUndo().focused();
        else
            screen.getUndo().unfocused();
        if(screen.getRedo().getBoundingRectangle().contains(point.x, point.y))
            screen.getRedo().focused();
        else
            screen.getRedo().unfocused();
        if(screen.getStart().getBoundingRectangle().contains(point.x, point.y))
            screen.getStart().focused();
        else
            screen.getStart().unfocused();
        if(screen.getTeleportButton().getBoundingRectangle().contains(point.x, point.y)) {
            screen.getTeleportButton().focused();
        }
        else
            screen.getTeleportButton().unfocused();
        screen.setSelectedNode(LevelManager.graph.getNodeByXY((int) point.x, (int)point.y));
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
