package games.play4ever.mrrobot;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import games.play4ever.libgdx.Assets;
import games.play4ever.libgdx.sprites.AnimatedSprite;

public class Hud {

    private static final int MAX_LIVES = 9;
    private static final int START_LIVES = 3;

    private static int score = 0;
    private static int highScore = 0;
    private static int lives = START_LIVES;

    private static AnimatedSprite magnetSprite = null;

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
        if(score > highScore) {
            highScore = score;
        }
        score = 0;
        lives = START_LIVES;
    }

    public static void hideMagnet() {
        if(magnetSprite != null) {
            magnetSprite.setVisible(false);
        }
        magnetSprite = null;
    }

    public static void showMagnet(AnimatedSprite magnetSprite) {
        if(Hud.magnetSprite != null) {
            Hud.magnetSprite.setVisible(false);
        }
        Hud.magnetSprite = magnetSprite;
    }

    public static void addScore(int value) {
        score += value;
    }

    public static void removeLive() { lives--; }

    public static boolean hasLessThanZeroLivesLeft() {
        return lives < 0;
    }

    public static void addLive() {
        if(lives < MAX_LIVES) {
            lives++;
        }
    }

    public static void doRender(SpriteBatch batch, float delta) {
        BitmapFont font = Assets.instance().getFont(MrRobotAssets.FONT_ID.LOADING);
        font.draw(batch, "SCORE: " + score, 2f, 150f);
        font.draw(batch, "HIGHSCORE: " + highScore, 120f, 150f);
        font.draw(batch, "LIVES: " + (lives > -1 ? lives : 0), 260f, 150f);
        if(magnetSprite != null) {
            magnetSprite.setPosition(80, 160);
            magnetSprite.draw(batch, delta);
        }
    }
}
