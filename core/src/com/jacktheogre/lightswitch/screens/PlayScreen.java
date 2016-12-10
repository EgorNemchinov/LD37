package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.Hud;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.Agent;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.commands.Command;
import com.jacktheogre.lightswitch.commands.CommandHandler;
import com.jacktheogre.lightswitch.commands.MoveCommand;
import com.jacktheogre.lightswitch.sprites.Player;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;
import com.jacktheogre.lightswitch.tools.B2WorldCreator;
import com.jacktheogre.lightswitch.tools.InputHandler;
import com.jacktheogre.lightswitch.tools.Lighting;

/**
 * Created by luna on 10.12.16.
 */
public class PlayScreen implements Screen{
    private AssetLoader loader;
    private Rectangle buttonBounds; // FIXME: 22.10.16 circle

    private LightSwitch game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private ShapeRenderer shapeRenderer;

    private OrthogonalTiledMapRenderer mapRenderer;

    private World world;

    private Box2DDebugRenderer b2dRenderer;

    private Hud hud;

    private Player player;

    private static Agent agent;

    private Lighting lighting;
    private CommandHandler commandHandler;

    private float runTime;

    private FPSLogger fpsLogger;
    public PlayScreen(LightSwitch game) {
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        gameCam.zoom -= 0.3f;

        loader = Assets.getAssetLoader();
        loader.load();
        mapRenderer = new OrthogonalTiledMapRenderer(loader.map);

        world = new World(new Vector2(0, 0), true);
        lighting = new Lighting(this);
        b2dRenderer = new Box2DDebugRenderer();

        LevelManager.loadLevel(loader.map);
        player = new Player(this);
        new B2WorldCreator(this);
        agent = new Agent(player.getActor(), world);

        hud = new Hud(game.batch);
        hud.setActor(player.getActor().toString());

        fpsLogger = new FPSLogger();
        // TODO: 10.12.16 finish
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);

        commandHandler = new CommandHandler(this);
        Gdx.input.setInputProcessor(new InputHandler(this));
        runTime = 0;
    }

    public void update(float dt) {
        commandHandler.update(dt);
        if(commandHandler.newCommands())
            commandHandler.executeCommands();
        player.update(dt);
        world.step(1/60f, 6, 2);

        lerpCamera(player.getActor().b2body.getPosition().x, player.getActor().b2body.getPosition().y , dt);

        gameCam.update();

        mapRenderer.setView(gameCam);
        runTime += dt;
    }

    private void lerpCamera(float targetX, float targetY, float dt) {
        float lerp;
        lerp = 10f;
        Vector3 position = gameCam.position;
        position.x += (targetX - position.x) * lerp * dt;
        position.y += (targetY - position.y) * lerp * dt;
    }

    @Override
    public void show() {

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
        player.getActor().draw(game.batch);

        game.batch.end();

        lighting.render();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

//        LevelManager.graph.render(shapeRenderer);
//        b2dRenderer.render(world, gameCam.combined);
//        player.getActor().getPath().render(shapeRenderer);

        //fpsLogger.log();
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

    public static Agent getAgent() {
        return agent;
    }

    public Lighting getLighting() {
        return lighting;
    }

    public Viewport getGamePort() {
        return gamePort;
    }
}
