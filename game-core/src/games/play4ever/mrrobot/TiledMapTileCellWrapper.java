package games.play4ever.mrrobot;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Wraps a cell from a Tiled map, adding storage for its column and row.
 */
public class TiledMapTileCellWrapper extends TiledMapTileLayer.Cell {

    private int column = -1;
    private int row = -1;
    private TiledMapTileLayer.Cell wrapped = null;

    public TiledMapTileCellWrapper(TiledMapTileLayer.Cell wrapped, int column, int row) {
        this.wrapped = wrapped;
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public TiledMapTile getTile() {
        return wrapped.getTile();
    }

    @Override
    public TiledMapTileLayer.Cell setTile(TiledMapTile tile) {
        return wrapped.setTile(tile);
    }

    @Override
    public boolean getFlipHorizontally() {
        return wrapped.getFlipHorizontally();
    }

    @Override
    public TiledMapTileLayer.Cell setFlipHorizontally(boolean flipHorizontally) {
        return wrapped.setFlipHorizontally(flipHorizontally);
    }

    @Override
    public boolean getFlipVertically() {
        return wrapped.getFlipVertically();
    }

    @Override
    public TiledMapTileLayer.Cell setFlipVertically(boolean flipVertically) {
        return wrapped.setFlipVertically(flipVertically);
    }

    @Override
    public int getRotation() {
        return wrapped.getRotation();
    }

    @Override
    public TiledMapTileLayer.Cell setRotation(int rotation) {
        return wrapped.setRotation(rotation);
    }
}
