package games.play4ever.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Handles persistent data storage (highscore, unlocked levels)
 *
 * @author Marcel Schoen
 */
public class GameDataStore {

    private static int lastUnlockedLevel = 1;
    private static int highScore = 0;

    private static Preferences preferences = Gdx.app.getPreferences("mrrobot");

    public static int getLastUnlockedLevel() {
        return lastUnlockedLevel;
    }
    public static void setLastUnlockedLevel(int lastUnlockedLevel) {
        GameDataStore.lastUnlockedLevel = lastUnlockedLevel;
    }

    public static int getHighScore() {
        return highScore;
    }
    public static void setHighScore(int highScore) {
        GameDataStore.highScore = highScore;
    }

    public static void load() {
        GameDataStore.highScore = preferences.getInteger("highscore", 0);
        GameDataStore.lastUnlockedLevel = preferences.getInteger("lastUnlockedMapIndex", 0);
    }
    public static void persist() {
        preferences.putInteger("lastUnlockedMapIndex", TileMap.getLastUnlockedMapIndex());
        preferences.putInteger("highscore", Hud.getHighScore());
        preferences.flush();
    }
}
