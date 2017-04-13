package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.sprites.Button;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;
import com.jacktheogre.lightswitch.multiplayer.BluetoothSingleton;
import com.jacktheogre.lightswitch.tools.ColorLoader;
import com.jacktheogre.lightswitch.tools.input.MainMenuInputHandler;

/**
 * Created by luna on 10.12.16.
 */
public class MainMenuScreen extends GameScreen{
    private final Color BACKGROUND_COLOR = ColorLoader.colorMap.get("MAINMENU_SCREEN_BACKGROUND");

    //basically every state has it's own array of buttons
    //inputhandler checks array of buttons received from getButtons(), which returns state's array
    public enum State {
        MAIN(),
        CHOOSING_GAMEMODE(MAIN),
        SERVER_OR_CLIENT(CHOOSING_GAMEMODE),
        CHOOSING_CHARACTER(MAIN);

        private Array<Button> buttons;
        private State previous;

        State() {
            this.buttons = new Array<Button>();
        }

        State(State previous) {
            this();
            this.previous = previous;
        }

        public Array<Button> getButtons() {
            return buttons;
        }

        public State getPrevious() {
            return previous;
        }
    }

    private State state;

    private Button play_button, boy_button, monster_button, singleplayer_button, multiplayer_button, nextdevice_button;
    private AssetLoader loader;

    public MainMenuScreen(LightSwitch game) {
        super();
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        loader = Assets.getAssetLoader();
        loader.load();

        setState(State.MAIN);

        initializeButtons();
        Gdx.input.setInputProcessor(new MainMenuInputHandler(this));
    }

    public MainMenuScreen(LightSwitch game,  State state) {
        this(game);
        setState(state);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r,BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        if(BluetoothSingleton.getInstance().getBluetoothManager().isConnected())
//            setState(State.CHOOSING_CHARACTER);
        renderButtons(gameCam);
    }

    //here initialize all the buttons and add them into different arrays for different states
    @Override
    protected void initializeButtons() {
        play_button = new Button(Assets.getAssetLoader().play_button, Button.State.ACTIVE, this) {
//            MainMenuScreen mainMenuScreen = (MainMenuScreen) screen;

            @Override
            protected void actUnpress() {
                screen.getGame().setScreen(new LevelChoosingScreen(screen.getGame()));
            }
        };
        play_button.setPosition(gamePort.getWorldWidth() / 2 - play_button.getWidth() / 2, gamePort.getWorldHeight() / 2 - play_button.getHeight() / 2);

        boy_button = new Button(Assets.getAssetLoader().boy_button, Button.State.ACTIVE, this) {
            @Override
            protected void actUnpress() {
                LightSwitch.setPlayingHuman(true);
                if(LightSwitch.getState() == LightSwitch.State.MULTIPLAYER) {
                    BluetoothSingleton.getInstance().getBluetoothManager().enableDiscoveribility();
                    BluetoothSingleton.getInstance().getBluetoothManager().startServer();
                    screen.getGame().setScreen(new ConnectingScreen(screen.getGame()));
                } else {
                    screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
                }
            }

            @Override
            public void initGraphics(TextureRegion textureRegion) {
                int width = textureRegion.getRegionWidth() / 2;
                Array<TextureRegion> frames = new Array<TextureRegion>();

                for (int i = 0; i < 2; i++) {
                    frames.add(new TextureRegion(textureRegion, i*width, 0, width, textureRegion.getRegionHeight()));
                }
                disabledTexture = frames.get(0);
                activeTexture = frames.get(0);
                focusedTexture = frames.get(0);
                pressedTexture = frames.get(1);

                this.setSize(width, this.getHeight());
            }
        };
        boy_button.setScale(2.5f);
        boy_button.setPosition(gamePort.getWorldWidth() / 4 - boy_button.getBoundingRectangle().getWidth() / 2, gamePort.getWorldHeight() / 2 - boy_button.getBoundingRectangle().getHeight() / 2);

        monster_button = new Button(Assets.getAssetLoader().monster_button, Button.State.ACTIVE, this) {
            @Override
            protected void actUnpress() {
                LightSwitch.setPlayingHuman(false);
                if(LightSwitch.getState() == LightSwitch.State.MULTIPLAYER) {
                    BluetoothSingleton.getInstance().getBluetoothManager().enableBluetooth();
                    BluetoothSingleton.getInstance().getBluetoothManager().discoverDevices();
                    BluetoothSingleton.getInstance().getBluetoothManager().connectToServer();
                    screen.getGame().setScreen(new ConnectingScreen(screen.getGame()));
                } else {
                    screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
                }
            }

            @Override
            public void initGraphics(TextureRegion textureRegion) {
                int width = textureRegion.getRegionWidth() / 2;
                Array<TextureRegion> frames = new Array<TextureRegion>();

                for (int i = 0; i < 2; i++) {
                    frames.add(new TextureRegion(textureRegion, i*width, 0, width, textureRegion.getRegionHeight()));
                }
                disabledTexture = frames.get(0);
                activeTexture = frames.get(0);
                focusedTexture = frames.get(0);
                pressedTexture = frames.get(1);
                this.setSize(width, this.getHeight());
            }
        };
        monster_button.setScale(2.5f);
        monster_button.setPosition((gamePort.getWorldWidth() * 3)/ 4 - boy_button.getBoundingRectangle().getWidth() / 2, gamePort.getWorldHeight() / 2 - boy_button.getBoundingRectangle().getHeight() / 2);

        singleplayer_button = new Button(Assets.getAssetLoader().singleplayer_button, Button.State.ACTIVE, this) {
            MainMenuScreen mainMenuScreen = (MainMenuScreen) screen;

            @Override
            public void actUnpress() {
                LightSwitch.setState(LightSwitch.State.SINGLEPLAYER);
                mainMenuScreen.setState(MainMenuScreen.State.CHOOSING_CHARACTER);
            }

            @Override
            public void initGraphics(TextureRegion textureRegion) {
                int width = textureRegion.getRegionWidth();
                disabledTexture = activeTexture = pressedTexture = focusedTexture = textureRegion;
                this.setSize(width, this.getHeight());
            }
        };
        singleplayer_button.setScale(1.5f);
        singleplayer_button.setPosition(gamePort.getWorldWidth() / 2 - singleplayer_button.getBoundingRectangle().getWidth() / 2,
                (gamePort.getWorldHeight()*3)/4 - singleplayer_button.getBoundingRectangle().getHeight() / 2);

        multiplayer_button = new Button(Assets.getAssetLoader().multiplayer_button, Button.State.ACTIVE, this) {
            MainMenuScreen mainMenuScreen = (MainMenuScreen) screen;

            @Override
            public void actUnpress() {
                LightSwitch.setState(LightSwitch.State.MULTIPLAYER);
                mainMenuScreen.setState(MainMenuScreen.State.CHOOSING_CHARACTER);
            }

            @Override
            public void initGraphics(TextureRegion textureRegion) {
                int width = textureRegion.getRegionWidth();
                disabledTexture = activeTexture = pressedTexture = focusedTexture = textureRegion;
                this.setSize(width, this.getHeight());
            }
        };
        multiplayer_button.setScale(1.5f);
        multiplayer_button.setPosition(gamePort.getWorldWidth() / 2 - multiplayer_button.getBoundingRectangle().getWidth() / 2,
                gamePort.getWorldHeight()/4 - multiplayer_button.getBoundingRectangle().getHeight() / 2);

        State.MAIN.getButtons().add(play_button);
        State.CHOOSING_GAMEMODE.getButtons().add(singleplayer_button);
        State.CHOOSING_GAMEMODE.getButtons().add(multiplayer_button);
        State.CHOOSING_CHARACTER.getButtons().add(boy_button);
        State.CHOOSING_CHARACTER.getButtons().add(monster_button);
    }

    //return needed buttons array depending on a state
    @Override
    public Array<Button> getButtons() {
        return state.getButtons();
    }

    public void setState(State state) {
        if(state.getPrevious() == null)
            Gdx.input.setCatchBackKey(false);
        else
            Gdx.input.setCatchBackKey(true);
        this.state = state;
    }

    public boolean returnBack() {
        if(state.getPrevious() == null) {
            return false;
        }
        setState(state.getPrevious());
        return true;
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Button getPlayButton() {
        return play_button;
    }

    public LightSwitch getGame() {
        return game;
    }

    public Viewport getGamePort() {
        return gamePort;
    }
}
