package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.ai.Node;
import com.jacktheogre.lightswitch.commands.AddTeleportCommand;
import com.jacktheogre.lightswitch.commands.AddTrapCommand;
import com.jacktheogre.lightswitch.commands.Command;
import com.jacktheogre.lightswitch.commands.CommandHandler;
import com.jacktheogre.lightswitch.objects.InteractiveObject;
import com.jacktheogre.lightswitch.objects.Shard;
import com.jacktheogre.lightswitch.objects.Teleport;
import com.jacktheogre.lightswitch.objects.Trap;
import com.jacktheogre.lightswitch.sprites.Button;
import com.jacktheogre.lightswitch.sprites.EnemyPlayer;
import com.jacktheogre.lightswitch.sprites.Player;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;
import com.jacktheogre.lightswitch.tools.B2WorldCreator;
import com.jacktheogre.lightswitch.tools.ColorLoader;
import com.jacktheogre.lightswitch.tools.input.GenerateInputHandler;
import com.jacktheogre.lightswitch.tools.Lighting;

import static com.jacktheogre.lightswitch.tools.DrawingAssistant.drawDottedLine;
import static com.jacktheogre.lightswitch.tools.DrawingAssistant.drawPulsingDottedLine;

/**
 * Created by luna on 10.12.16.
 */
public class GeneratingScreen extends GameScreen {
    private final Color DEFAULT = new Color(0.2f, 0.1f, 0.2f, 0.3f);
    private final Color CORRECT = new Color(0, 1, 0, 0.3f);
    private final Color WRONG = new Color(1, 0, 0, 0.3f);
    private final Color BACKGROUND_COLOR = ColorLoader.colorMap.get("GENERATING_SCREEN_BACKGROUND");
//    private final Color BACKGROUND_COLOR = Color.BLACK;

    public enum State {DEFAULT, SETTING_TELEPORT, SETTING_TRAP}

    private State state;
    private final EnemyPlayer enemyPlayer;
    private final Player player;
    private Lighting lighting;
    private AssetLoader loader;
    private OrthogonalTiledMapRenderer mapRenderer;
    private World world;
    private CommandHandler commandHandler;

    private Button undo, redo, start, teleportButton, trapButton, clearButton;
    private Label teleportsLeft, trapsLeft;

    public Array<Teleport> teleports;
    public Array<Trap> traps;
    public Array<Shard> shards;
    private Node selectedNode;

    private Teleport unpairedTeleport = null;

    private float runTime;

    public GeneratingScreen(LightSwitch game) {
        super();
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        gameCam.zoom -= 0.2f;

        runTime = 0;

        loader = Assets.getAssetLoader();
        Node.Indexer.nullify();
        LevelManager.loadLevel(loader.getMap());
        mapRenderer = new OrthogonalTiledMapRenderer(loader.getMap(), 1);

        state = State.DEFAULT;

        initializeButtons();

        world = new World(new Vector2(0, 0), true);
        teleports = new Array<Teleport>();
        traps = new Array<Trap>();
        shards = new Array<Shard>();

        player = new Player(this);
        enemyPlayer = new EnemyPlayer(this);

        lighting = new Lighting(this);
        new B2WorldCreator(this);
        if(maxTeleports()) {
            teleportButton.disable();
        }
        if(maxTraps()) {
            trapButton.disable();
        }

        initializeLabels();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);
        commandHandler = new CommandHandler(this);
        Trap.Indexer.nullify();
        Teleport.Indexer.nullify();
        Gdx.input.setInputProcessor(new GenerateInputHandler(this));
//        game.batch.setShader(Assets.getAssetLoader().shaderProgram);

        initObjects = true;
    }

    private Array<InteractiveObject> interactiveObjects;
    boolean initObjects;

    public GeneratingScreen(LightSwitch game, Array<InteractiveObject> interactiveObjects) {
        this(game);
        initObjects = false;
        this.interactiveObjects = interactiveObjects;
        clearButton.setState(Button.State.ACTIVE);
    }


    @Override
    protected void initializeButtons() {
        undo = new Button(Assets.getAssetLoader().undo_button, Button.State.ACTIVE) {
            @Override
            protected void actUnpress() {
                commandHandler.undo();
            }
        };
        redo = new Button(Assets.getAssetLoader().redo_button, Button.State.ACTIVE) {
            @Override
            protected void actUnpress() {
                commandHandler.redo();
            }
        };
        start = new Button(Assets.getAssetLoader().start_button, Button.State.ACTIVE, this) {
            @Override
            protected void actUnpress() {
                game.setScreen(new PlayScreen(generatingScreen));
            }
        };
        teleportButton = new Button(Assets.getAssetLoader().teleport_button, Button.State.ACTIVE, this) {
            @Override
            protected void actPress() {
                generatingScreen.setState(GeneratingScreen.State.SETTING_TELEPORT);
            }
        };
        trapButton = new Button(Assets.getAssetLoader().trap_button, Button.State.ACTIVE, this) {
            @Override
            protected void actPress() {
                generatingScreen.setState(GeneratingScreen.State.SETTING_TRAP);
            }
        };
        clearButton = new Button(Assets.getAssetLoader().clear_button, Button.State.DISABLED, this) {
            @Override
            protected void actUnpress() {
                traps.clear();
                teleports.clear();
                InteractiveObject.Indexer.nullify();
                undo.disable();
                redo.disable();
                clearButton.disable();
                unpairedTeleport = null;
                if(!maxTeleports())
                    teleportButton.enable();
                if(!maxTraps())
                    trapButton.enable();
            }
        };
        undo.setScale(1.3f);
        redo.setScale(1.3f);
        clearButton.setScale(1.3f);
//        if(BluetoothSingleton.getInstance().bluetoothManager.isConnected())
        start.setScale(1.3f);
        teleportButton.setScale(1.2f);
        trapButton.setScale(1.2f);
        undo.setPosition(60, -25);
        redo.setPosition(undo.getX() + undo.getBoundingRectangle().getWidth()+10, undo.getY());
        start.setPosition(redo.getX() + redo.getBoundingRectangle().getWidth()+10, redo.getY());
        teleportButton.setPosition(-teleportButton.getBoundingRectangle().getWidth() + 10, 100);
        trapButton.setPosition(teleportButton.getX(), teleportButton.getY() - trapButton.getHeight() - 10);
        clearButton.setPosition(undo.getX() - undo.getBoundingRectangle().getWidth() - 10, undo.getY());
        undo.disable();
        redo.disable();
        teleportButton.setAutoUnpress(false);

        buttons.add(undo);
        buttons.add(redo);
        buttons.add(start);
        buttons.add(teleportButton);
        buttons.add(trapButton);
        buttons.add(clearButton);
    }

    private void initializeLabels() {
        Color fontColor = ColorLoader.colorMap.get("RESOURSES_LEFT_LABELS_COLOR");
        teleportsLeft = new Label(teleportsLeft()+"x", new Label.LabelStyle(Assets.getAssetLoader().font, fontColor)) {
            @Override
            public void act(float delta) {
                this.setText(teleportsLeft()+"x");
            }
        };
        teleportsLeft.setSize(teleportButton.getWidth() * 1.5f, teleportButton.getHeight());
        teleportsLeft.setPosition(teleportButton.getX()/* - teleportsLeft.getWidth() */, teleportButton.getY() + teleportButton.getHeight() / 2 + 5, Align.right);
        teleportsLeft.setFontScale(0.8f);
        teleportsLeft.setAlignment(Align.right);

        trapsLeft = new Label(trapsLeft()+"x", new Label.LabelStyle(Assets.getAssetLoader().font, fontColor)) {
            @Override
            public void act(float delta) {
                this.setText(trapsLeft()+"x");
            }
        };
        trapsLeft.setSize(trapButton.getWidth() * 1.5f, trapButton.getHeight());
        trapsLeft.setPosition(trapButton.getX()/* - trapsLeft.getWidth() */, trapButton.getY() + trapButton.getHeight() / 2 + 5, Align.right);
        trapsLeft.setFontScale(0.8f);
        trapsLeft.setAlignment(Align.right);
//        Gdx.app.log("teleportsLabel", teleportsLeft.getX() + " "+teleportsLeft.getY());
    }

    public void update(float dt){
        runTime += dt;
        commandHandler.update(dt);
        if(commandHandler.newCommands()) {
            commandHandler.executeCommandsGenerate();
        }
        world.step(1/60f, 6, 2);

        lerpCamera(gamePort.getWorldWidth() / 4, gamePort.getWorldHeight() / 4, dt);
        gameCam.update();

        mapRenderer.setView(gameCam);

        if(!initObjects) {
            Array<Command> commandArray = new Array<Command>();
            for (InteractiveObject interactiveObject : interactiveObjects) {
                if(ClassReflection.isInstance(Teleport.class, interactiveObject)) {
                    commandArray.add(new AddTeleportCommand(this, interactiveObject.getX(), interactiveObject.getY(), unpairedTeleport));
                } else { //trap
                    commandArray.add(new AddTrapCommand(this, interactiveObject.getX(), interactiveObject.getY()));
                }
                undo.enable();
            }
            commandHandler.addAllCommands(commandArray);
            initObjects = true;
        }
    }

    private void lerpCamera(float targetX, float targetY, float dt) {
        float lerp;
        lerp = 10f;
        Vector3 position = gameCam.position;
        position.x += (targetX - position.x) * lerp * dt;
        position.y += (targetY - position.y) * lerp * dt;
    }

    @Override
    public void render(float delta) {
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);
        game.batch.setProjectionMatrix(gameCam.combined);
        update(delta);

        Gdx.gl.glClearColor(BACKGROUND_COLOR.r,BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        renderUI();
        renderSelected();

        game.batch.begin();
        boolean lineDrawn = false;
        for (int i = 0; i < teleports.size; i++) {
            Teleport teleport = teleports.get(i);
            teleport.render(game.batch, delta);
            if(lineDrawn) {
                lineDrawn = false;
                continue;
            }
            if(teleport.getPartner() != null) {
                game.batch.end();
                drawPulsingDottedLine(shapeRenderer, 10, teleport.getX() + 8, teleport.getY() + 8, teleport.getPartner().getX() + 8, teleport.getPartner().getY() + 8, 0.5f, runTime);
                game.batch.begin();
                lineDrawn = true;
            }
        }
        for (Trap trap: traps) {
            trap.render(game.batch, delta);
        }
        for (Shard shard: shards) {
            shard.render(game.batch, delta);
        }

        player.getGameActor().draw(game.batch);
        if(lighting.lightsOn())
            enemyPlayer.getGameActor().draw(game.batch);
        game.batch.end();
//        LevelManager.graph.render(shapeRenderer);

    }

    public void renderUI() {
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        game.batch.draw(Assets.getAssetLoader().moon, Assets.getAssetLoader().moon.getWidth()-gamePort.getWorldWidth() / 4, -gameCam.position.y / 2);
        //buttons
        renderButtons(gameCam);
        renderLabels();
        if(game.batch.isDrawing())
            game.batch.end();
    }

    private void renderLabels() {
        teleportsLeft.act(0);
        teleportsLeft.draw(game.batch, 1f);
        trapsLeft.act(0);
        trapsLeft.draw(game.batch, 1f);
    }

    public void renderSelected() {
        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        if(selectedNode == null) {
            return;
        }
        if(state == State.DEFAULT) {
            shapeRenderer.setColor(DEFAULT);
        } else {
            if(selectedNode.getConnections().size > 0 && !existsObject()) {
                shapeRenderer.setColor(CORRECT);
            } else {
                shapeRenderer.setColor(WRONG);
            }
        }
        if(!shapeRenderer.isDrawing())
            shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(selectedNode.getWorldX() - LevelManager.tilePixelWidth / 2, selectedNode.getWorldY() - LevelManager.tilePixelHeight / 2,
                LevelManager.tilePixelWidth, LevelManager.tilePixelHeight);
        shapeRenderer.end();
    }

    public State getState() {
        return state;
    }

    public boolean anyObjects() {
        return teleports.size + traps.size > 0;
    }

    public int teleportsLeft() {
        return LevelManager.getAmountOfTeleports() - teleports.size;
    }

    public int trapsLeft() {
        return LevelManager.getAmountOfTraps() - traps.size;
    }

    public boolean maxTeleports() {
        if(teleports.size >= LevelManager.getAmountOfTeleports())
            return true;
        else
            return false;
    }

    public boolean maxTraps() {
        if(traps.size >= LevelManager.getAmountOfTraps())
            return true;
        else
            return false;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Button getUndo() {
        return undo;
    }

    public Button getRedo() {
        return redo;
    }

    public Button getStart() {
        return start;
    }

    public Button getClearButton() {
        return clearButton;
    }

    public Button getTeleportButton() {
        return teleportButton;
    }

    public Button getTrapButton() {
        return trapButton;
    }

    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void addTeleport() {
        if(selectedNode.getConnections().size > 0 && state == State.SETTING_TELEPORT && !existsObject()) {
            commandHandler.addCommandGenerate(new AddTeleportCommand(this, (int) selectedNode.getWorldX() - LevelManager.tilePixelWidth / 2, (int)selectedNode.getWorldY() - LevelManager.tilePixelHeight / 2, unpairedTeleport));
            undo.enable();
            state = State.DEFAULT;
            teleportButton.unpress();
        }
    }

    public Teleport getUnpairedTeleport() {
        return unpairedTeleport;
    }

    public void setUnpairedTeleport(Teleport unpairedTeleport) {
        this.unpairedTeleport = unpairedTeleport;
    }

    public void addTrap() {
        if(selectedNode.getConnections().size > 0 && state == State.SETTING_TRAP && !existsObject()) {
            commandHandler.addCommandGenerate(new AddTrapCommand(this, (int) selectedNode.getWorldX() - LevelManager.tilePixelWidth / 2, (int)selectedNode.getWorldY() - LevelManager.tilePixelHeight / 2));
            undo.enable();
            state = State.DEFAULT;
            trapButton.unpress();
        }
    }

    public boolean existsObject() {
            return existsTeleport((int) selectedNode.getWorldX() - LevelManager.tilePixelWidth / 2, (int)selectedNode.getWorldY() - LevelManager.tilePixelHeight / 2)
            || existsTrap((int) selectedNode.getWorldX() - LevelManager.tilePixelWidth / 2, (int)selectedNode.getWorldY() - LevelManager.tilePixelHeight / 2);
    }

    public boolean existsTeleport(int x, int y) {
        for (Teleport obj : teleports) {
            if((obj.getX() == x && obj.getY() == y)) {
                    return true;
               }
        }
        return false;
    }

    public boolean existsTrap(int x, int y) {
        for (Trap obj : traps) {
            if(obj.getX() == x && obj.getY() == y) {
                    return true;
            }
        }
        return false;
    }

    public EnemyPlayer getEnemyPlayer() {
        return enemyPlayer;
    }

    public Player getPlayer() {
        return player;
    }

    public OrthographicCamera getGameCam() {
        return gameCam;
    }

    public Viewport getGamePort() {
        return gamePort;
    }

    public LightSwitch getGame() {
        return game;
    }

    public Array<Teleport> getTeleports() {
        return teleports;
    }

    public Array<Trap> getTraps() {
        return traps;
    }

    public Array<Shard> getShards() {
        return shards;
    }

    public AssetLoader getLoader() {
        return loader;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public OrthogonalTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public World getWorld() {
        return world;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
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

    public Lighting getLighting() {
        return lighting;
    }

}
