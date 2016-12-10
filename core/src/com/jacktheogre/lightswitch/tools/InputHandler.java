package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.commands.MoveCommand;
import com.jacktheogre.lightswitch.commands.TurnOffCommand;
import com.jacktheogre.lightswitch.commands.TurnOnCommand;
import com.jacktheogre.lightswitch.screens.PlayScreen;

/**
 * Created by luna on 10.12.16.
 */
public class InputHandler implements InputProcessor {

    private PlayScreen screen;

    public InputHandler(PlayScreen playScreen) {
        this.screen = playScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.DPAD_UP) {
            screen.getCommandHandler().addCommand(new TurnOnCommand(screen));
        } if(keycode == Input.Keys.DPAD_DOWN) {
            screen.getCommandHandler().addCommand(new TurnOffCommand(screen));
        }

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
        Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.getCommandHandler().addCommand(new MoveCommand(point.x, point.y));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
