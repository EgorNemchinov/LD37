package com.jacktheogre.lightswitch.tools.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.commands.StartMovingCommand;
import com.jacktheogre.lightswitch.commands.TurnOffCommand;
import com.jacktheogre.lightswitch.commands.TurnOnCommand;
import com.jacktheogre.lightswitch.commands.WallthroughCommand;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Monster;

/**
 * Created by luna on 10.12.16.
 */
public class PlayInputHandler extends Stage{

    private PlayScreen screen;
    private float lastMakingPathTime = 0;

    public PlayInputHandler(PlayScreen playScreen) {
        this.screen = playScreen;
    }

    public void update() {
        if(screen.getLighting().lightsOn() && !Gdx.input.isKeyPressed(Input.Keys.SPACE))
            screen.getCommandHandler().addCommand(new TurnOffCommand(screen));
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.SPACE:
                if(LightSwitch.isPlayingHuman())
                    screen.getCommandHandler().addCommand(new TurnOnCommand(screen));
                else {
                    screen.getCommandHandler().addCommand(new WallthroughCommand(screen.getPlayer()));
                }
                break;
            case Input.Keys.W:
            case Input.Keys.UP:
                screen.getCommandHandler().addCommand(new StartMovingCommand(GameActor.Direction.UP, screen.getPlayer()));
                screen.getPlayer().getGameActor().setMoving(true);
                break;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                screen.getCommandHandler().addCommand(new StartMovingCommand(GameActor.Direction.LEFT, screen.getPlayer()));
                screen.getPlayer().getGameActor().setMoving(true);
                break;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                screen.getCommandHandler().addCommand(new StartMovingCommand(GameActor.Direction.DOWN, screen.getPlayer()));
                screen.getPlayer().getGameActor().setMoving(true);
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                screen.getCommandHandler().addCommand(new StartMovingCommand(GameActor.Direction.RIGHT, screen.getPlayer()));
                screen.getPlayer().getGameActor().setMoving(true);
                break;
            case Input.Keys.BACK:
            case Input.Keys.BACKSPACE:
                screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch(keycode) {
            case Input.Keys.SPACE:
                if(LightSwitch.isPlayingHuman())
                    screen.getCommandHandler().addCommand(new TurnOffCommand(screen));
                break;
            case Input.Keys.W:
            case Input.Keys.UP:
                // TODO: 10.02.17 get command working
                screen.getCommandHandler().stopMoving(GameActor.Direction.UP);
//                screen.getCommandHandler().addCommand(new StopDirectionCommand(screen.getCommandHandler(), GameActor.Direction.UP));
                break;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                screen.getCommandHandler().stopMoving(GameActor.Direction.LEFT);
//                screen.getCommandHandler().addCommand(new StopDirectionCommand(screen.getCommandHandler(), GameActor.Direction.LEFT));
                break;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                screen.getCommandHandler().stopMoving(GameActor.Direction.DOWN);
//                screen.getCommandHandler().addCommand(new StopDirectionCommand(screen.getCommandHandler(), GameActor.Direction.DOWN));
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                screen.getCommandHandler().stopMoving(GameActor.Direction.RIGHT);
//                screen.getCommandHandler().addCommand(new StopDirectionCommand(screen.getCommandHandler(), GameActor.Direction.RIGHT));
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
        super.touchDown(screenX, screenY, pointer, button);
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 hudPoint = screen.getHud().getCamera().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.touchDownButtons(hudPoint.x, hudPoint.y, pointer);
        /*screen.getCommandHandler().addCommand(new MoveToCommand(point.x, point.y));
        screen.setTouchPoint((int)point.x, (int)point.y);
        lastMakingPathTime = screen.getRunTime();*/
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 hudPoint = screen.getHud().getCamera().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.touchUpButtons(hudPoint.x, hudPoint.y, pointer);
        return true;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        super.touchDragged(screenX, screenY, pointer);
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getHud().getCamera().unproject(screenTouch.cpy());
        screen.touchDraggedButtons(point.x, point.y, pointer);
       /* if(screen.getRunTime() - lastMakingPathTime > 0.2f) {
            return touchDown(screenX, screenY, pointer, 0);
        } else
            return false;*/
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        super.mouseMoved(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


}
