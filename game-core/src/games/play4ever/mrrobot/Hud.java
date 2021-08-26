package games.play4ever.mrrobot;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import games.play4ever.libgdx.Assets;

public class Hud {

    private static final int MAX_LIVES = 1;

    private static int score = 0;
    private static int highScore = 0;
    private static int lives = MAX_LIVES;

    public static int getHighScore() {
        return highScore;
    }

    public static int getScore() {
        return score;
    }

    public static void setHighScore(int highScore) {
        Hud.highScore = highScore;
    }

    public static void resetScoreAndLives() {
        highScore = score;
        score = 0;
        lives = MAX_LIVES;
    }

    public static void addScore(int value) {
        score += value;
    }

    public static void removeLive() { lives--; }

    public static boolean hasLessThanZeroLivesLeft() {
        return lives < 0;
    }

    public static void addLive() { lives++; }

    public static void doRender(SpriteBatch batch, float delta) {
        BitmapFont font = Assets.instance().getFont(MrRobotAssets.FONT_ID.LOADING);
        font.draw(batch, "SCORE: " + score, 2f, 150f);
        font.draw(batch, "HIGHSCORE: " + highScore, 120f, 150f);
        font.draw(batch, "LIVES: " + lives, 300f, 150f);
    }
}
