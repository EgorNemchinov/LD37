package com.jacktheogre.lightswitch;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.tools.Assets;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new LightSwitch(), config);
	}
}
