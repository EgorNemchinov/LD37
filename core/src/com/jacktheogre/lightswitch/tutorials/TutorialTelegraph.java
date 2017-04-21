package com.jacktheogre.lightswitch.tutorials;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Align;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.commands.TurnOffCommand;
import com.jacktheogre.lightswitch.commands.TurnOnCommand;
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
    public static final int LEVEL_FRAME = index++;

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
            screen.setHighlighter(new Highlighter(screen, 8f, "Use portals to shorten the distance. \nIt works on the monster too though.") {
                @Override
                protected void onCreate() {
                    super.onCreate();
                    camera = generatingScreen.getUiCamera();
                    addButton(generatingScreen.getTeleportButton());
                    okButton.setScale(1.1f);
                    okButton.setPosition(screen.getGamePort().getWorldWidth() / 2 - okButton.getBoundingRectangle().getWidth() / 2 - 90, -35);
                }

                @Override
                protected void renderItems(float dt) {
                    screen.getGamePort().setCamera(camera);
                    screen.getGamePort().apply();
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
            screen.setHighlighter(new Highlighter(screen, 8f, "It's a trap! \n" +
                    "Turn your ships around, fools! \n" +
                    "Actually it just holds the clumsy\n" +
                    "monster who stepped into it.") {
                @Override
                protected void onCreate() {
                    super.onCreate();
                    camera = generatingScreen.getUiCamera();
                    addButton(generatingScreen.getTrapButton());
                    okButton.setScale(1.1f);
                    okButton.setPosition(screen.getGamePort().getWorldWidth() / 2 - okButton.getBoundingRectangle().getWidth() / 2 - 90, -35);
                }

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
            screen.setHighlighter(new Highlighter(screen, 8f, "Collect shards to unlock next levels.") {
                @Override
                protected void onCreate() {
                    super.onCreate();
                    okButton.setScale(1.1f);
                    okButton.setPosition(screen.getGamePort().getWorldWidth() / 2 - okButton.getBoundingRectangle().getWidth() / 2 - 90, -35);
                }

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
                protected void onCreate() {
                    super.onCreate();
                    camera = generatingScreen.getUiCamera();
                    addButton(generatingScreen.getStart());
                    okButton.setScale(1.1f);
                    okButton.setPosition(screen.getGamePort().getWorldWidth() / 2 - okButton.getBoundingRectangle().getWidth() / 2 - 90, -35);
                }

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
                label =  "Press the button to \n" +
                        "turn on the lights,\n" +
                    "but be careful: amount of energy\n" +
                    "\t\t\tis limited!\n" +
                        "Try it out now!";
            else
                label = "Press SPACE to turn on the lights,\n" +
                        "but be careful: amount of energy\n" +
                        "\t\t\tis limited! \n" +
                        "Try it out now!";
            screen.setHighlighter(new Highlighter(screen, -1f, label) {
                @Override
                protected void onCreate() {
                    super.onCreate();
                    camera = playScreen.getHud().stage.getCamera();
                    okButton.setScale(1);
                    okButton.setPosition(label.getX() + label.getWidth() - okButton.getBoundingRectangle().getWidth() / 2 - 25,
                                        label.getY() - okButton.getBoundingRectangle().getHeight() - 20);
                }

                @Override
                protected void renderItems(float dt) {
                    Button lightButton = playScreen.getHud().getLightButton();
                    if(lightButton != null)
                        lightButton.draw(screen.getSpriteBatch());
                }

                @Override
                protected void beginAction() {
                    playScreen.setPaused(true);
                    if(Gdx.app.getType() == Application.ApplicationType.Android)
                        addButton(playScreen.getHud().getLightButton());
                }

                @Override
                protected void finishAction() {
                    TutorialTelegraph.getInstance().unlock(LIGHT_BUTTON);
                    playScreen.setPaused(false);
                    playScreen.addEnergy(100);
                }

                @Override
                public boolean keyDown(int keycode) {
                    if(keycode == Input.Keys.SPACE) {
                        playScreen.getCommandHandler().addCommandPlay(new TurnOnCommand(playScreen));
                    } else if(keycode == Input.Keys.ENTER) {
                        okButton.press();
                    }
                    return true;
                }

                @Override
                public boolean keyUp(int keycode) {
                    if(keycode == Input.Keys.SPACE) {
                        playScreen.getCommandHandler().addCommandPlay(new TurnOffCommand(playScreen));
                    } else if(keycode == Input.Keys.ENTER) {
                        okButton.unpress();
                    }
                    return true;
                }

                @Override
                protected void initializeLabel() {
                    super.initializeLabel();
                    label.setPosition(100, screen.getGamePort().getWorldHeight() - 100, Align.bottomLeft);
                }

                @Override
                protected void update(float dt) {
                    playScreen.addEnergy(Constants.ADD_ENERGY_PER_SEC*dt);
                }
            });
            screen.getHighlighter().begin();
        } else if(msg.message == LEVEL_FRAME) {
            final LevelChoosingScreen levelChoosingScreen= (LevelChoosingScreen) screen;
            String label;
            if(Gdx.app.getType() == Application.ApplicationType.Android)
                label =  "I have no idea\n" +
                         "what to write here so\n" +
                        "I'll just pretend that \n" +
                        "it's part of the plan :s";
            else
                label = "\tI have no idea\n" +
                        "what to write here so\n" +
                        "I'll just pretend that \n" +
                        "it's part of the plan :s";
            screen.setHighlighter(new Highlighter(screen, 8F, label) {
                LevelChoosingScreen.Frame frame;
                @Override
                protected void onCreate() {
                    super.onCreate();
                    frame = levelChoosingScreen.getFrame(1);
                }

                @Override
                protected void renderItems(float dt) {
                    frame.setOpacity(calculateOpacity());
                    frame.drawBefore();
                    screen.getSpriteBatch().end();
                    levelChoosingScreen.getStage().draw();
                    screen.getSpriteBatch().begin();
                    frame.drawAfter();
                }

                @Override
                protected float calculateOpacity() {
                    if(getState() == State.WAITING)
                        return 1f;
                    float baseOpacity = 0.6f;
                    return (float) (baseOpacity + (1-baseOpacity)*Math.abs(Math.cos(timeSinceBegin)));
                }

                @Override
                protected void beginAction() {
                }

                @Override
                protected void finishAction() {
                    TutorialTelegraph.getInstance().unlock(LEVEL_FRAME);
                    frame.setOpacity(1f);
                }

                @Override
                protected void initializeLabel() {
                    super.initializeLabel();
                    label.setPosition(20, screen.getGamePort().getWorldHeight() - 100, Align.bottomLeft);
                    label.setWidth(100);
                    label.setFontScale(0.4f);
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
