package ch.marcelschoen.mrrobot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.marcelschoen.mrrobot.MrRobotGame;
import ch.marcelschoen.mrrobot.scenes.Hud;

public class PlayScreen implements Screen {

    // Map is 40x22 with 8x8 pixel tiles
    public static int VIRTUAL_WIDTH = 320;
    public static int VIRTUAL_HEIGHT = 176;

    private MrRobotGame game;
    private Viewport gamePort = null;
    private OrthographicCamera camera = null;
    private Hud hud;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private TmxMapLoader loader;

    private BitmapFont font;

    private World world;
    private Box2DDebugRenderer b2dr;

    //private Texture mrrobotSprite;

    public PlayScreen(MrRobotGame game) {
        this.game = game;

        this.world = new World(new Vector2(0,0), true); // world without gravity
        b2dr = new Box2DDebugRenderer();

        font = new BitmapFont(Gdx.files.internal("fonts/font1.fnt"),
                Gdx.files.internal("fonts/font1.png"), false);
        this.camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        this.camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0f);

        this.gamePort = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, this.camera);
        //this.gamePort = new ScreenViewport(this.camera);

        this.loader = new TmxMapLoader();
        this.map = loader.load("map/level1_large.tmx");
        this.renderer = new OrthogonalTiledMapRenderer(map);

        System.out.println("------------------------ BEGIN -----------------------------");
        System.out.println("> Layers: " + this.map.getLayers().getCount());
        for(MapLayer layer : this.map.getLayers()) {
            System.out.println("--> layer: " + layer.getName() + " / " + layer.getClass().getName());
            if(layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer)layer;
                System.out.println("==>> layer size: " + tiledMapTileLayer.getWidth() + " by " + tiledMapTileLayer.getHeight());
                System.out.println("==>> tile 0,0: " + tiledMapTileLayer.getCell(0,0).getTile().getId());
                for(int lineCt = tiledMapTileLayer.getHeight() - 1; lineCt > -1; lineCt --) {
                    String line = "";
                    for(int colCt = 0; colCt < tiledMapTileLayer.getWidth(); colCt ++) {
                        TiledMapTileLayer.Cell cell = tiledMapTileLayer.getCell(colCt, lineCt);
                        if(cell == null) {
                            line += "-";
                        } else {
                            TiledMapTile tile = tiledMapTileLayer.getCell(colCt, lineCt).getTile();
                            line += tile.getId();
                            if(tile.getId() == 1 || tile.getId() == 2 || tile.getId() == 3 || tile.getId() == 4) {
                                // generate polygon for box2d collision detection
                                BodyDef bdef = new BodyDef();
                                bdef.type = BodyDef.BodyType.StaticBody;
                                bdef.position.set(colCt * 8, lineCt * 8);

                                PolygonShape shape = new PolygonShape();
                                shape.setAsBox(8, 8);

                                FixtureDef fdef = new FixtureDef();
                                fdef.shape = shape;

                                Body body = world.createBody(bdef);
                                body.createFixture(fdef);
                            }
                        }
                    }
                    System.out.println("==>> " + lineCt + ": " + line);
                }
            }
            System.out.println("-->> Objects in layer: " + this.map.getLayers().get(0).getObjects().getCount());

            for(MapObject obj : this.map.getLayers().get(0).getObjects()) {
                System.out.println("> obj: " + obj.getName() + " / " + obj.getClass().getName());
            }
        }

        this.hud = new Hud(game.spriteBatch, this.font);

        //this.mrrobotSprite = new Texture("sprite_mrrobot1.png");
    }

    @Override
    public void show() {
    }

    public void handleInput(float dt) {
        if(Gdx.input.isTouched()) {
            camera.position.x += 100 * dt;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {

        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= 100 * dt;
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += 100 * dt;
        }
    }

    public void update(float dt) {
        handleInput(dt);
        this.camera.update();
        this.renderer.setView(this.camera);
    }

    @Override
    public void render(float delta) {
        try {
            update(delta);

            Gdx.gl.glClearColor(0f, 0f, 0f,1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderer.render();
/*
            game.spriteBatch.setProjectionMatrix(this.camera.combined);
            game.spriteBatch.begin();
//            game.spriteBatch.draw(this.mrrobotSprite, 10, 10);
            game.spriteBatch.end();
*/
            game.spriteBatch.setProjectionMatrix(hud.stage.getCamera().combined);

            hud.stage.draw();
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height, true);
/*
        gamePort.update(Gdx.graphics.getWidth() - 80, Gdx.graphics.getHeight() - 80, true);
        System.out.println("w: " + (Gdx.graphics.getWidth() - 80));
        gamePort.setScreenBounds(80, 80, Gdx.graphics.getWidth() - 160, Gdx.graphics.getHeight() - 160);
        gamePort.apply();
        gamePort.update(width, height);
 */
//        gamePort.setScreenPosition(40, 40);
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
