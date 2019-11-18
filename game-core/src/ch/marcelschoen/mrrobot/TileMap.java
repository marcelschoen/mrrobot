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

    public TileMap(String filename, MrRobot mrRobot) {
        this.mrRobot = mrRobot;
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
            this.tileMapRenderer = new OrthogonalTiledMapRenderer(map);

            for(MapObject obj : this.map.getLayers().get(0).getObjects()) {
                System.out.println("> obj: " + obj.getName() + " / " + obj.getClass().getName());
            }
        }
    }

    public void clearCell(TiledMapTileLayer.Cell cell) {
        cell.setTile(clearedFloor);
    }

    public TiledMapTileLayer.Cell getTileMapCell(CELL_TYPE type) {
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

    public boolean cellEmpty(TiledMapTileLayer.Cell cell) {
        if(cell == null || cell.getTile() == null) {
            return true;
        }
        return false;
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
