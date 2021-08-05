package games.play4ever.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import games.play4ever.libgdx.FileUtil;
import games.play4ever.libgdx.collision.Collision;
import games.play4ever.libgdx.sprites.AnimatedSprite;
import games.play4ever.libgdx.sprites.Sprites;

import static games.play4ever.mrrobot.MrRobotState.STANDING_RIGHT;
import static games.play4ever.mrrobot.Tiles.TILE_DOT;
import static games.play4ever.mrrobot.Tiles.TILE_FLAME;
import static games.play4ever.mrrobot.Tiles.TILE_MAGNET_ITEM_LEFT;
import static games.play4ever.mrrobot.Tiles.TILE_MAGNET_ITEM_RIGHT;
import static games.play4ever.mrrobot.Tiles.TILE_MAGNET_LEFT;
import static games.play4ever.mrrobot.Tiles.TILE_MAGNET_RIGHT;
import static games.play4ever.mrrobot.Tiles.TILE_MR_ROBOT;
import static games.play4ever.mrrobot.Tiles.TILE_ONE_UP;
import static games.play4ever.mrrobot.Tiles.TILE_SHIELD;
import static games.play4ever.mrrobot.Tiles.TILE_TELEPORTER;
import static games.play4ever.mrrobot.Tiles.TILE_TRAMPOLINE_END;
import static games.play4ever.mrrobot.Tiles.TILE_TRAMPOLINE_LEFT;
import static games.play4ever.mrrobot.Tiles.TILE_TRAMPOLINE_MIDDLE;
import static games.play4ever.mrrobot.Tiles.TILE_TRAMPOLINE_RIGHT;

/**
 * Encapsulates tilemap-related functionality.
 *
 * @author Marcel Schoen
 */
public class TileMap {

    public static final int COLUMNS = 40;
    public static final int ROWS = 22;

    private float mrRobotStartingPositionX;
    private float mrRobotStartingPositionY;
    private Map<Flame, float[]> flameStartingPositions = new HashMap<>();

    private TiledMapTileLayer tiledMapTileLayer;
    private TiledMapTile clearedFloor = null;
    private TiledMap map;
    private String filename = null;
    private int numberOfDots = 0;

    private TmxMapLoader loader = new TmxMapLoader();
    private OrthogonalTiledMapRenderer tileMapRenderer;

    private MrRobot mrRobot;

    private static Array<String> mapFileNames = new Array<>();
    private static int currentMapIndex = 0;

    public enum CELL_TYPE {
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

    public static void readMapLists() {
        // Load all bitmap fonts
        Array<String> mapList = FileUtil.readConfigTextFile("maplist.txt");
        for(String line : mapList) {
            mapFileNames.add("map/" + line);
        }
    }

    public static String getCurrentMap() {
        return mapFileNames.get(currentMapIndex);
    }

    public static void resetToFirstMap() {
        currentMapIndex = 0;
    }

    public static void switchToNextMap() {
        currentMapIndex++;
        if(currentMapIndex >= mapFileNames.size) {
            resetToFirstMap();
        }
    }

    public TiledMapTileSet getTileSet() {
        return this.map.getTileSets().getTileSet(0);
    }

    public TileMap(String filename, MrRobot mrRobot) {
        this.filename = filename;
        this.map = loader.load(filename);
        this.mrRobot = mrRobot;

        Flame.flames.clear();
        Magnets.reset();
        Teleporter.startLevel();

        this.clearedFloor = this.map.getTileSets().getTileSet(0).getTile(3);
        Gdx.app.log("TileMap", "------------------------ BEGIN -----------------------------");
        Gdx.app.log("TileMap", "> Layers: " + this.map.getLayers().getCount());
        for(MapLayer layer : this.map.getLayers()) {
            Gdx.app.log("TileMap", "--> layer: " + layer.getName() + " / " + layer.getClass().getName());
            if(layer instanceof TiledMapTileLayer) {
                this.tiledMapTileLayer = (TiledMapTileLayer)layer;
                Gdx.app.log("TileMap", "==>> layer size: " + tiledMapTileLayer.getWidth() + " by " + tiledMapTileLayer.getHeight());
//                Gdx.app.log("TileMap", "==>> tile 0,0: " + tiledMapTileLayer.getCell(0,0).getTile().getId());
                for(int lineCt = tiledMapTileLayer.getHeight() - 1; lineCt > -1; lineCt --) {
                    String line = "";
                    for(int colCt = 0; colCt < tiledMapTileLayer.getWidth(); colCt ++) {
                        TiledMapTileLayer.Cell cell = tiledMapTileLayer.getCell(colCt, lineCt);
                        if(cell == null) {
                            line += "-";
                        } else {
                            TiledMapTileLayer.Cell mapCell = tiledMapTileLayer.getCell(colCt, lineCt);
                            TiledMapTileCellWrapper cellWrapper = new TiledMapTileCellWrapper(mapCell, colCt, lineCt);
                            tiledMapTileLayer.setCell(colCt, lineCt, cellWrapper);
                            TiledMapTile tile = cellWrapper.getTile();

                            line += tile.getId();
                            float x = colCt * 8;
                            float y = lineCt * 8;
                            if(tile.getId() == TILE_MR_ROBOT) {
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                // Placement of Mr. Robot starting position
                                mrRobot.setTileMap(this);
                                mrRobotStartingPositionX = x - 8;
                                mrRobotStartingPositionY = y;
                            } else if(tile.getId() == TILE_DOT) {
                                numberOfDots++;
                            } else if(tile.getId() == TILE_TELEPORTER) {
                                Teleporter.addTeleporter(cellWrapper, x - 8, y);
                            } else if(tile.getId() == TILE_ONE_UP) {
                                AnimatedTiledMapTile animatedTiledMapTile = (AnimatedTiledMapTile)tiledMapTileLayer.getCell(colCt, lineCt).getTile();
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                AnimatedSprite oneUpItem = Sprites.createSprite(animatedTiledMapTile, SpriteTypes.ONE_UP);
                                oneUpItem.setPosition(x, y);
                                oneUpItem.setVisible(true);
                                oneUpItem.setDefaultCollisionBounds(0, 0, 8, 8);
                                Collision.addRectangles(oneUpItem);
                            } else if(tile.getId() == TILE_SHIELD) {
                                AnimatedTiledMapTile animatedTiledMapTile = (AnimatedTiledMapTile)tiledMapTileLayer.getCell(colCt, lineCt).getTile();
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                AnimatedSprite shieldItem = Sprites.createSprite(animatedTiledMapTile, SpriteTypes.SHIELDS);
                                shieldItem.setPosition(x, y);
                                shieldItem.setVisible(true);
                                shieldItem.setDefaultCollisionBounds(0, 0, 8, 8);
                                Collision.addRectangles(shieldItem);
                            } else if(tile.getId() == TILE_FLAME) {
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                // Placement of flame starting position
                                Flame flame = new Flame();
                                Flame.flames.add(flame);
                                flame.setTileMap(this);
                                float[] flameStartingPosition = new float[2];
                                flameStartingPosition[0] = x - 8;
                                flameStartingPosition[1] = y;
                                flameStartingPositions.put(flame, flameStartingPosition);
                            } else if(tile.getId() == TILE_MAGNET_ITEM_LEFT) {
                                TiledMapTile tiledMapTile = (TiledMapTile)tiledMapTileLayer.getCell(colCt, lineCt).getTile();
                                AnimatedSprite magnetLeftItem = Sprites.createSprite(tiledMapTile, 50, SpriteTypes.MAGNET_LEFT);
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                magnetLeftItem.setPosition(x, y);
                                magnetLeftItem.setVisible(true);
                                magnetLeftItem.setDefaultCollisionBounds(0, 0, 8, 8);
                                Collision.addRectangles(magnetLeftItem);
                            } else if(tile.getId() == TILE_MAGNET_ITEM_RIGHT) {
                                TiledMapTile tiledMapTile = (TiledMapTile)tiledMapTileLayer.getCell(colCt, lineCt).getTile();
                                AnimatedSprite magnetRightItem = Sprites.createSprite(tiledMapTile, 50, SpriteTypes.MAGNET_RIGHT);
                                magnetRightItem.setPosition(x, y);
                                magnetRightItem.setVisible(true);
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                magnetRightItem.setDefaultCollisionBounds(0, 0, 8, 8);
                                Collision.addRectangles(magnetRightItem);
                            } else if(tile.getId() == TILE_MAGNET_LEFT) {
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                AnimatedSprite magnetLeft = Sprites.createSprite(MrRobot.ANIM.magnet_left.name());
                                magnetLeft.setPosition(x, y);
                                magnetLeft.setVisible(true);
                                Magnets.addMagnetLeft(magnetLeft);
                            } else if(tile.getId() == TILE_MAGNET_RIGHT) {
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                AnimatedSprite magnetRight = Sprites.createSprite(MrRobot.ANIM.magnet_right.name());
                                magnetRight.setPosition(x, y);
                                magnetRight.setVisible(true);
                                Magnets.addMagnetRight(magnetRight);
                            } else if(tile.getId() == TILE_TRAMPOLINE_LEFT) {
                                replaceTrampolineTilesWithSprite(colCt, lineCt);
                                AnimatedSprite trampolineSprite = Sprites.createSprite(Arrays.asList(MrRobot.ANIM.trampoline_still.name(), MrRobot.ANIM.trampoline_big.name()) , SpriteTypes.TRAMPOLINES);
                                trampolineSprite.showAnimation(MrRobot.ANIM.trampoline_still.name());
                                trampolineSprite.setPosition(x, y);
                                trampolineSprite.setVisible(true);
                                Trampoline trampoline = new Trampoline(trampolineSprite);
                                Trampolins.addTrampoline(colCt, lineCt, trampoline);
                            }
                        }
                    }
//                    Gdx.app.log("TileMap", "==>> " + lineCt + ": " + line);
                }
            }
//            Gdx.app.log("TileMap", "-->> Objects in layer: " + this.map.getLayers().get(0).getObjects().getCount());
            this.tileMapRenderer = new OrthogonalTiledMapRenderer(map);

            for(MapObject obj : this.map.getLayers().get(0).getObjects()) {
                Gdx.app.log("TileMap", "> obj: " + obj.getName() + " / " + obj.getClass().getName());
            }
        }
        Bombs.getInstance().initialize(this);
        Teleporter.initializeTargets();
        putCharactersAtStartingPosition();
    }

    private void replaceTrampolineTilesWithSprite(int startColumn, int row) {
        // Use two transparent tiles "behind" the sprite, so that Mr. Robot can stand on them
        TiledMapTile emptyTile1 = map.getTileSets().getTile(TILE_TRAMPOLINE_END);
        TiledMapTile emptyTile2 = map.getTileSets().getTile(TILE_TRAMPOLINE_MIDDLE);
        tiledMapTileLayer.getCell(startColumn, row).setTile(emptyTile1);
        int col = startColumn + 1;
        while(col < this.tiledMapTileLayer.getWidth() && tiledMapTileLayer.getCell(col, row).getTile().getId() != TILE_TRAMPOLINE_RIGHT) {
            tiledMapTileLayer.getCell(col, row).setTile(emptyTile2);
            col++;
        }
        tiledMapTileLayer.getCell(col, row).setTile(emptyTile1);
    }

    /**
     * Counts down by one dot, after Mr. Robot consumed one by walking over it.
     */
    public void decreaseNumberOfDots() {
        numberOfDots--;
        if(numberOfDots < 0) {
            throw new IllegalStateException("Number of dots now negative!");
        }
    }

    /**
     * @return The number of remaining dots before the level is completed.
     */
    public int getNumberOfDots() {
        return numberOfDots;
    }

    /**
     * Reset Mr. Robot and the flame sprites to their starting positions in the level (after
     * Mr. Robot has died).
     */
    private void putCharactersAtStartingPosition() {
        mrRobot.setPosition(mrRobotStartingPositionX, mrRobotStartingPositionY);
        mrRobot.setState(STANDING_RIGHT);
        for(Flame flame : flameStartingPositions.keySet()) {
            flame.setPosition(flameStartingPositions.get(flame)[0], flameStartingPositions.get(flame)[1]);
            flame.setState(Flame.FLAME_STATE.WALKING_LEFT);
            flame.getSprite().setVisible(true);
        }
        mrRobot.getMrRobotSprite().setVisible(true);
    }

    /**
     * Resets the map (when the player dies).
     */
    public void restart() {
        putCharactersAtStartingPosition();
    }

    public void clearCell(TiledMapTileLayer.Cell cell) {
        cell.setTile(clearedFloor);
    }

    public int getTileMapTile(CELL_TYPE type) {
        return getTileMapTile(mrRobot.getMrRobotSprite(), type);
    }

    public int getTileMapTile(AnimatedSprite sprite, CELL_TYPE type) {
        TiledMapTileLayer.Cell cell = getTileMapCell(sprite, type);
        if(cell == null || cell.getTile() == null) {
            return -1;
        }
        return cell.getTile().getId();
    }

    public TiledMapTileLayer.Cell getTileMapCell(AnimatedSprite entity, CELL_TYPE type) {
        float x = mrRobot.getX() + 12f;
        float y = mrRobot.getY();

        float col = x / 8f;
        float line = (y / 8f) - 1f;
        if(type == CELL_TYPE.BEHIND) {
            line += 1;
        } else if(type == CELL_TYPE.FURTHER_BELOW) {
            line -= 1;
        }
        return getCell((int)col, (int)line);
    }

    public TiledMapTileLayer.Cell getTileMapCell(CELL_TYPE type) {
        return getTileMapCell(mrRobot.getMrRobotSprite(), type);
    }

    /**
     * Returns a cell from the tile map.
     *
     * @param col The column for the tile.
     * @param line The line (row) for the tile.
     * @return The tile at the position (may be null).
     */
    public TiledMapTileLayer.Cell getCell(int col, int line) {
        TiledMapTileLayer.Cell cell = tiledMapTileLayer.getCell(col, line);
        if(cell == null) {
            return null;
        }
        if(cell.getTile() == null) {
            return null;
        }
        return cell;
    }

    /**
     * Renders the tile map.
     *
     * @param delta The time delta
     * @param camera The 2D camera.
     */
    public void doRender(float delta, Camera camera) {
        this.tileMapRenderer.setView((OrthographicCamera) camera);
        tileMapRenderer.render();
    }
}
