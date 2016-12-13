package com.jacktheogre.lightswitch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.screens.PlayScreen;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 20.10.16.
 */
public class Hud implements Disposable{

    public Stage stage;
    private Viewport viewport;

    private Label timeLabel;
    private PlayScreen screen;
    private Sprite scale, fill;

    public Hud(PlayScreen screen) {
        this.screen = screen;
        scale = new Sprite(Assets.getAssetLoader().scale);
        scale.setPosition(scale.getWidth() / 2, 20);
        scale.setScale(1f);
        fill = new Sprite(Assets.getAssetLoader().scale_fill);
        fill.setPosition(scale.getX() + 2, scale.getY() + 2);
        fill.setSize(scale.getWidth() - 4, scale.getHeight() - 4);
        fill.setOrigin(fill.getWidth() / 2, 0);
        viewport = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT);
        stage = new Stage(viewport, screen.getGame().batch);

        Table table = new Table();
        table.setFillParent(true);
        table.top();

//        timeLabel = new Label(String.format("SCORE: %03d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label(String.format("%d", (int) Constants.PLAYTIME), new Label.LabelStyle(new BitmapFont(), Color.GOLD));
        timeLabel.setFontScale(1.4f);
//        actorLabel = new Label(String.format(": %s", "-"), new Label.LabelStyle(new BitmapFont(), Color.GREEN));

//        table.add(timeLabel).expandX().padBottom(10);
        table.add(timeLabel).align(Align.right).padTop(10);

        stage.addActor(table);
    }

    public void render() {
        update();
        stage.draw();
        screen.getGame().batch.begin();
        screen.getGame().batch.setProjectionMatrix(stage.getCamera().combined);
        fill.draw(screen.getGame().batch);
        scale.draw(screen.getGame().batch);
        screen.getGame().batch.end();
    }

//    public void addScore(int addition) {
//        score += addition;
//        timeLabel.setText(String.format("SCORE: %03d", score));
//    }

    public void update() {
        timeLabel.setText(String.format("%d", (int)(Constants.PLAYTIME - screen.getRunTime())));
        fill.setScale(scale.getScaleX(), scale.getScaleY()*screen.getEnergy()/100f);
    }

//    public void setActor(String actor) {
//        actorLabel.setText(String.format("PLAYER: %s", actor));
//    }



    public void dispose() {

    }
}
