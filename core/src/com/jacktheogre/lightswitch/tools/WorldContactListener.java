package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.objects.InteractiveObject;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Actor;
import com.jacktheogre.lightswitch.sprites.Enemy;

/**
 * Created by luna on 11.12.16.
 */
public class WorldContactListener implements ContactListener {

    private PlayScreen screen;

    public WorldContactListener(PlayScreen screen) {
        this.screen = screen;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        switch(cDef) {
            case Constants.INTERACTIVE_BIT | Constants.ACTOR_BIT:
                if(fixA.getFilterData().categoryBits == Constants.ACTOR_BIT) {
                    ((InteractiveObject) fixB.getUserData()).activate((Actor)fixA.getUserData());
                } else {
                    ((InteractiveObject) fixA.getUserData()).activate((Actor)fixB.getUserData());
                }
                break;
            case  Constants.ACTOR_BIT | Constants.ACTOR_BIT:
                screen.endGame(false);

        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
