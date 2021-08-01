package games.play4ever.mrrobot;

import com.badlogic.gdx.Gdx;
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
            if(xPos < leftMagnet.getX() && leftMagnet.getX() - xPos < 80 && Math.abs(leftMagnet.getY() - yPos) < 50) {
                Gdx.app.log("Magnets", "*** PULL TO RIGHT ***");
                return true;
            }
        }
        return false;
    }

    public static boolean isMagnetRightClose(float xPos, float yPos) {
        for(Sprite rightMagnet : rightMagnets) {
            if(xPos > rightMagnet.getX() && xPos - rightMagnet.getX() < 80 && Math.abs(rightMagnet.getY() - yPos) < 50) {
                Gdx.app.log("Magnets", "*** PULL TO LEFT ***");
                return true;
            }
        }
        return false;
    }
}
