package com.jacktheogre.lightswitch.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jacktheogre.lightswitch.LightSwitch;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 2 * LightSwitch.WIDTH;
        config.height = 2 * LightSwitch.HEIGHT;
        config.title = "Somniphobia";
        new LwjglApplication(new LightSwitch(), config);
	}
}
