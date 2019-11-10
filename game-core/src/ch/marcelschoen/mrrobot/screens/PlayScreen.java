package ch.marcelschoen.mrrobot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.jplay.gdx.Assets;
import com.jplay.gdx.DebugOutput;
import com.jplay.gdx.MoveableEntity;
import com.jplay.gdx.screens.AbstractBaseScreen;
import com.jplay.gdx.screens.ScreenUtil;

import ch.marcelschoen.mrrobot.MrRobotAssets;
import ch.marcelschoen.mrrobot.MrRobotGame;

public class PlayScreen extends AbstractBaseScreen /*implements TweenCallback*/ {

    private TiledMap map;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    private TmxMapLoader loader;

    private MoveableEntity mrRobot = new MoveableEntity("mrrobot", 0, 0);

    private boolean initialized = false;

    private enum MRROBOT_STATE {
        STANDING_RIGHT(true),
        STANDING_LEFT(false),
        WALKING_RIGHT(true),
        WALKING_LEFT(false);

        private boolean isFacingRight = true;

        MRROBOT_STATE(boolean isFacingRight) {
            this.isFacingRight = isFacingRight;
        }

        public boolean isFacingRight() {
            return this.isFacingRight;
        }
    }
    private MRROBOT_STATE mrRobotState = MRROBOT_STATE.STANDING_RIGHT;

    /**
     *
     * @param game
     */
    public PlayScreen(MrRobotGame game) {
        super(game, null);

        this.loader = new TmxMapLoader();
        this.map = loader.load("map/level16.tmx");
        this.tileMapRenderer = new OrthogonalTiledMapRenderer(map);

        this.mrRobot.setSprite("mrrobot_stand_right", Assets.instance().getJPlaySprite("mrrobot_stand_right"));
        this.mrRobot.setSprite("mrrobot_walk_right", Assets.instance().getJPlaySprite("mrrobot_walk_right"));
        this.mrRobot.setSprite("mrrobot_stand_left", Assets.instance().getJPlaySprite("mrrobot_stand_left"));
        this.mrRobot.setSprite("mrrobot_walk_left", Assets.instance().getJPlaySprite("mrrobot_walk_left"));
        //this.mrRobotSprite = Assets.instance().getJPlaySprite("mrrobot_walk_right");

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
                                // tile with point to collect

                            }
                            if(tile.getId() == 18) {
                                System.out.println("Found mrrobot at: " + colCt + "," + lineCt);
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                // Placement of Mr. Robot starting position
                                float x = (colCt * 8) - 8;
                                float y = lineCt * 8;
                                mrRobot.setPosition(x, y);
                                mrRobot.setState("mrrobot_stand_right");
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
    }

    private void handleMrRobot() {
        float x = mrRobot.getX();
        float y = mrRobot.getY();
        if(mrRobotState == MRROBOT_STATE.WALKING_RIGHT) {
            mrRobot.setPosition(x + 0.5f, y);
        } else if(mrRobotState == MRROBOT_STATE.WALKING_LEFT) {
            mrRobot.setPosition(x - 0.5f, y);
        }

    }

    public void handleInput(float dt) {
        if(Gdx.input.isTouched()) {
            camera.position.x += 100 * dt;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            ScreenUtil.dispose();
            System.exit(-1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camera.position.x -= 100 * dt;
            } else {
                if(mrRobotState != MRROBOT_STATE.WALKING_LEFT) {
                    mrRobot.setState("mrrobot_walk_left");
                    mrRobotState = MRROBOT_STATE.WALKING_LEFT;
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camera.position.x += 100 * dt;
            } else {
                if(mrRobotState != MRROBOT_STATE.WALKING_RIGHT) {
                    mrRobot.setState("mrrobot_walk_right");
                    mrRobotState = MRROBOT_STATE.WALKING_RIGHT;
                }
            }
        } else {
            // Stop movement
            if(mrRobotState.isFacingRight) {
                if(mrRobotState != MRROBOT_STATE.STANDING_RIGHT) {
                    mrRobot.setState("mrrobot_stand_right");
                    mrRobotState = MRROBOT_STATE.STANDING_RIGHT;
                }
            } else {
                if(mrRobotState != MRROBOT_STATE.STANDING_LEFT) {
                    mrRobot.setState("mrrobot_stand_left");
                    mrRobotState = MRROBOT_STATE.STANDING_LEFT;
                }
            }
        }

        handleMrRobot();
    }

    @Override
    public void show() {
        MrRobotAssets.stopMenuMusic();
    }

    /* (non-Javadoc)
     * @see com.jplay.gdx.screens.AbstractBaseScreen#doRender(float)
     */
    @Override
    public void doRender(float delta) {
        handleInput(delta);
        this.camera.update();
        this.tileMapRenderer.setView((OrthographicCamera)this.camera);

        tileMapRenderer.render();

        batch.begin();
        if(!initialized) {
            initialized = true;
            DebugOutput.initialize(Assets.instance().getFont(MrRobotAssets.FONT_ID.DEBUG));
        }

        this.mrRobot.draw(batch, delta);

        // get cell
        int cellX = ((int)this.mrRobot.getX() + 8) / 8;
        int cellY = (int)this.mrRobot.getY() / 8;
        //System.out.println("CELL: " + cellX + "," + cellY);

        // print debug stuff on screen
        // TODO: Enable only in testing / debug mode
        DebugOutput.draw(batch);

        BitmapFont font = Assets.instance().getFont(MrRobotAssets.FONT_ID.LOADING);
        font.draw(batch, "SCORE: 0", 2f, 140f);


        batch.end();
    }
}
