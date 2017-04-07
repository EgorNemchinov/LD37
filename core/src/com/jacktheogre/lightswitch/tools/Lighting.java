package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.commands.TurnOffCommand;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.PlayScreen;

import java.util.Random;

import box2dLight.PointLight;
import box2dLight.RayHandler;

/**
 * Created by luna on 24.10.16.
 */
public class Lighting {

    private final Color[] COLORS = {
        new Color(0xed/255f, 0x8a/255f, 0x00/255f, 0.6f), new Color(0xed/255f, 0x8a/255f, 0x00/255f, 0.6f)
    };
    private final Color AMBIENT_HUMAN = new Color(0.05f, 0.15f, 0.05f, 0.20f);
    private final Color AMBIENT_MONSTER = new Color(0.4f, 0.15f, 0.1f, 0.35f);
    private final Color LIGHT_HUMAN= new Color(0xb6/255f, 0xFF/255f, 0xDB/255f, 0.8f);
    private final Color LIGHT_MONSTER = new Color(200/255f, 0/255f, 100/255f, 0.6f);

    //box2dlights
    private RayHandler rayHandler;
    private Array<PointLight> pointLights;
    private PointLight actorLight;
    private GeneratingScreen screen;
    private PlayScreen playScreen;
    private Filter aboveLightFilter, actorLightFilter;
    private boolean lightsOn = true;

    public Lighting(GeneratingScreen screen) {
        this.screen = screen;
        rayHandler = new RayHandler(screen.getWorld());
        rayHandler.setAmbientLight(LightSwitch.isPlayingHuman() ? AMBIENT_HUMAN : AMBIENT_MONSTER);
        rayHandler.setBlurNum(3);
        pointLights = new Array<PointLight>();

        actorLight = new PointLight(rayHandler, Constants.LIGHT_RAYS, LightSwitch.isPlayingHuman()? LIGHT_HUMAN : LIGHT_MONSTER,
                0.6f*Constants.LIGHT_DISTANCE , 550, 100);

        actorLightFilter = new Filter();
        actorLightFilter.categoryBits = Constants.LIGHT_BIT;
        actorLightFilter.maskBits = Constants.WALLS_BIT | Constants.OBJECT_BIT | Constants.MONSTER_BIT ;
        transformActorLight(actorLight);

        aboveLightFilter = new Filter();
        aboveLightFilter.categoryBits = Constants.LIGHT_BIT;
        aboveLightFilter.maskBits = 0; //light is above everything. player can see all of it

        for (PointLight light : pointLights) {
            transformAboveLight(light);
        }
    }

    private void transformAboveLight(PointLight light) {
        light.setContactFilter(aboveLightFilter);
        light.setStaticLight(true);
        light.setSoftnessLength(0);
    }

    private void transformActorLight(PointLight light) {
        light.setContactFilter(actorLightFilter);
        light.setStaticLight(false);
        light.setSoftnessLength(0);
    }

    public void setPlayScreen(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

    public void render(float dt) {
        if(lightsOn && playScreen != null) {
            if(!playScreen.subEnergy(Constants.WASTE_ENERGY_PER_SEC *dt))
                playScreen.getCommandHandler().addCommandPlay(new TurnOffCommand(playScreen));
        }
        rayHandler.setCombinedMatrix(screen.getGameCam());
        actorLight.setPosition(screen.getPlayer().getGameActor().b2body.getPosition());
//        if(!LightSwitch.isPlayingHuman())
//            actorLight.setActive(false);
        rayHandler.updateAndRender();
    }

    public void addLight(float x, float y) {
        Random rand = new Random();
        addLight(x, y, COLORS[rand.nextInt(COLORS.length-1)]);
    }

    public void addLight(float x, float y, Color color) {
        pointLights.add(new PointLight(rayHandler, Constants.LIGHT_RAYS, color, Constants.LIGHT_DISTANCE, x, y));
        transformAboveLight(pointLights.peek());
    }

    public boolean lightsOn() {
        return lightsOn;
    }

    public void turnOn() {
        if(!playScreen.subEnergy(Constants.WASTE_ENERGY_PER_SWITCH))
            return;
        lightsOn = true;
        actorLight.setActive(!lightsOn);
        setPointLightsActive(lightsOn);
    }

    public void turnOff() {
        lightsOn = false;
        actorLight.setActive(!lightsOn);
        setPointLightsActive(lightsOn);
    }


    public void setPointLightsActive(boolean active) {
        for (PointLight light : pointLights) {
            light.setActive(active);
        }
    }
}
