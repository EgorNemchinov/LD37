package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
import com.jacktheogre.lightswitch.objects.InteractiveObject;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.sprites.EnemyPlayer;
import com.jacktheogre.lightswitch.sprites.GameActor;
import com.jacktheogre.lightswitch.sprites.Player;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.PlayInputHandler;
import com.jacktheogre.lightswitch.tools.Lighting;
import com.jacktheogre.lightswitch.tools.WorldContactListener;


/**
 * Created by luna on 10.12.16.
 */
public class PlayScreen implements Screen{
    private final WorldContactListener contactListener;
    private PlayInputHandler inputHandler;
    private AssetLoader loader;
    public Array<InteractiveObject> objects;

    private LightSwitch game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private ShapeRenderer shapeRenderer;

    private OrthogonalTiledMapRenderer mapRenderer;

    private World world;

    private Box2DDebugRenderer b2dRenderer;

    private Hud hud;

    private Player player;

    private Lighting lighting;
    private CommandHandler commandHandler;

    private float runTime;
    private float energy;

    private FPSLogger fpsLogger;
    private EnemyPlayer enemyPlayer;
    private Vector2 touchPoint;

    private Array<Contact> fixturesContacts;

    public PlayScreen(GeneratingScreen screen) {
        this.game = screen.getGame();
        gameCam = screen.getGameCam();
        gamePort = screen.getGamePort();
        loader = screen.getLoader();
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

        objects = screen.getObjects();
        for (InteractiveObject obj : objects) {
            obj.initPhysics();
        }

        commandHandler = screen.getCommandHandler();
        commandHandler.setScreen(this);

        lighting = screen.getLighting();
        lighting.setPlayScreen(this);
        Gdx.input.setInputProcessor(inputHandler);
        world.setContactListener(contactListener);
        lighting.turnOff();
        makeTeleportConnections();
        commandHandler.addCommand(new StopCommand());
        energy = 100f;
        fixturesContacts = new Array<Contact>();
        runTime = 0;

    }

    public void update(float dt) {
        runTime += dt;
        if (runTime > Constants.PLAYTIME) {
            endGame(true);
        }
        addEnergy(Constants.ADD_ENERGY_PER_SEC * dt);
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            handleTouchpadInput();
        }

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

//        lerpCamera(player.getGameActor().b2body.getPosition().x, player.getGameActor().b2body.getPosition().y , dt);
        lerpCamera(gamePort.getWorldWidth() / 4, gamePort.getWorldHeight() / 4, dt);
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
        player.getGameActor().draw(game.batch);
        if(lighting.lightsOn())
            enemyPlayer.getMonster().draw(game.batch);

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
//        enemyPlayer.getMonster().getPath().render(shapeRenderer);
//        fpsLogger.log();
    }

    private void handleTouchpadInput() {
        Touchpad touchpad = hud.getTouchpad();
        float x = touchpad.getKnobPercentX();
        float y = touchpad.getKnobPercentY();
        GameActor.Direction direction;
        if(x == 0 && y == 0) {
            Gdx.app.log("PlayScreen", "touchpad x&y are zero");
            if(player.getGameActor().isMoving())
                commandHandler.addCommand(new StopCommand());
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
            commandHandler.addCommand(new StartMovingCommand(direction));
        player.getGameActor().setMoving(true);
    }

    private void checkFixtureContacts() {
        for (int i = 0; i < fixturesContacts.size; i++) {
            if(contactListener.checkContact(fixturesContacts.get(i)))
                fixturesContacts.removeIndex(i);
        }
    }

    public void addFixtureContact(Contact contact) {
        fixturesContacts.add(contact);
    }

    private void makeTeleportConnections() {
        for (int i = 0; i < objects.size; i++) {
            if(ClassReflection.isInstance(Teleport.class, objects.get(i))) {
                Teleport tp = (Teleport) objects.get(i);
                for (int j = 0; j < objects.size; j++) {
                    if(ClassReflection.isInstance(Teleport.class, objects.get(j)) && j != i) {
                        Teleport tpIns = (Teleport) objects.get(j);
                        tp.addTeleport(tpIns);
                    }
                }
            }
        }
    }

    public void endGame(boolean win) {
        if(win)
            game.setScreen(new GameOverScreen(game, GameOverScreen.State.WIN));
        else
            game.setScreen(new GameOverScreen(game, GameOverScreen.State.LOSE));
    }

    private void lerpCamera(float targetX, float targetY, float dt) {
        float lerp = 10f;
        Vector3 position = gameCam.position;
        position.x += (targetX - position.x) * lerp * dt;
        position.y += (targetY - position.y) * lerp * dt;
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
        // TODO: 10.12.16 dispose
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
