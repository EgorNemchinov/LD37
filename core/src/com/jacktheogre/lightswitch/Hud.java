package com.jacktheogre.lightswitch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by luna on 20.10.16.
 */
public class Hud implements Disposable{

    public Stage stage;
    private Viewport viewport;

    private static int score;

    private Label scoreLabel;
    private Label actorLabel;

    public Hud(SpriteBatch sb) {
        score = 0;

        viewport = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT);
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.setFillParent(true);
        table.bottom();

        scoreLabel = new Label(String.format("SCORE: %03d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        actorLabel = new Label(String.format("PLAYER: %s", "-"), new Label.LabelStyle(new BitmapFont(), Color.GREEN));

//        table.add(scoreLabel).expandX().padBottom(10);
        table.add(actorLabel).expandX().padBottom(10);

        stage.addActor(table);
    }

    public void addScore(int addition) {
        score += addition;
        scoreLabel.setText(String.format("SCORE: %03d", score));
    }

    public void setActor(String actor) {
        actorLabel.setText(String.format("PLAYER: %s", actor));
    }

    public void dispose() {

    }
}
