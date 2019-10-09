package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainScreen implements Screen {

    private int VIRTUAL_WIDTH = 320;
    private int VIRTUAL_HEIGHT = 200;

    private Game game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera = null;
    private Viewport viewport = null;
    private TmxMapLoader loader;

    public MainScreen(Game game) {
        this.game = game;
        this.camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        this.viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, this.camera);
        this.loader = new TmxMapLoader();
        this.map = loader.load("map/level1.tmx");
        this.renderer = new OrthogonalTiledMapRenderer(map);
System.out.println("World width: " + this.viewport.getWorldWidth() + " / height: " + this.viewport.getWorldHeight());
//        camera.position.set(this.viewport.getWorldHeight() / 2, this.viewport.getWorldHeight() / 2, 0f);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0f);
    }

    @Override
    public void show() {
        try {
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    public void update(float dt) {

    }

    @Override
    public void render(float delta) {
        try {
            Gdx.gl.glClearColor(0,0,0,1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            renderer.setView(camera);
            renderer.render();
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = VIRTUAL_WIDTH;
        camera.viewportHeight = VIRTUAL_HEIGHT;
        /*
        viewport.update(width, height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
         */
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {

    }
}
