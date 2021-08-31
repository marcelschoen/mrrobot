package games.play4ever.mrrobot;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteUtil {

    /**
     * @return True if the sprite is nearly vertically aligned with his feet (lower sprite boundary).
     */
    public static boolean isAlignedVertically(Sprite sprite) {
        float y = sprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return Math.abs(diff) < 0.5f;
    }

    /**
     * @return True if the sprite is nearly vertically aligned with his head (upper sprite boundary).
     */
    public static boolean isNearlyAlignedVerticallyAtTop(Sprite sprite) {
        float y = sprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return diff > 1.5f;
    }

    /**
     * @return True if the sprite is nearly aligned horizontally (center of sprite).
     */
    public static boolean isNearlyAlignedHorizontally(Sprite sprite) {
        float x = sprite.getX() + 6f;
        int col = (int)(x / 8f) + 1;
        float diff = x - (col * 8f);
        return Math.abs(diff) < 8f;
    }

}
