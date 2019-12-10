package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jplay.gdx.Assets;

public class GamepadOverlay {

    public static final int OVERLAY_WIDTH = 230;
    public static final int OVERLAY_HEIGHT = 230;

    private static Texture gamepad_overlay = null;

    private static SpriteBatch batch;
    private static OrthographicCamera camera;
    private static Viewport viewport;
    private static ShapeRenderer shapeRenderer;

    public static void initialize() {
        camera = new OrthographicCamera(OVERLAY_WIDTH, OVERLAY_WIDTH);
        camera.setToOrtho(false, OVERLAY_WIDTH, OVERLAY_WIDTH);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        viewport = new FitViewport(OVERLAY_WIDTH, OVERLAY_HEIGHT, camera);
        viewport = new ScalingViewport(Scaling.none, OVERLAY_WIDTH, OVERLAY_HEIGHT, camera);
        gamepad_overlay = Assets.instance().getTexture(MrRobotAssets.TEXTURE_ID.GAMEPAD_OVERLAY);
        shapeRenderer = new ShapeRenderer();
    }

    public static void resize(int width, int height) {
        if(camera == null) {
            initialize();
        }
        camera.update();
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    public static void draw() {
        Gdx.gl.glViewport(0, 0, OVERLAY_WIDTH ,OVERLAY_HEIGHT);
        camera.update();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(0, 0, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        shapeRenderer.end();

        batch.begin();
        if (gamepad_overlay != null) {
            batch.draw(gamepad_overlay, 0, 0);
        }
        batch.end();
    }
}
