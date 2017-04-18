package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
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
            ColorLoader.colorMap.get("GLOBAL_LIGHTS_COLOR"), ColorLoader.colorMap.get("GLOBAL_LIGHTS_COLOR")
    };
    private final Color AMBIENT_HUMAN = ColorLoader.colorMap.get("AMBIENT_LIGHT_BOY_COLOR");
    private final Color AMBIENT_MONSTER = new Color(0.4f, 0.15f, 0.1f, 0.35f);
    private final Color LIGHT_HUMAN= ColorLoader.colorMap.get("ACTOR_LIGHT_COLOR");
    private final Color LIGHT_MONSTER = new Color(200/255f, 0/255f, 100/255f, 0.6f);

    //box2dlights
    private RayHandler rayHandler;
    private Array<PointLight> pointLights;
    private PointLight actorLight;
    private PlayScreen playScreen;
    private Filter aboveLightFilter, actorLightFilter;
    private boolean lightsOn = true;

    public Lighting(World world) {
        rayHandler = new RayHandler(world);
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
        rayHandler.setCombinedMatrix(playScreen.getGameCam());
        actorLight.setPosition(playScreen.getPlayer().getGameActor().b2body.getPosition());
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
