package com.jacktheogre.lightswitch.commands;

import com.badlogic.gdx.Gdx;
import com.jacktheogre.lightswitch.ai.Node;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.objects.Trap;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.GameActor;

/**
 * Created by luna on 12.02.17.
 */

public class CommandEnterpreter {

    private static PlayScreen playScreen;

    public CommandEnterpreter(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

    public static Command createCommandFromString(String string) {
        Command cmd = new NullActorCommand();
        String[] partsOfString = string.split(" ");
        try {
            if(partsOfString[0].equals("mv")) {
                cmd = new MoveToCommand(Integer.parseInt(partsOfString[1]), Integer.parseInt(partsOfString[2]),
                        Integer.parseInt(partsOfString[3]), Integer.parseInt(partsOfString[4]),
                        Integer.parseInt(partsOfString[5])==1,
                        Integer.parseInt(partsOfString[6]) == 1 ? playScreen.getEnemyPlayer():playScreen.getPlayer() );
            } else if(partsOfString[0].equals("sp")) {

            } else if(partsOfString[0].equals("sm")) {
                cmd = new StartMovingCommand(GameActor.Direction.getDirectionByLetter(partsOfString[1]),
                        GameActor.Direction.getDirectionByLetter(partsOfString[2]),
                        Integer.parseInt(partsOfString[3])==1,
                        Integer.parseInt(partsOfString[4]) == 1 ? playScreen.getEnemyPlayer():playScreen.getPlayer() );
            } else if(partsOfString[0].equals("stop")) {

            } else if(partsOfString[0].equals("sd")) {
                cmd = new StartMovingCommand(GameActor.Direction.getDirectionByLetter(partsOfString[1]),
                        Integer.parseInt(partsOfString[2]) == 1 ? playScreen.getEnemyPlayer():playScreen.getPlayer() );
            } else if(partsOfString[0].equals("teleport")) {
                cmd = new TeleportCommand((Teleport)playScreen.getInteractiveObjectByIndex(Integer.parseInt(partsOfString[1])),
                        (Teleport) playScreen.getInteractiveObjectByIndex(Integer.parseInt(partsOfString[2])),
                Integer.parseInt(partsOfString[3]) == 1 ? playScreen.getEnemyPlayer():playScreen.getPlayer());
            } else if(partsOfString[0].equals("trapt")) {
                cmd = new TrapTriggerCommand((Trap)playScreen.getInteractiveObjectByIndex(Integer.parseInt(partsOfString[1])),
                        Integer.parseInt(partsOfString[2]) == 1 ? playScreen.getEnemyPlayer():playScreen.getPlayer());
            } else if(partsOfString[0].equals("ton")) {
                cmd = new TurnOnCommand(playScreen, Integer.parseInt(partsOfString[1]) == 1);
            } else if(partsOfString[0].equals("toff")) {
                cmd = new TurnOffCommand(playScreen, Integer.parseInt(partsOfString[1]) == 1);
            }
        } catch (Exception e) {
            Gdx.app.log("CommandEnterprener", "Error parsing string into command.");
            return new NullActorCommand();
        }

        if(cmd != null) {

            return cmd;
        } else {
            return new NullActorCommand();
        }
    }

}
