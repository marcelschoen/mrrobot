package games.play4ever.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import games.play4ever.libgdx.Assets;
import games.play4ever.libgdx.screens.ScreenUtil;

public class GamepadOverlay {

    public static final int OVERLAY_WIDTH = 230;
    public static final int OVERLAY_HEIGHT = 230;

    public static Stage stage;
    private static Vector3 touchPointLeft = new Vector3();
    private static Vector3 touchPointRight = new Vector3();
    private static Vector3 touchPadCenter = new Vector3();
    private static Rectangle touchPadBounds = null;
    private static Rectangle touchButtonBounds = null;
    private static boolean touchLeft = false;
    private static boolean touchRight = false;

    public static boolean isLeftPressed = false;
    public static boolean isUpPressed = false;
    public static boolean isDownPressed = false;
    public static boolean isRightPressed = false;
    public static boolean isJumpPressed = false;

    private static Texture gamepad_overlay = null;
    private static Texture gamepad_overlay_button = null;

    private static SpriteBatch batch;
    private static OrthographicCamera camera;
    private static Viewport viewport;
    private static ShapeRenderer shapeRenderer;

    private static boolean active = false;

    public static void initialize() {
        if(Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard)) {
            return;
        }
        if(System.getProperty("desktop") != null) {
            return;
        }
        active = true;
        int width = ScreenUtil.getVirtualResolution().getWidth();
        int height = ScreenUtil.getVirtualResolution().getHeight();
        camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false, width, height);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        viewport = new FitViewport(width, height, camera);
        stage = new Stage(viewport, batch);
        //viewport = new ScalingViewport(Scaling.none, OVERLAY_WIDTH, OVERLAY_HEIGHT, camera);

        shapeRenderer = new ShapeRenderer();

        gamepad_overlay = Assets.instance().getTexture(MrRobotAssets.TEXTURE_ID.GAMEPAD_OVERLAY);
        gamepad_overlay_button = Assets.instance().getTexture(MrRobotAssets.TEXTURE_ID.GAMEPAD_OVERLAY_BUTTON);
        touchPadBounds = new Rectangle(0, 0, gamepad_overlay.getWidth(), gamepad_overlay.getHeight());
        touchButtonBounds = new Rectangle(ScreenUtil.getVirtualResolution().getWidth() - gamepad_overlay_button.getWidth(), 0, gamepad_overlay_button.getWidth(), gamepad_overlay_button.getHeight());
        touchPadCenter.set(gamepad_overlay.getWidth() / 2, gamepad_overlay.getHeight() / 2, 0);
        Table table = new Table();
        table.bottom();
        table.setFillParent(true);
        table.add(new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.draw(gamepad_overlay, 0, 0, gamepad_overlay.getWidth(), gamepad_overlay.getHeight());
                batch.draw(gamepad_overlay_button, ScreenUtil.getVirtualResolution().getWidth() - gamepad_overlay_button.getWidth(),
                        0, gamepad_overlay_button.getWidth(), gamepad_overlay_button.getHeight());
            }
        });
        stage.addActor(table);
    }

    public static void resize(int width, int height) {
        if(!active) {
            return;
        }
        if(camera == null) {
            initialize();
        }
        int camWidth = ScreenUtil.getVirtualResolution().getWidth();
        int camHeight = ScreenUtil.getVirtualResolution().getHeight();
        camera = new OrthographicCamera(camWidth, camHeight);
        camera.setToOrtho(false, camWidth, camHeight);
        batch.setProjectionMatrix(camera.combined);
    }

    public static void draw() {
        if(!active) {
            return;
        }

        handleInput();
        batch.setProjectionMatrix(stage.getCamera().combined);
        stage.draw();

        if(isLeftPressed || isRightPressed || isUpPressed || isDownPressed) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.circle(touchPointLeft.x, touchPointLeft.y, 3);
            shapeRenderer.end();
        }
        if(isJumpPressed) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle(touchPointRight.x, touchPointRight.y, 3);
            shapeRenderer.end();
        }
    }

    private static void handleInput() {
        if(MrRobot.instance.getState().isInputBlocked()) {
            return;
        }
        touchLeft = false;
        touchRight = false;
        for(int finger=0; finger<2; finger++) {
            if (Gdx.input.isTouched(finger)) {
                transformTouchCoordinates(Gdx.input.getX(finger), Gdx.input.getY(finger), camera);
            }
        }
        isLeftPressed = false;
        isRightPressed = false;
        isUpPressed = false;
        isDownPressed = false;
        isJumpPressed = false;
        if(touchLeft && touchPointLeft.x < touchPadBounds.width && touchPointLeft.y < touchPadBounds.height) {
            float angle = getAngle(touchPointLeft, touchPadCenter);
            if((angle > 0 && angle < 46) || (angle > 315 && angle < 360)) {
                isLeftPressed = true;
            } else if(angle > 45 && angle < 136) {
                isDownPressed = true;
            } else if(angle > 135 && angle < 226) {
                isRightPressed = true;
            } else if(angle > 225 && angle < 315) {
                isUpPressed = true;
            }
        }
        if(touchRight && touchPointRight.x > touchButtonBounds.x && touchPointRight.y < touchButtonBounds.height) {
            isJumpPressed = true;
        }
    }

    private static void transformTouchCoordinates(int screenX, int screenY, Camera camera) {
        if(screenX < ScreenUtil.getScreenResolution().getWidth() / 2) {
            touchLeft = true;
            touchPointLeft = camera.unproject(touchPointLeft.set(screenX, screenY, 0));
        } else {
            touchRight = true;
            touchPointRight = camera.unproject(touchPointRight.set(screenX, screenY, 0));
        }
    }

    private static float getAngle(Vector3 source, Vector3 target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - source.y, target.x - source.x));
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }
}
