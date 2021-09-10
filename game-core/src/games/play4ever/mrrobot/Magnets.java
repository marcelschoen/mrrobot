package games.play4ever.mrrobot;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages magents pulling Mr. Robot in a direction.
 *
 * @author Marcel Schoen
 */
public class Magnets {

    public static List<Sprite> leftMagnets = new ArrayList<>();
    public static List<Sprite> rightMagnets = new ArrayList<>();

    private final static float DISTANCE_VERTICAL = 20;
    private final static float DISTANCE_HORIZONTAL = 50;

    public static void reset() {
        leftMagnets = new ArrayList<>();
        rightMagnets = new ArrayList<>();
    }

    public static void addMagnetLeft(Sprite magnetSprite) {
        leftMagnets.add(magnetSprite);
    }

    public static void addMagnetRight(Sprite magnetSprite) {
        rightMagnets.add(magnetSprite);
    }

    public static boolean isMagnetLeftClose(float xPos, float yPos) {
        for(Sprite leftMagnet : leftMagnets) {
            if(xPos < leftMagnet.getX()
                    && leftMagnet.getX() - xPos < DISTANCE_HORIZONTAL
                    && Math.abs((leftMagnet.getY() + 8) - yPos) < DISTANCE_VERTICAL) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMagnetRightClose(float xPos, float yPos) {
        for(Sprite rightMagnet : rightMagnets) {
            if(xPos > rightMagnet.getX()
                    && xPos - rightMagnet.getX() < DISTANCE_HORIZONTAL
                    && Math.abs((rightMagnet.getY() + 8) - yPos) < DISTANCE_VERTICAL) {
                return true;
            }
        }
        return false;
    }
}
