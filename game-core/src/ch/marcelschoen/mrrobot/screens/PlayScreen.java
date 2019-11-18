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
    public static final int TILE_ROLL_RIGHT_1 = 12;
    public static final int TILE_ROLL_RIGHT_2 = 13;
    public static final int TILE_LADDER_LEFT = 6;
    public static final int TILE_LADDER_RIGHT = 7;

    public static final int TILE_ROLL_LEFT_1 = 140;
    public static final int TILE_ROLL_LEFT_2 = 141;

    private static final float WALK_SPEED = 32;
    private static final float DOWN_SPEED = 24;

    private static int score = 0;

    private TiledMap map;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    private TmxMapLoader loader;

    private MoveableEntity mrRobot = new MoveableEntity("mrrobot", 0, 0);

    private TiledMapTileLayer tiledMapTileLayer;
    private TiledMapTile clearedFloor = null;

    private enum CELL_TYPE {
        BELOW(1f),
        FURTHER_BELOW(2f),
        BEHIND(0f);

        private float yOffset = 0;
        CELL_TYPE(float yOffset) {
            this.yOffset = yOffset;
        }

        public float getyOffset() {
            return this.yOffset;
        }
    }

    private boolean initialized = false;

    private enum MRROBOT_STATE {
        CLIMBING_UP(true),
        CLIMBING_DOWN(true),
        SLIDING_RIGHT(true),
        SLIDING_LEFT(false),
        FALLING_RIGHT(true),
        FALLING_LEFT(false),
        STANDING_ON_LADDER(true),
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
        this.mrRobot.setSprite("mrrobot_stand_on_ladder", Assets.instance().getAnimated2DSprite("mrrobot_stand_on_ladder"));
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
                    if(!mrRobotIsClimbing() && !mrRobotIsFalling() && !mrRobotIsSliding()) {
                        mrRobot.setState("mrrobot_walk_left");
                        intendedState = MRROBOT_STATE.WALKING_LEFT;
                    }
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camera.position.x += 100 * delta;
            } else {
                if(mrRobotState != MRROBOT_STATE.WALKING_RIGHT) {
                    if(!mrRobotIsClimbing() && !mrRobotIsFalling() && !mrRobotIsSliding()) {
                        mrRobot.setState("mrrobot_walk_right");
                        intendedState = MRROBOT_STATE.WALKING_RIGHT;
                    }
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            intendedState = mrRobotClimbing(MRROBOT_STATE.CLIMBING_UP);
        } else {
            // Stop movement
            intendedState = stopMrRobot();
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

    private MRROBOT_STATE mrRobotClimbing(MRROBOT_STATE climbingType) {
        TiledMapTileLayer.Cell cell = getTileMapCell(CELL_TYPE.BEHIND);
        TiledMapTileLayer.Cell cellBelow = getTileMapCell(CELL_TYPE.FURTHER_BELOW);
        DebugOutput.log("behind: " + (cell == null ? "-" : cell.getTile().getId()), 80, 160);
        int tileId = cell != null && cell.getTile() != null ? cell.getTile().getId() : -1;
        int tileBelowId = cellBelow != null && cellBelow.getTile() != null ? cellBelow.getTile().getId() : -1;
//        DebugOutput.log("behind: " + tileId, 0, 40);
        boolean climb = false;
        boolean alignLeftTile = false;
        if(cell != null && (tileId == TILE_LADDER_LEFT || tileId == TILE_LADDER_RIGHT)) {
            alignLeftTile = tileId == TILE_LADDER_RIGHT;
            climb = true;
        } else if(cellBelow != null && (tileBelowId == TILE_LADDER_LEFT || tileBelowId == TILE_LADDER_RIGHT)) {
            alignLeftTile = tileBelowId == TILE_LADDER_RIGHT;
            climb = true;
        }
        if(climb && mrRobotIsNearlyAligned()) {
            alignMrRobotHorizontally(alignLeftTile);
            mrRobot.setState("mrrobot_climb");
            mrRobotState = climbingType;
        }
        return null;
    }

    private float alignMrRobotVertically() {
        float y = mrRobot.getY() + 12;
        int row = (int)(y / 8f) - 1;
        mrRobot.setPosition(mrRobot.getX(), row * 8f);
        return mrRobot.getY();
    }

    private void alignMrRobotHorizontally(boolean alignLeftTile) {
        float x = mrRobot.getX() + 12;
        int col = (int)(x / 8f) - 2;
        if(alignLeftTile) {
            col --;
        }
        mrRobot.setPosition(col * 8f + 12, mrRobot.getY());
    }

    private boolean mrRobotIsNearlyAligned() {
        float x = mrRobot.getX() + 6f;
        int col = (int)(x / 8f) + 1;
        float diff = x - (col * 8f);
        /////////DebugOutput.log("x: " + x + ", col: " + col + ", diff: " + diff, 0, 100);
        return Math.abs(diff) < 8f;
    }

    private MRROBOT_STATE stopMrRobot() {
        if(mrRobotIsClimbing()) {
            mrRobot.setState("mrrobot_stand_on_ladder");
            return MRROBOT_STATE.STANDING_ON_LADDER;
        } else {
            if(mrRobotState.isFacingRight) {
                if(mrRobotState != MRROBOT_STATE.STANDING_RIGHT) {
                    mrRobot.setState("mrrobot_stand_right");
                    return MRROBOT_STATE.STANDING_RIGHT;
                }
            } else {
                if(mrRobotState != MRROBOT_STATE.STANDING_LEFT) {
                    mrRobot.setState("mrrobot_stand_left");
                    return MRROBOT_STATE.STANDING_LEFT;
                }
            }
        }
        return null;
    }

    private void moveMrRobot(float delta) {
        float x = mrRobot.getX();
        float y = mrRobot.getY();
        if (mrRobotState == MRROBOT_STATE.WALKING_RIGHT) {
            x += WALK_SPEED * delta;
            y = alignMrRobotVertically();
        } else if (mrRobotState == MRROBOT_STATE.WALKING_LEFT) {
            x -= WALK_SPEED * delta;
            y = alignMrRobotVertically();
        } else if (mrRobotState == MRROBOT_STATE.FALLING_RIGHT || mrRobotState == MRROBOT_STATE.FALLING_LEFT
            || mrRobotState == MRROBOT_STATE.SLIDING_LEFT || mrRobotState == MRROBOT_STATE.SLIDING_RIGHT) {
            y -= DOWN_SPEED * delta;
        } else if (mrRobotState == MRROBOT_STATE.CLIMBING_UP) {
            y += WALK_SPEED * delta;
        } else if (mrRobotState == MRROBOT_STATE.CLIMBING_DOWN) {
            y -= WALK_SPEED * delta;
        }

        if(x < -5) {
            x = -5;
            if(mrRobotIsFalling()) {
                // TODO - REVERSE DIRECTION
            }
            // TODO - CONSTANT FOR MR.ROBOT SPRITE WIDTH
        } else if(x > MrRobotGame.VIRTUAL_WIDTH - 19) {
            x = MrRobotGame.VIRTUAL_WIDTH - 19;
        }

        mrRobot.setPosition(x, y);
    }

    private TiledMapTileLayer.Cell getTileMapCell(CELL_TYPE type) {
        float x = mrRobot.getX() + 12f;
        float y = mrRobot.getY()+ 7f;

        float col = x / 8f;
        float line = (y / 8f) - 1f;
        if(type == CELL_TYPE.BEHIND) {
            line += 1;
        } else if(type == CELL_TYPE.FURTHER_BELOW) {
            line -= 1;
        }
        return getCell((int)col, (int)line);
    }

    private boolean cellEmpty(TiledMapTileLayer.Cell cell) {
        if(cell == null || cell.getTile() == null) {
            return true;
        }
        return false;
//        return cell.getTile().getId() == TILE_LADDER_RIGHT || cell.getTile().getId() == TILE_LADDER_LEFT;
    }

    private void checkMrRobot(float delta) {
        float x = mrRobot.getX() + 12f;
        float y = mrRobot.getY()+ 7f;

        float col = x / 8f;
        float line = (y / 8f) - 1f;

        TiledMapTileLayer.Cell cell = getTileMapCell(CELL_TYPE.BELOW);

        if(cellEmpty(cell)) {
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
                score += 1;
            }
            if(cell.getTile().getId() == TILE_ROLL_LEFT_1 || cell.getTile().getId() == TILE_ROLL_LEFT_2) {
                mrRobot.setPosition(mrRobot.getX() - 20f * delta, mrRobot.getY());
            } else if(cell.getTile().getId() == TILE_ROLL_RIGHT_1 || cell.getTile().getId() == TILE_ROLL_RIGHT_2) {
                mrRobot.setPosition(mrRobot.getX() + 20f * delta, mrRobot.getY());
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

    private boolean mrRobotIsClimbing() {
        return mrRobotState == MRROBOT_STATE.CLIMBING_DOWN
                || mrRobotState == MRROBOT_STATE.CLIMBING_UP
                || mrRobotState == MRROBOT_STATE.STANDING_ON_LADDER;
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
        font.draw(batch, "SCORE: " + score, 2f, 150f);


        batch.end();
    }
}
