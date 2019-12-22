package games.play4ever.mrrobot;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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



    private static Texture gamepad_overlay = null;

    private static SpriteBatch batch;
    private static OrthographicCamera camera;
    private static Viewport viewport;
    private static ShapeRenderer shapeRenderer;

    public static void initialize() {
        int width = ScreenUtil.getVirtualResolution().getWidth();
        int height = ScreenUtil.getVirtualResolution().getHeight();
        camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false, width, height);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        viewport = new FitViewport(width, height, camera);
        stage = new Stage(viewport, batch);
        //viewport = new ScalingViewport(Scaling.none, OVERLAY_WIDTH, OVERLAY_HEIGHT, camera);
        gamepad_overlay = Assets.instance().getTexture(MrRobotAssets.TEXTURE_ID.GAMEPAD_OVERLAY);
        shapeRenderer = new ShapeRenderer();

        Table table = new Table();
        table.bottom();
        table.setFillParent(true);
        table.add(new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.draw(gamepad_overlay, 0, 0, gamepad_overlay.getWidth(), gamepad_overlay.getHeight());
            }
        });
        stage.addActor(table);


//        Gdx.input.setInputProcessor();
    }

    public static void resize(int width, int height) {
        if(camera == null) {
            initialize();
        }
        camera = new OrthographicCamera(OVERLAY_WIDTH, OVERLAY_WIDTH);
        camera.setToOrtho(false, OVERLAY_WIDTH, OVERLAY_WIDTH);
        batch.setProjectionMatrix(camera.combined);
    }

    public static void draw() {
        batch.setProjectionMatrix(stage.getCamera().combined);
        stage.draw();
    }
}
