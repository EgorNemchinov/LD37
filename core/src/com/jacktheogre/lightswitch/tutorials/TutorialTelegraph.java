package com.jacktheogre.lightswitch.tutorials;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Align;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.objects.Shard;
import com.jacktheogre.lightswitch.screens.*;
import com.jacktheogre.lightswitch.sprites.Button;

/**
 * Created by luna on 14.04.17.
 */

public class TutorialTelegraph implements Telegraph{
    private static int index = 0;
    public final static int TELEPORTS_ON_LEVEL = index++;
    public final static int TRAPS_ON_LEVEL = index++;
    public final static int SHARDS_ON_LEVEL = index++;
    public final static int START_BUTTON_PRESSED = index++;
    public static final int LIGHT_BUTTON = index++;

    //// FIXME: 14.04.17 into preferences
    public boolean[] unlocked = new boolean[index];

    private GameScreen screen;

    private static TutorialTelegraph telegraph;

    public TutorialTelegraph() {
    }

    public void setScreen(GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if(unlocked[msg.message])
            return true;
        if(msg.message == TELEPORTS_ON_LEVEL) {
            if(LevelManager.getLevelNum() > 3) {
                unlock(TELEPORTS_ON_LEVEL);
                return true;
            }
            final GeneratingScreen generatingScreen = (GeneratingScreen) screen;
            screen.setHighlighter(new Highlighter(screen, 5f, "Use portals to shorten the distance. \nIt works on the monster too though.") {
                @Override
                protected void renderItems(float dt) {
                    generatingScreen.getTeleportButton().draw(generatingScreen.getSpriteBatch());
                }

                @Override
                protected void finishAction() {
                    TutorialTelegraph.getInstance().unlock(TELEPORTS_ON_LEVEL);
                }

                @Override
                protected void initializeLabel() {
                    super.initializeLabel();
                    label.setPosition(generatingScreen.getTeleportButton().getX() + generatingScreen.getTeleportButton().getWidth() + 10,
                            generatingScreen.getTeleportButton().getY() + generatingScreen.getTeleportButton().getHeight(), Align.left);
                    label.setWrap(false);
//                    label.setText("Portals are used to fuck your mother.");
                }
            });
            screen.getHighlighter().begin();
//            Gdx.app.log("TutorialTelegraph", "Teleports on a level");
        } else if(msg.message == TRAPS_ON_LEVEL) {
            if(LevelManager.getLevelNum() > 7) {
                unlock(TRAPS_ON_LEVEL);
                return true;
            }
            final GeneratingScreen generatingScreen = (GeneratingScreen) screen;
            screen.setHighlighter(new Highlighter(screen, 5f, "It's a trap! \n" +
                    "Turn your ships around, fools! \n" +
                    "Actually it just holds the clumsy\n" +
                    "monster who stepped into it.") {
                @Override
                protected void renderItems(float dt) {
                    generatingScreen.getTrapButton().draw(generatingScreen.getSpriteBatch());
                }

                @Override
                protected void finishAction() {
                    TutorialTelegraph.getInstance().unlock(TRAPS_ON_LEVEL);
                }

                @Override
                protected void initializeLabel() {
                    super.initializeLabel();
                    label.setPosition(generatingScreen.getTrapButton().getX() + generatingScreen.getTrapButton().getWidth() + 10,
                            generatingScreen.getTrapButton().getY() + generatingScreen.getTrapButton().getHeight(), Align.bottomLeft);
                }
            });
            screen.getHighlighter().begin();
        } else if(msg.message == SHARDS_ON_LEVEL) {
            if(LevelManager.getLevelNum() > 1) {
                unlock(SHARDS_ON_LEVEL);
                return true;
            }
            final GeneratingScreen generatingScreen = (GeneratingScreen) screen;
            screen.setHighlighter(new Highlighter(screen, 5f, "Collect shards to unlock next levels.") {
                @Override
                protected void renderItems(float dt) {
                    for(Shard shard: generatingScreen.getShards()) {
                        shard.render(screen.getSpriteBatch());
                    }
                }
                @Override
                protected void finishAction() {
                    TutorialTelegraph.getInstance().unlock(SHARDS_ON_LEVEL);
                    MessageManager.getInstance().dispatchMessage( 1f, null, TutorialTelegraph.getInstance(), TutorialTelegraph.START_BUTTON_PRESSED);
                }
            });
            screen.getHighlighter().begin();
        } else if(msg.message == START_BUTTON_PRESSED) {
            final GeneratingScreen generatingScreen = (GeneratingScreen) screen;
            screen.setHighlighter(new Highlighter(screen) {
                @Override
                protected void renderItems(float dt) {
                    generatingScreen.getStart().draw(screen.getSpriteBatch());
                }
                @Override
                protected void finishAction() {
                    TutorialTelegraph.getInstance().unlock(START_BUTTON_PRESSED);
                }
            });
            screen.getHighlighter().begin();
        } else if(msg.message == LIGHT_BUTTON) {
            final PlayScreen playScreen = (PlayScreen) screen;
            String label;
            if(Gdx.app.getType() == Application.ApplicationType.Android)
                label =  "You can turn on the lights,\n" +
                    "but be careful: amount of energy\n" +
                    "\t\t\tis limited!";
            else
                label = "Press SPACE to turn on the lights,\n" +
                        "but be careful: amount of energy\n" +
                        "\t\t\tis limited!";
            screen.setHighlighter(new Highlighter(screen, 5F, label) {
                @Override
                protected void renderItems(float dt) {
                    Button lightButton = playScreen.getHud().getLightButton();
                    if(lightButton != null)
                        lightButton.draw(screen.getSpriteBatch());
                }

                @Override
                protected void beginAction() {
                    playScreen.setPaused(true);
                }

                @Override
                protected void finishAction() {
                    TutorialTelegraph.getInstance().unlock(LIGHT_BUTTON);
                    playScreen.setPaused(false);
                }

                @Override
                protected void initializeLabel() {
                    super.initializeLabel();
                    label.setPosition(100, screen.getGamePort().getWorldHeight() - 100, Align.bottomLeft);
                }
            });
            screen.getHighlighter().begin();
        }
        return false;
    }

    public void unlock(int tutorial) {
        unlocked[tutorial] = true;
    }

    public static TutorialTelegraph getInstance() {
        if(telegraph == null)
            telegraph = new TutorialTelegraph();
        return telegraph;
    }
}
