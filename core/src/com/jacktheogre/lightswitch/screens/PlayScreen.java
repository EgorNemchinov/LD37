package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.Hud;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.commands.CommandHandler;
import com.jacktheogre.lightswitch.commands.StartMovingCommand;
import com.jacktheogre.lightswitch.commands.StopCommand;
import com.jacktheogre.lightswitch.multiplayer.MessageHandler;
import com.jacktheogre.lightswitch.objects.InteractiveObject;
import com.jacktheogre.lightswitch.objects.NullInteractiveObject;
import com.jacktheogre.lightswitch.objects.Shard;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.objects.Trap;
import com.jacktheogre.lightswitch.sprites.EnemyPlayer;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.MapActor;
import com.jacktheogre.lightswitch.sprites.Player;
import com.jacktheogre.lightswitch.tools.Assets;
import com.jacktheogre.lightswitch.tools.CameraSettings;
import com.jacktheogre.lightswitch.tools.ColorLoader;
import com.jacktheogre.lightswitch.tools.input.PlayInputHandler;
import com.jacktheogre.lightswitch.tools.Lighting;
import com.jacktheogre.lightswitch.tools.WorldContactListener;
import com.jacktheogre.lightswitch.tutorials.TutorialTelegraph;


/**
 * Created by luna on 10.12.16.
 */
public class PlayScreen extends GameScreen {
    private final Color BACKGROUND_COLOR = ColorLoader.colorMap.get("PLAYING_SCREEN_BACKGROUND");
    private final WorldContactListener contactListener;
    private PlayInputHandler inputHandler;
    public Array<InteractiveObject> objects;
    private Array<Shard> shards;

//    private OrthogonalTiledMapRenderer mapRenderer;
    private MapActor mapActor;

    private World world;
    private Box2DDebugRenderer b2dRenderer;

    private Hud hud;

    private Player player;

    private Lighting lighting;
    private CommandHandler commandHandler;

    private FPSLogger fpsLogger;

    private EnemyPlayer enemyPlayer;
    private Vector2 touchPoint;
    private Array<Contact> fixturesContacts;

    private float runTime;
    private float energy;

    private CameraSettings currentSetings, targetSettings;
    private boolean paused = false;

    public PlayScreen(GeneratingScreen screen) {
        super();
        this.game = screen.getGame();
//        gameCam = screen.getGameCam();
        currentSetings = new CameraSettings(screen.getGameCam());
//        if(!game.isPlayingHuman())
//            targetSettings = new CameraSettings(currentSetings.getX() + 25, currentSetings.getY() + 30, currentSetings.getZoom() - 0.2f);
//        else
            targetSettings = new CameraSettings(currentSetings.getX(), currentSetings.getY() + 30, currentSetings.getZoom() - 0.1f);

//        gamePort = screen.getGamePort();
//        mapRenderer = new OrthogonalTiledMapRenderer(Assets.getAssetLoader().getMap());
        mapActor = new MapActor(1f, Assets.getAssetLoader().getMap(), gamePort);
        mapActor.setCamera(gameCam);

        world = screen.getWorld();
        b2dRenderer = new Box2DDebugRenderer();
        contactListener = new WorldContactListener(this);

        player = screen.getPlayer();
        enemyPlayer = screen.getEnemyPlayer();

        inputHandler = new PlayInputHandler(this);
        hud = new Hud(this);

        fpsLogger = new FPSLogger();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);

        objects = combineTrapsAndTeleports(screen.getTeleports(), screen.getTraps());
        for (InteractiveObject obj : objects) {
            obj.initPhysics();
            obj.initClose();
        }

        shards = new Array<Shard>(screen.getShards());
        for(Shard shard : shards) {
            shard.setPlayScreen(this);
        }

        commandHandler = screen.getCommandHandler();
        commandHandler.setScreen(this);
        commandHandler.setScreenState(CommandHandler.ScreenState.PLAYSCREEN);

        lighting = screen.getLighting();
        lighting.setPlayScreen(this);
        world.setContactListener(contactListener);
        lighting.turnOff();
        commandHandler.addCommandPlay(new StopCommand(player));
        energy = 100f;
        fixturesContacts = new Array<Contact>();
        runTime = 0;

        paused = false;
        initializeButtons();

        notifyTutorialTelegraph();
//        game.batch.setShader(Assets.getAssetLoader().shaderProgram);

        Gdx.input.setInputProcessor(inputHandler);
    }

    @Override
    protected void notifyTutorialTelegraph() {
        super.notifyTutorialTelegraph();
        MessageManager.getInstance().dispatchMessage(0.01f, null, TutorialTelegraph.getInstance(), TutorialTelegraph.LIGHT_BUTTON);
    }

    //sorts them out by index
    private Array<InteractiveObject> combineTrapsAndTeleports(Array<Teleport> teleports, Array<Trap> traps) {
        Array<InteractiveObject> objectArray =  new Array<InteractiveObject>();
        int index = 1, indexTeleports = 0, indexTraps = 0;
        Teleport currentTeleport;
        Trap currentTrap;
        while(objectArray.size < teleports.size + traps.size) {
            currentTeleport = teleports.size > indexTeleports ? teleports.get(indexTeleports) : null;
            currentTrap = traps.size > indexTraps ? traps.get(indexTraps) : null;
            if(currentTeleport != null && currentTeleport.getIndex() == index) {
                    index++;
                    indexTeleports++;
                    objectArray.add(currentTeleport);
            }
            else if(currentTrap != null && currentTrap.getIndex() == index) {
                    index++;
                    indexTraps++;
                    objectArray.add(currentTrap);
            } else {
                Gdx.app.log("", "No IO with such index");
            }
            }
        return objectArray;
    }

    private int updateCounter = 0;
    private long lastTimeUpdate = System.currentTimeMillis();

    private void countUpdates() {
        if(System.currentTimeMillis() - lastTimeUpdate >= 1000) {
//            Gdx.app.log("updates", updateCounter+"");
            lastTimeUpdate = System.currentTimeMillis();
            updateCounter = 0;
        }
        updateCounter++;
    }

    public void update(float dt) {
        super.update(dt);

        countUpdates();

        if(!paused) {
            inputHandler.update();
            runTime += dt;
            if (runTime > Constants.PLAYTIME) {
                endGame();
            }
            addEnergy(Constants.ADD_ENERGY_PER_SEC * dt);
            if(Gdx.app.getType() == Application.ApplicationType.Android) {
                handleTouchpadInput();
            }

            MessageHandler.getMessage(commandHandler);
            //messages are sent from commandhandler
            commandHandler.update(dt);
            for (InteractiveObject object :objects) {
                object.update(dt);
            }
            player.update(dt);
            enemyPlayer.update(dt);
            checkFixtureContacts();

        }
        gamePort.apply();
        world.step(1 / (float)FPS, 6, 2);

        player.getGameActor().remakePath();

        lerpCamera(targetSettings, dt);
        gameCam.update();

        mapActor.act(dt);
    }

    private long lastTime = System.nanoTime();
    public static final long ONE_SECOND_NS = 1000000000;
    private int maxUpdates = 0;
    public static final int FPS = 60;
    private final long timeStepNano = ONE_SECOND_NS / FPS;
    private long timeLeft = 0;

    public void render(float dt) {
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            renderAndroid(dt);
        } else if(Gdx.app.getType() == Application.ApplicationType.Desktop || Gdx.app.getType() == Application.ApplicationType.WebGL) {
            //because fps is always ~60
            update(dt);
            renderWorld(dt);
        }
        highlighter.render(dt);
    }

    private void renderAndroid(float dt) {
        long time = System.nanoTime();
        long timeDelta = time - lastTime + timeLeft;
        lastTime = System.nanoTime();

        int updateCount = 0;
        while (timeDelta > timeStepNano && (maxUpdates <= 0 || updateCount < maxUpdates) /*&& !paused*/) {
            // Update using a time step in seconds
//            long updateTimeStep = Math.min(timeDelta, ONE_SECOND_NS / FPS);
            long updateTimeStep = timeStepNano;
            float updateTimeStepSeconds = updateTimeStep / (float) ONE_SECOND_NS;

            update(updateTimeStepSeconds);

            timeDelta -= updateTimeStep;
            updateCount++;
        }
        timeLeft = timeDelta;

        renderWorld(dt);

    }

    private void renderWorld(float dt) {
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setColor(Color.WHITE);

        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapActor.getMapRenderer().render();
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        for (InteractiveObject object : objects) {
            object.render(game.batch, shapeRenderer, dt);
        }
        for (Shard shard: shards) {
            shard.render(game.batch, dt);
        }
        if(isHigher(player.getGameActor(), enemyPlayer.getGameActor())) {
            player.getGameActor().draw(game.batch);
            if(lighting.lightsOn() || !LightSwitch.isPlayingHuman()) {
                enemyPlayer.getGameActor().draw(game.batch);
            }
        } else {
            if(lighting.lightsOn() || !LightSwitch.isPlayingHuman()) {
                enemyPlayer.getGameActor().draw(game.batch);
            }
            player.getGameActor().draw(game.batch);
        }
        game.batch.end();

//        shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.BLUE);
//        shapeRenderer.rect(100, 100, 100, 100);
//        shapeRenderer.circle(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 200);
//        if(touchPoint != null)
//            shapeRenderer.circle(touchPoint.x, touchPoint.y, 1);
//        shapeRenderer.end();
        lighting.render(dt);

        hud.render(dt);
//        LevelManager.graph.render(shapeRenderer);
//        b2dRenderer.render(world, gameCam.combined);
//        player.getGameActor().getPath().render(shapeRenderer);
//        enemyPlayer.getGameActor().getPath().render(shapeRenderer);
//        fpsLogger.log();
    }

    private boolean isHigher(GameActor first, GameActor second) {
        return first.b2body.getPosition().y > second.b2body.getPosition().y;
    }

    @Override
    protected void initializeButtons() {
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            if(LightSwitch.isPlayingHuman())
                buttons.add(hud.getLightButton());
            else
                buttons.add(hud.getWallthroughButton());
        }
    }

    private void handleTouchpadInput() {
        Touchpad touchpad = hud.getTouchpad();
        float x = touchpad.getKnobPercentX();
        float y = touchpad.getKnobPercentY();
//        if(x == 0 && y == 0) {
//            if(!player.getGameActor().getVelocity().equals(Vector2.Zero))
//                commandHandler.addCommandPlay(new StopCommand(player));
//            return;
//        }
        GameActor.HorizontalDirection horizontalDirection = GameActor.HorizontalDirection.NONE;
        GameActor.VerticalDirection verticalDirection = GameActor.VerticalDirection.NONE;
        if(x > Constants.TOUCHPAD_EDGE)
            horizontalDirection = GameActor.HorizontalDirection.RIGHT;
        else if(x < -Constants.TOUCHPAD_EDGE)
            horizontalDirection = GameActor.HorizontalDirection.LEFT;
        if(y > Constants.TOUCHPAD_EDGE)
            verticalDirection = GameActor.VerticalDirection.UP;
        else if(y < -Constants.TOUCHPAD_EDGE)
            verticalDirection = GameActor.VerticalDirection.DOWN;

        if(player.getGameActor().getDirection().getHorizontalDirection() != horizontalDirection)
            commandHandler.addCommandPlay(new StartMovingCommand(horizontalDirection, player));
        if(player.getGameActor().getDirection().getVerticalDirection() != verticalDirection)
            commandHandler.addCommandPlay(new StartMovingCommand(verticalDirection, player));

        player.getGameActor().setMoving(true);
    }

    private void checkFixtureContacts() {
        for (int i = 0; i < fixturesContacts.size; i++) {
            if(contactListener.checkContact(fixturesContacts.get(i)))
                fixturesContacts.removeIndex(i);
        }
    }

    public void collectShard(int number) {
        hud.collectShard(number);
    }

    //center of tile
    public void activateShard(int x, int y) {
        for (Shard shard : shards) {
            if (shard.getCenter().equals(new Vector2(x, y)))
                shard.activate(player);
        }
    }

    public void addFixtureContact(Contact contact) {
        fixturesContacts.add(contact);
    }

    public Array<Contact> getFixturesContacts() {
        return fixturesContacts;
    }


    public void endGame() {
//        Gdx.app.log("PlayScreen", "endGame called");
        game.batch.setShader(null);
        boolean win = false;
        if(runTime > Constants.PLAYTIME) {
            win = game.isPlayingHuman();
        }
        if(win) {
            if(LevelManager.getAmountOfCollectedShards() < hud.getAmountOfCollectedShards())
                LevelManager.collectShards(hud.collectedShardsAsString());
            game.setScreen(new GameOverScreen(game, GameOverScreen.State.WIN, objects));
        }
        else {
            game.setScreen(new GameOverScreen(game, GameOverScreen.State.LOSE, objects));
        }
    }

    private void lerpCamera(CameraSettings targetSettings, float dt) {
        if(currentSetings.getPosition().epsilonEquals(targetSettings.getPosition(), 5))
            return;
        float lerp = 10f;
        currentSetings.setX(currentSetings.getX() + (targetSettings.getX() - currentSetings.getX())*lerp*dt);
        currentSetings.setY(currentSetings.getY() + (targetSettings.getY() - currentSetings.getY())*lerp*dt);
        currentSetings.setZoom(currentSetings.getZoom() + (targetSettings.getZoom() - currentSetings.getZoom())*lerp*dt);
        currentSetings.applyTo(gameCam);
    }

    public void addEnergy(float addition) {
        energy += addition;
        if(energy > 100)
            energy = 100;
    }

    public boolean subEnergy(float sub) {
        if(energy < sub) {
            return false;
        }
        energy -= sub;
        return true;
    }

    public float getEnergy() {
        return energy;
    }

    @Override
    public void show() {

    }

    public InteractiveObject getInteractiveObjectByIndex(int index) {
        for (int i = 0; i < objects.size; i++) {
            if(objects.get(i).getIndex() == index)
                return objects.get(i);
        }
        Gdx.app.log("PlayScreen", "Not found object by index");
        return new NullInteractiveObject(game);
    }

    public boolean isPlayingHuman() {
        return game.isPlayingHuman();
    }

    public void setTouchPoint(int x, int y) {
        touchPoint = new Vector2(x, y);
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public float getRunTime() {
        return runTime;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public Array<Shard> getShards() {
        return shards;
    }

    public LightSwitch getGame() {
        return game;
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
        hud.dispose();
        world.dispose();
//        mapRenderer.dispose();
    }

    public World getWorld() {
        return world;
    }

    public OrthographicCamera getGameCam() {
        return gameCam;
    }

    public Player getPlayer() {
        return player;
    }

    public Lighting getLighting() {
        return lighting;
    }

    public Viewport getGamePort() {
        return gamePort;
    }

    public PlayInputHandler getInputHandler() {
        return inputHandler;
    }

    public EnemyPlayer getEnemyPlayer() {
        return enemyPlayer;
    }

    public void setObjects(Array<InteractiveObject> objects) {
        this.objects = objects;
    }

    public Hud getHud() {
        return hud;
    }
}
