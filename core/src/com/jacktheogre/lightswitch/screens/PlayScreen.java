package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.Hud;
import com.jacktheogre.lightswitch.LightSwitch;
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
import com.jacktheogre.lightswitch.sprites.Player;
import com.jacktheogre.lightswitch.tools.CameraSettings;
import com.jacktheogre.lightswitch.tools.input.PlayInputHandler;
import com.jacktheogre.lightswitch.tools.Lighting;
import com.jacktheogre.lightswitch.tools.WorldContactListener;


/**
 * Created by luna on 10.12.16.
 */
public class PlayScreen extends GameScreen {
    private final WorldContactListener contactListener;
    private PlayInputHandler inputHandler;
    public Array<InteractiveObject> objects;
    private Array<Shard> shards;

    private OrthogonalTiledMapRenderer mapRenderer;

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
    private int shardsCollected;

    private CameraSettings currentSetings, targetSettings;

    public PlayScreen(GeneratingScreen screen) {
        super();
        this.game = screen.getGame();
        gameCam = screen.getGameCam();
        currentSetings = new CameraSettings(screen.getGameCam());
//        if(!game.isPlayingHuman())
//            targetSettings = new CameraSettings(currentSetings.getX() + 25, currentSetings.getY() + 30, currentSetings.getZoom() - 0.2f);
//        else
            targetSettings = new CameraSettings(currentSetings.getX(), currentSetings.getY() + 30, currentSetings.getZoom() - 0.1f);

        gamePort = screen.getGamePort();
        mapRenderer = screen.getMapRenderer();
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
        commandHandler.addCommand(new StopCommand(player));
        energy = 100f;
        fixturesContacts = new Array<Contact>();
        runTime = 0;
        shardsCollected = 0;

        initializeButtons();

        Gdx.input.setInputProcessor(inputHandler);
    }

    //sorts them out by index
    private Array<InteractiveObject> combineTrapsAndTeleports(Array<Teleport> teleports, Array<Trap> traps) {
        Array<InteractiveObject> objectArray = new Array<InteractiveObject>();
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

    public void update(float dt) {
        super.update(dt);
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
        if (commandHandler.newCommands()) {
            commandHandler.executeCommandsPlay();
        }
        for (InteractiveObject object :objects) {
            object.update(dt);
        }
        player.update(dt);
        enemyPlayer.update(dt);
        checkFixtureContacts();
        world.step(1 / 60f, 6, 2);
        player.getGameActor().remakePath();

        lerpCamera(targetSettings, dt);
        gameCam.update();

        mapRenderer.setView(gameCam);
    }

    public void render(float dt) {
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);
        update(dt);

        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        for (InteractiveObject object : objects) {
            object.render(game.batch, dt);
        }
        for (Shard shard: shards) {
            shard.render(game.batch, dt);
        }
        if(higher(player.getGameActor(), enemyPlayer.getGameActor())) {
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

        shapeRenderer.begin();
        shapeRenderer.setColor(Color.BLUE);
        if(touchPoint != null)
            shapeRenderer.circle(touchPoint.x, touchPoint.y, 1);
        shapeRenderer.end();

        lighting.render(dt);

        hud.render(dt);

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

//        LevelManager.graph.render(shapeRenderer);
//        b2dRenderer.render(world, gameCam.combined);
//        player.getGameActor().getPath().render(shapeRenderer);
//        enemyPlayer.getGameActor().getPath().render(shapeRenderer);
//        fpsLogger.log();
    }

    private boolean higher(GameActor first, GameActor second) {
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
        GameActor.Direction direction;
        if(x == 0 && y == 0) {
            if(player.getGameActor().isMoving())
                commandHandler.addCommand(new StopCommand(player));
            return;
        }
        if(x >= 0) {
            if(y >= 0) {
                if(x > y)
                    direction = GameActor.Direction.RIGHT;
                else
                    direction = GameActor.Direction.UP;
            }
            else {
                if(x > -y)
                    direction = GameActor.Direction.RIGHT;
                else
                    direction = GameActor.Direction.DOWN;
            }
        } else {
            if(y >= 0) {
                if(-x > y)
                    direction = GameActor.Direction.LEFT;
                else
                    direction = GameActor.Direction.UP;
            }
            else {
                if(-x > -y)
                    direction = GameActor.Direction.LEFT;
                else
                    direction = GameActor.Direction.DOWN;
            }
        }

        if(player.getGameActor().getDirection() != direction || !player.getGameActor().isMoving())
            commandHandler.addCommand(new StartMovingCommand(direction, getPlayer()));
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

    public void addFixtureContact(Contact contact) {
        fixturesContacts.add(contact);
    }

    public Array<Contact> getFixturesContacts() {
        return fixturesContacts;
    }


    public void endGame() {
        boolean win = false;
        if(runTime > Constants.PLAYTIME) {
            win = game.isPlayingHuman();
        }
        if(win) {
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

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
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
        mapRenderer.dispose();
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
