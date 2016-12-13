package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.jacktheogre.lightswitch.commands.MoveToCommand;
import com.jacktheogre.lightswitch.commands.StartMovingCommand;
import com.jacktheogre.lightswitch.commands.StopCommand;
import com.jacktheogre.lightswitch.commands.StopMovingCommand;
import com.jacktheogre.lightswitch.commands.TurnOffCommand;
import com.jacktheogre.lightswitch.commands.TurnOnCommand;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Actor;

/**
 * Created by luna on 10.12.16.
 */
public class PlayInputHandler implements InputProcessor {

    private PlayScreen screen;
    private float lastMakingPathTime = 0;

    public PlayInputHandler(PlayScreen playScreen) {
        this.screen = playScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.SPACE:
                screen.getCommandHandler().addCommand(new TurnOnCommand(screen));
                break;
            case Input.Keys.W:
            case Input.Keys.UP:
                screen.getCommandHandler().addCommand(new StartMovingCommand(Actor.Direction.UP));
                break;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                screen.getCommandHandler().addCommand(new StartMovingCommand(Actor.Direction.LEFT));
                break;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                screen.getCommandHandler().addCommand(new StartMovingCommand(Actor.Direction.DOWN));
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                screen.getCommandHandler().addCommand(new StartMovingCommand(Actor.Direction.RIGHT));
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO: 12.12.16 maybe undo last command with certain direction
        switch(keycode) {
            case Input.Keys.SPACE:
                screen.getCommandHandler().addCommand(new TurnOffCommand(screen));
                break;
            case Input.Keys.W:
            case Input.Keys.UP:
                screen.getCommandHandler().stopMoving(Actor.Direction.UP);
                break;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                screen.getCommandHandler().stopMoving(Actor.Direction.LEFT);
                break;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                screen.getCommandHandler().stopMoving(Actor.Direction.DOWN);
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                screen.getCommandHandler().stopMoving(Actor.Direction.RIGHT);
                break;
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
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.getCommandHandler().addCommand(new MoveToCommand(point.x, point.y));
        screen.setTouchPoint((int)point.x, (int)point.y);
        lastMakingPathTime = screen.getRunTime();
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(screen.getRunTime() - lastMakingPathTime > 0.2f) {
            return touchDown(screenX, screenY, pointer, 0);
        } else
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
