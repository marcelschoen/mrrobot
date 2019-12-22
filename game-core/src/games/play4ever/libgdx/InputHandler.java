package games.play4ever.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

/**
 * Processes Gdx.gl input and dispatches it to InputListeners. While this is basically a
 * redundancy of the LIBGDX InputProcessor, the reason for this class is to provide easier
 * input handling and get rid of issues caused by the fact that only one InputProcessor can be
 * set in LIBGDX at any point.
 *
 * @author Marcel Schoen
 */
public class InputHandler {

    /** Holds the touch coordinates. */
    private static Vector3 touchPoint = new Vector3();

    /**
     * Must be invoked once during the render function, and it will then
     * notify all the registered listeners.
     *
     * @param camera Optional: If a camera is provided, touch input coordinates will be transformed.
     */
    public static void handleInput(Camera camera) {
        if(Gdx.input.isTouched()) {
            if(camera != null) {

            }
            transformTouchCoordinates(Gdx.input.getX(), Gdx.input.getY(), camera);
            System.out.println("*TOUCH*" + touchPoint.x + "," + touchPoint.y);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
        }
    }

    private static void transformTouchCoordinates(int screenX, int screenY, Camera camera) {
        touchPoint = camera.unproject(touchPoint.set(screenX, screenY, 0));
        touchPoint.x = (int)touchPoint.x;
        touchPoint.y = (int)touchPoint.y;
    }
}
