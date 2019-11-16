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

    public static final int TILE_DOT = 2;
    public static final int TILE_SLIDER = 17;

    private static final float WALK_SPEED = 32;
    private static final float DOWN_SPEED = 24;

    private TiledMap map;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    private TmxMapLoader loader;

    private MoveableEntity mrRobot = new MoveableEntity("mrrobot", 0, 0);

    private TiledMapTileLayer tiledMapTileLayer;
    private TiledMapTile clearedFloor = null;

    private boolean initialized = false;

    private enum MRROBOT_STATE {
        SLIDING_RIGHT(true),
        SLIDING_LEFT(false),
        FALLING_RIGHT(true),
        FALLING_LEFT(false),
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

        this.mrRobot.setSprite("mrrobot_stand_right", Assets.instance().getAnimated2DSprite("mrrobot_stand_right"));
        this.mrRobot.setSprite("mrrobot_walk_right", Assets.instance().getAnimated2DSprite("mrrobot_walk_right"));
        this.mrRobot.setSprite("mrrobot_stand_left", Assets.instance().getAnimated2DSprite("mrrobot_stand_left"));
        this.mrRobot.setSprite("mrrobot_walk_left", Assets.instance().getAnimated2DSprite("mrrobot_walk_left"));
        this.mrRobot.setSprite("mrrobot_climb", Assets.instance().getAnimated2DSprite("mrrobot_climb"));
        //this.mrRobotSprite = Assets.instance().getJPlaySprite("mrrobot_walk_right");

        this.clearedFloor = this.map.getTileSets().getTileSet(0).getTile(3);
        System.out.println("------------------------ BEGIN -----------------------------");
        System.out.println("> Layers: " + this.map.getLayers().getCount());
        for(MapLayer layer : this.map.getLayers()) {
            System.out.println("--> layer: " + layer.getName() + " / " + layer.getClass().getName());
            if(layer instanceof TiledMapTileLayer) {
                this.tiledMapTileLayer = (TiledMapTileLayer)layer;
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

    public void handleInput(float delta) {
        if(Gdx.input.isTouched()) {
            camera.position.x += 100 * delta;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            ScreenUtil.dispose();
            System.exit(-1);
        }
        MRROBOT_STATE intendedState = null;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camera.position.x -= 100 * delta;
            } else {
                if(mrRobotState != MRROBOT_STATE.WALKING_LEFT) {
                    mrRobot.setState("mrrobot_walk_left");
                    intendedState = MRROBOT_STATE.WALKING_LEFT;
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camera.position.x += 100 * delta;
            } else {
                if(mrRobotState != MRROBOT_STATE.WALKING_RIGHT) {
                    mrRobot.setState("mrrobot_walk_right");
                    intendedState = MRROBOT_STATE.WALKING_RIGHT;
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mrRobot.setState("mrrobot_climb");
        } else {
            // Stop movement
            if(mrRobotState.isFacingRight) {
                if(mrRobotState != MRROBOT_STATE.STANDING_RIGHT) {
                    mrRobot.setState("mrrobot_stand_right");
                    intendedState = MRROBOT_STATE.STANDING_RIGHT;
                }
            } else {
                if(mrRobotState != MRROBOT_STATE.STANDING_LEFT) {
                    mrRobot.setState("mrrobot_stand_left");
                    intendedState = MRROBOT_STATE.STANDING_LEFT;
                }
            }
        }

        if(intendedState != null) {
            // Mr. Roobot can only move around if he isn't currently falling down
            if(!mrRobotIsFalling() && !mrRobotIsSliding()) {
                mrRobotState = intendedState;
            }
        }

        moveMrRobot(delta);
        checkMrRobot(delta);
    }

    private void moveMrRobot(float delta) {
        float x = mrRobot.getX();
        float y = mrRobot.getY();
        if (mrRobotState == MRROBOT_STATE.WALKING_RIGHT) {
            mrRobot.setPosition(x + WALK_SPEED * delta, y);
        } else if (mrRobotState == MRROBOT_STATE.WALKING_LEFT) {
            mrRobot.setPosition(x - WALK_SPEED * delta, y);
        } else if (mrRobotState == MRROBOT_STATE.FALLING_RIGHT || mrRobotState == MRROBOT_STATE.FALLING_LEFT
            || mrRobotState == MRROBOT_STATE.SLIDING_LEFT || mrRobotState == MRROBOT_STATE.SLIDING_RIGHT) {
            mrRobot.setPosition(x, y - DOWN_SPEED * delta);
        }
    }

    private void checkMrRobot(float delta) {
        float x = mrRobot.getX() + 12f;
        float y = mrRobot.getY()+ 7f;

        float col = x / 8f;
        float line = (y / 8f) - 1f;

        TiledMapTileLayer.Cell cell = getCell((int)col, (int)line);
        DebugOutput.log("tile: " + (cell == null ? "-" : cell.getTile().getId()));

        if(cell == null) {
            if(!mrRobotIsFalling()) {
                mrRobotStartsFalling();
            }
        } else {
            // TODO - CHECK ONLY FOR SOLID BLOCKS TO STAND ON, NOT DOWNWARD PIPES OR LADDERS
            int tileId = cell.getTile().getId();
            if(tileId != 0 && tileId != TILE_SLIDER) {
                if(mrRobotIsFalling() || mrRobotIsSliding()) {
                    mrRobotLands();
                    mrRobot.setPosition(mrRobot.getX(), line * 8f);
                }
            }
            if(cell.getTile().getId() == TILE_DOT) {
                cell.setTile(clearedFloor);
            }
        }

        if(line > 0) {
            cell = getCell((int)col, (int)line - 1);
            if(cell != null && cell.getTile().getId() == TILE_SLIDER && !mrRobotIsSliding()) {
                mrRobotStartsSliding();
            }
        }
    }

    private void mrRobotLands() {
        if(mrRobotState.isFacingRight()) {
            mrRobotState = MRROBOT_STATE.STANDING_RIGHT;
        } else {
            mrRobotState = MRROBOT_STATE.STANDING_LEFT;
        }
    }

    private void mrRobotStartsFalling() {
        if(mrRobotState.isFacingRight()) {
            mrRobotState = MRROBOT_STATE.FALLING_RIGHT;
        } else {
            mrRobotState = MRROBOT_STATE.FALLING_LEFT;
        }
    }

    private void mrRobotStartsSliding() {
        if(mrRobotState.isFacingRight()) {
            mrRobotState = MRROBOT_STATE.SLIDING_RIGHT;
        } else {
            mrRobotState = MRROBOT_STATE.SLIDING_LEFT;
        }
    }

    private boolean mrRobotIsFalling() {
        return mrRobotState == MRROBOT_STATE.FALLING_LEFT || mrRobotState == MRROBOT_STATE.FALLING_RIGHT;
    }

    private boolean mrRobotIsSliding() {
        return mrRobotState == MRROBOT_STATE.SLIDING_LEFT || mrRobotState == MRROBOT_STATE.SLIDING_RIGHT;
    }

    private TiledMapTileLayer.Cell getCell(int col, int line) {
        TiledMapTileLayer.Cell cell = tiledMapTileLayer.getCell(col, line);
        if(cell == null) {
            return null;
        }
        if(cell.getTile() == null) {
            return null;
        }
        return cell;
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

        this.mrRobot.draw(batch, delta);

        // print debug stuff on screen
        // TODO: Enable only in testing / debug mode
        DebugOutput.draw(batch);

        BitmapFont font = Assets.instance().getFont(MrRobotAssets.FONT_ID.LOADING);
        font.draw(batch, "SCORE: 0", 2f, 140f);


        batch.end();
    }
}
