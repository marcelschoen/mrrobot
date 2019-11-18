package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jplay.gdx.Assets;

public class Hud {

    private static int score = 0;

    public static void setScore(int value) {
        score = value;
    }

    public static void addScore(int value) {
        score += value;
    }

    public static void doRender(SpriteBatch batch, float delta) {
        BitmapFont font = Assets.instance().getFont(MrRobotAssets.FONT_ID.LOADING);
        font.draw(batch, "SCORE: " + score, 2f, 150f);
    }
}
