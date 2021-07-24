package games.play4ever.mrrobot;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import games.play4ever.libgdx.Assets;

public class Hud {

    private static int score = 0;

    private static int lives = 3;

    public static void setScore(int value) {
        score = value;
    }

    public static void addScore(int value) {
        score += value;
    }

    public static void removeLive() { lives--; }

    public static void addLive() { lives++; }

    public static void doRender(SpriteBatch batch, float delta) {
        BitmapFont font = Assets.instance().getFont(MrRobotAssets.FONT_ID.LOADING);
        font.draw(batch, "SCORE: " + score, 2f, 150f);
        font.draw(batch, "LIVES: " + lives, 120f, 150f);
    }
}
