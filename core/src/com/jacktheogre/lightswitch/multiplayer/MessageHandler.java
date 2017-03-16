package com.jacktheogre.lightswitch.multiplayer;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.jacktheogre.lightswitch.commands.Command;
import com.jacktheogre.lightswitch.commands.CommandEnterpreter;
import com.jacktheogre.lightswitch.commands.CommandHandler;
import com.jacktheogre.lightswitch.commands.NullActorCommand;
import com.jacktheogre.lightswitch.commands.NullGlobalCommand;

/**
 * Created by luna on 12.02.17.
 */

public class MessageHandler {

    public static boolean getMessage(CommandHandler receiver) {
        String msg = BluetoothSingleton.getInstance().getBluetoothManager().getMessage();
        if(msg == null)
            return false;
        Command cmd = new NullActorCommand();
        if(msg.length() > 0)
            cmd = CommandEnterpreter.createCommandFromString(msg);
        if(ClassReflection.isInstance(NullActorCommand.class, cmd) || ClassReflection.isInstance(NullGlobalCommand.class, cmd)) {
            return false;
        } else {
            receiver.addCommand(cmd);
            return true;
        }
    }

    public static void sendMessage(String message) {
        BluetoothSingleton.getInstance().getBluetoothManager().sendMessage(message);
    }
}
