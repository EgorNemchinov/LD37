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
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            screen.getGame().setScreen(new PlayScreen(screen));
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO: 11.12.16 screenX instead of GDx.inout.getX()
        Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        if(screen.getUndo().getBoundingRectangle().contains(point.x, point.y))
            screen.getUndo().press();
        if(screen.getRedo().getBoundingRectangle().contains(point.x, point.y))
            screen.getRedo().press();
        if(screen.getStart().getBoundingRectangle().contains(point.x, point.y))
            screen.getStart().press();
        if(screen.getTeleportButton().getBoundingRectangle().contains(point.x, point.y))
            screen.getTeleportButton().press();
        screen.setSelectedNode(LevelManager.graph.getNodeByXY((int) point.x, (int)point.y));
        screen.addTeleport();
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        if(screen.getUndo().getBoundingRectangle().contains(point.x, point.y))
            screen.getUndo().touchUp();
        if(screen.getRedo().getBoundingRectangle().contains(point.x, point.y))
            screen.getRedo().touchUp();
        if(screen.getStart().getBoundingRectangle().contains(point.x, point.y))
            screen.getStart().touchUp();
        if(screen.getTeleportButton().getBoundingRectangle().contains(point.x, point.y))
            screen.getTeleportButton().touchUp();
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
        if(screen.getTeleportButton().getBoundingRectangle().contains(point.x, point.y))
            screen.getTeleportButton().focused();
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
