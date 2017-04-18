package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.objects.InteractiveObject;
import com.jacktheogre.lightswitch.objects.Shard;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.objects.Trap;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.sprites.Monster;

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
            case Constants.TELEPORT_BIT | Constants.BOY_BIT:
                if(fixA.getFilterData().categoryBits == Constants.BOY_BIT) {
                    Teleport teleport = ((Teleport) fixB.getUserData());
                    if(!teleport.activate(LightSwitch.isPlayingHuman()?screen.getPlayer():screen.getEnemyPlayer())) {
                        screen.addFixtureContact(contact);
                    }
                    screen.activateShard(teleport.getX() + LevelManager.tilePixelWidth / 2, teleport.getY() + LevelManager.tilePixelHeight / 2);
                } else {
                    if(!((InteractiveObject) fixA.getUserData()).activate(LightSwitch.isPlayingHuman()?screen.getPlayer():screen.getEnemyPlayer())) {
                        screen.addFixtureContact(contact);
                    }
                }
                break;
            case Constants.TELEPORT_BIT | Constants.MONSTER_BIT:
                if(fixA.getFilterData().categoryBits == Constants.BOY_BIT || fixA.getFilterData().categoryBits == Constants.MONSTER_BIT) {
                    if(!((InteractiveObject) fixB.getUserData()).activate(LightSwitch.isPlayingHuman()?screen.getEnemyPlayer():screen.getPlayer()))
                        screen.addFixtureContact(contact);
                } else {
                    if(!((InteractiveObject) fixA.getUserData()).activate(LightSwitch.isPlayingHuman()?screen.getEnemyPlayer():screen.getPlayer()))
                        screen.addFixtureContact(contact);
                }
                break;
            case  Constants.BOY_BIT | Constants.MONSTER_BIT:
                screen.endGame();
                break;
            case Constants.MONSTER_BIT | Constants.TRAP_BIT:
                if(fixA.getFilterData().categoryBits == Constants.TRAP_BIT) {
                    if (!((Trap) fixA.getUserData()).activate(LightSwitch.isPlayingHuman()?screen.getEnemyPlayer():screen.getPlayer())) {
                        screen.addFixtureContact(contact);
                    }
                }
                else {
                    if (!((Trap) fixB.getUserData()).activate(LightSwitch.isPlayingHuman()?screen.getEnemyPlayer():screen.getPlayer())) {
                            screen.addFixtureContact(contact);
                       }
                }
                break;
            case Constants.BOY_BIT | Constants.PICKABLE_BIT:
                if(fixA.getFilterData().categoryBits == Constants.PICKABLE_BIT) {
                    ((Shard) (fixA.getUserData())).activate(LightSwitch.isPlayingHuman()?screen.getPlayer():screen.getEnemyPlayer());
                }
                else {
                    ((Shard) (fixB.getUserData())).activate(LightSwitch.isPlayingHuman()?screen.getPlayer():screen.getEnemyPlayer());
                }
        }
    }

    // FIXME: 06.02.17 doens't check all the time, mb contact ends too soon for some reason
    public boolean checkContact(Contact contact) {
        Gdx.app.log("ContactListener", "Checking contact");
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(cDef) {
            case Constants.TELEPORT_BIT | Constants.BOY_BIT:
                if (fixA.getFilterData().categoryBits == Constants.BOY_BIT) {
                    if (!((InteractiveObject) fixB.getUserData()).activate(LightSwitch.isPlayingHuman()?screen.getPlayer():screen.getEnemyPlayer()))
                        return false;
                    else return true;
                } else {
                    if (!((InteractiveObject) fixA.getUserData()).activate(LightSwitch.isPlayingHuman()?screen.getPlayer():screen.getEnemyPlayer()))
                        return false;
                    else
                        return true;
                }
            case Constants.MONSTER_BIT | Constants.TRAP_BIT:
                if(fixA.getFilterData().categoryBits == Constants.TRAP_BIT) {
                    if (!((Trap) fixA.getUserData()).trigger((Monster) fixB.getUserData()))
                        return false;
                    else
                        return true;
                } else {
                    if(!((Trap) fixB.getUserData()).trigger((Monster)fixA.getUserData()))
                        return false;
                    else
                        return true;
                }
        }
        return true;
    }

    public void update() {

    }

    @Override
    public void endContact(Contact contact) {
        if(screen.getFixturesContacts().contains(contact, true))
            screen.getFixturesContacts().removeValue(contact, true);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
