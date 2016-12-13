package com.jacktheogre.lightswitch.screens;

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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.Constants;
import com.jacktheogre.lightswitch.Hud;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.commands.CommandHandler;
import com.jacktheogre.lightswitch.commands.StopCommand;
import com.jacktheogre.lightswitch.objects.InteractiveObject;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.sprites.EnemyPlayer;
import com.jacktheogre.lightswitch.sprites.Player;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.PlayInputHandler;
import com.jacktheogre.lightswitch.tools.Lighting;
import com.jacktheogre.lightswitch.tools.WorldContactListener;

/**
 * Created by luna on 10.12.16.
 */
public class PlayScreen implements Screen{
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
    private boolean gameOver;

    public PlayScreen(GeneratingScreen screen) {
        this.game = screen.getGame();
        gameCam = screen.getGameCam();
        gamePort = screen.getGamePort();
        loader = screen.getLoader();
        mapRenderer = screen.getMapRenderer();
        world = screen.getWorld();
        b2dRenderer = new Box2DDebugRenderer();

        player = screen.getPlayer();
        enemyPlayer = screen.getEnemyPlayer();

        hud = new Hud(this);

        fpsLogger = new FPSLogger();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);

        objects = screen.getObjects();

        commandHandler = screen.getCommandHandler();
        commandHandler.setScreen(this);

        lighting = screen.getLighting();
        lighting.setPlayScreen(this);
        Gdx.input.setInputProcessor(new PlayInputHandler(this));
        world.setContactListener(new WorldContactListener(this));
        lighting.turnOff();
        makeTeleportConnections();
        commandHandler.addCommand(new StopCommand());
        energy = 100f;
        gameOver = false;
        runTime = 0;
    }

    private void makeTeleportConnections() {

        for (int i = 0; i < objects.size; i++) {
            if(Teleport.class.isInstance(objects.get(i))) {
                Teleport tp = (Teleport) objects.get(i);
                for (int j = 0; j < objects.size; j++) {
                    if(Teleport.class.isInstance(objects.get(j)) && j != i) {
                        Teleport tpIns = (Teleport) objects.get(j);
                        tp.addTeleport(tpIns);
                    }
                }
            }
        }
    }

    public void update(float dt) {
        runTime += dt;
        if (runTime > Constants.PLAYTIME) {
            endGame(true);
        }

        addEnergy(5 * dt);
        commandHandler.update(dt);
        if (commandHandler.newCommands()) {
            commandHandler.executeCommandsPlay();
        }
        player.update(dt);
        enemyPlayer.update(dt);
        world.step(1 / 60f, 6, 2);

//        lerpCamera(player.getActor().b2body.getPosition().x, player.getActor().b2body.getPosition().y , dt);
        lerpCamera(gamePort.getWorldWidth() / 4, gamePort.getWorldHeight() / 4, dt);
        gameCam.update();

        mapRenderer.setView(gameCam);
    }

    public void endGame(boolean win) {
        if(win)
            game.setScreen(new GameOverScreen(game, GameOverScreen.State.WIN));
        else
            game.setScreen(new GameOverScreen(game, GameOverScreen.State.LOSE));
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
            object.render(game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.getActor().draw(game.batch);
        if(lighting.lightsOn())
            enemyPlayer.getEnemy().draw(game.batch);

        game.batch.end();

        shapeRenderer.begin();
        shapeRenderer.setColor(Color.RED);
        if(touchPoint != null)
            shapeRenderer.circle(touchPoint.x, touchPoint.y, 2);
        shapeRenderer.end();

        lighting.render(dt);

        hud.render();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

//        LevelManager.graph.render(shapeRenderer);
//        b2dRenderer.render(world, gameCam.combined);
//        player.getActor().getPath().render(shapeRenderer);
//        enemyPlayer.getEnemy().getPath().render(shapeRenderer);

        //fpsLogger.log();
    }

    private void lerpCamera(float targetX, float targetY, float dt) {
        float lerp;
        lerp = 10f;
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

    /*public void handleInput(float dt) {// TODO: 19.10.16 transfer handleinput to actor
        player.getActor().b2body.setLinearVelocity(0, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) )
            player.getActor().b2body.setLinearVelocity(player.getActor().getSpeed(), player.getActor().b2body.getLinearVelocity().y);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            player.getActor().b2body.setLinearVelocity(-player.getActor().getSpeed(), player.getActor().b2body.getLinearVelocity().y);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            player.getActor().b2body.setLinearVelocity(0, player.getActor().b2body.getLinearVelocity().y);
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
                player.getActor().b2body.setLinearVelocity(player.getActor().b2body.getLinearVelocity().x, 0);
            else
                player.getActor().b2body.setLinearVelocity(player.getActor().b2body.getLinearVelocity().x, player.getActor().getSpeed());
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            lighting.switchLights();
        }
        if(Gdx.input.justTouched()) {
            Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 point = gamePort.unproject(screenTouch.cpy());
            screenTouch.y = gamePort.getScreenHeight() - screenTouch.y;
//            Gdx.app.log("Touched", "Screen - " + screenTouch);
            player.setTarget(new Vector2(point.x, point.y));
            agent.makePath(player.getActor());
//                Gdx.app.log("ResultPath", agent.getResultPath().toString());
        }
    }*/

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

    public EnemyPlayer getEnemyPlayer() {
        return enemyPlayer;
    }

    public void setObjects(Array<InteractiveObject> objects) {
        this.objects = objects;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
