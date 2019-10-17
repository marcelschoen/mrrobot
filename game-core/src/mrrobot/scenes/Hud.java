package ch.marcelschoen.mrrobot.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.marcelschoen.mrrobot.BitmapText;
import ch.marcelschoen.mrrobot.screens.PlayScreen;

public class Hud {
    public Stage stage;
    private Viewport viewport;
    private BitmapFont font;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label mrRobotLabel;

    public Hud(SpriteBatch spriteBatch, BitmapFont font) {
        this.font = font;

        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(PlayScreen.VIRTUAL_WIDTH, PlayScreen.VIRTUAL_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        mrRobotLabel = new Label("MR.ROBOT", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(mrRobotLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(new BitmapText(font, "MR. ROBOT BY MARCEL SCHOEN"));

        stage.addActor(table);
    }
}
