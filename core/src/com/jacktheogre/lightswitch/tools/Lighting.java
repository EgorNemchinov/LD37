package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.screens.PlayScreen;

import java.util.Random;

import box2dLight.PointLight;
import box2dLight.RayHandler;

/**
 * Created by luna on 24.10.16.
 */
public class Lighting {

    private final Color[] COLORS = {
        new Color(0xFF/255f, 0xEF/255f, 0x8F/255f, 0.8f), new Color(0xF7/255f, 0xF0/255f, 0x97/255f, 0.8f)
    };

    //box2dlights
    private RayHandler rayHandler;
    private Array<PointLight> pointLights;
    private PointLight actorLight;
    private PlayScreen screen;
    private Filter aboveLightFilter, actorLightFilter;
    private boolean lightsOn = true;

    // TODO: 24.10.16 probably make lights static
    public Lighting(PlayScreen screen) {
        this.screen = screen;
        rayHandler = new RayHandler(screen.getWorld());
        rayHandler.setAmbientLight(0, 0.2f, 0, 0.1f);
        rayHandler.setBlurNum(3);
        // TODO: 20.10.16 add generating lights to worldcreator
        pointLights = new Array<PointLight>();
        /*pointLights.add(new PointLight(rayHandler, Constants.LIGHT_RAYS, Color.BLUE, Constants.LIGHT_DISTANCE, 64, 64));
        pointLights.add(new PointLight(rayHandler, Constants.LIGHT_RAYS, Color.BROWN, Constants.LIGHT_DISTANCE, 200, 112));
        pointLights.add(new PointLight(rayHandler, Constants.LIGHT_RAYS, Color.PINK, Constants.LIGHT_DISTANCE, 300, 64));
        pointLights.add(new PointLight(rayHandler, Constants.LIGHT_RAYS, Color.FOREST, Constants.LIGHT_DISTANCE, 400, 120));
        pointLights.add(new PointLight(rayHandler, Constants.LIGHT_RAYS, Color.OLIVE, Constants.LIGHT_DISTANCE, 550, 100));*/

        actorLight = new PointLight(rayHandler, Constants.LIGHT_RAYS, new Color(0xF1/255f, 0x91/255f, 0x22/255f, 0.8f), Constants.LIGHT_DISTANCE * 0.4f, 550, 100);
        transformActorLight(actorLight);

        actorLightFilter = new Filter();
        actorLightFilter.categoryBits = Constants.LIGHT_BIT;
        actorLightFilter.maskBits = Constants.WALLS_BIT | Constants.OBJECT_BIT;

        aboveLightFilter = new Filter();
        aboveLightFilter.categoryBits = Constants.LIGHT_BIT;
        aboveLightFilter.maskBits = 0; //light is above everything. player can see all of it

        for (PointLight light : pointLights) {
            transformAboveLight(light);
        }
        actorLight.setActive(false);
    }

    private void transformAboveLight(PointLight light) {
        light.setContactFilter(aboveLightFilter);
        light.setStaticLight(true);
        light.setSoftnessLength(0);
    }

    private void transformActorLight(PointLight light) {
        light.setContactFilter(actorLightFilter);
        light.setStaticLight(true);
        light.setSoftnessLength(0);
    }

    public void render() {
        rayHandler.setCombinedMatrix(screen.getGameCam());
        actorLight.setPosition(screen.getPlayer().getActor().b2body.getPosition());
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
//        rayHandler.setAmbientLight(251/255f,239/255f,100/255f, 0.5f);
        lightsOn = true;
        actorLight.setActive(!lightsOn);
        setPointLightsActive(lightsOn);
    }

    public void turnOff() {
//        rayHandler.setAmbientLight(0, 0.2f, 0, 0.1f);
        lightsOn = false;
        actorLight.setActive(!lightsOn);
        setPointLightsActive(lightsOn);
    }


    /*public void turnOn() {
        if(!lightsOn())
            switchLights();
    }

    public void turnOff(){
        if(lightsOn())
            switchLights();
    }

    public void switchLights() {
        lightsOn = !lightsOn;
        for (PointLight light : pointLights) {
            light.setActive(lightsOn);
        }
    }*/

    public void setPointLightsActive(boolean active) {
        for (PointLight light : pointLights) {
            light.setActive(active);
        }
    }
}
