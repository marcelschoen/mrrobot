package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.jplay.gdx.MoveableEntity;

import static ch.marcelschoen.mrrobot.Tiles.TILE_FLAME;
import static ch.marcelschoen.mrrobot.Tiles.TILE_MR_ROBOT;

/**
 * Encapsulates tilemap-related functionality.
 *
 * @author Marcel Schoen
 */
public class TileMap {

    private TiledMapTileLayer tiledMapTileLayer;
    private TiledMapTile clearedFloor = null;
    private TiledMap map;
    private TmxMapLoader loader;
    private OrthogonalTiledMapRenderer tileMapRenderer;

    private MrRobot mrRobot;


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

    public TileMap(String filename, MrRobot mrRobot, Camera camera) {
        this.mrRobot = mrRobot;
        Flame.flames.clear();
        this.loader = new TmxMapLoader();
        this.map = loader.load("map/level16.tmx");

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
                            if(tile.getId() == TILE_MR_ROBOT) {
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                // Placement of Mr. Robot starting position
                                float x = (colCt * 8) - 8;
                                float y = lineCt * 8;
                                mrRobot.setTileMap(this);
                                mrRobot.setPosition(x, y);
                                mrRobot.setState(MrRobot.MRROBOT_STATE.STANDING_RIGHT);
                            } else if(tile.getId() == TILE_FLAME) {
                                tiledMapTileLayer.setCell(colCt, lineCt, null);
                                // Placement of flame starting position
                                float x = (colCt * 8) - 8;
                                float y = lineCt * 8;
                                Flame flame = new Flame(camera);
                                Flame.flames.add(flame);
                                flame.setTileMap(this);
                                flame.setPosition(x, y);
                                flame.setState(Flame.FLAME_STATE.WALKING_LEFT);
                            }
                        }
                    }
                    System.out.println("==>> " + lineCt + ": " + line);
                }
            }
            System.out.println("-->> Objects in layer: " + this.map.getLayers().get(0).getObjects().getCount());
            this.tileMapRenderer = new OrthogonalTiledMapRenderer(map);

            for(MapObject obj : this.map.getLayers().get(0).getObjects()) {
                System.out.println("> obj: " + obj.getName() + " / " + obj.getClass().getName());
            }
        }
    }

    public void clearCell(TiledMapTileLayer.Cell cell) {
        cell.setTile(clearedFloor);
    }

    public int getTileMapTile(CELL_TYPE type) {
        return getTileMapTile(mrRobot.getSprite(), type);
    }

    public int getTileMapTile(MoveableEntity sprite, CELL_TYPE type) {
        TiledMapTileLayer.Cell cell = getTileMapCell(sprite, type);
        if(cell == null || cell.getTile() == null) {
            return -1;
        }
        return cell.getTile().getId();
    }

    public TiledMapTileLayer.Cell getTileMapCell(MoveableEntity entity, CELL_TYPE type) {
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
        return getTileMapCell(mrRobot.getSprite(), type);
    }

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

    public void doRender(float delta, Camera camera) {
        this.tileMapRenderer.setView((OrthographicCamera) camera);
        tileMapRenderer.render();
    }
}
