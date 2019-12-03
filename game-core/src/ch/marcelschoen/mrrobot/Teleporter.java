package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles teleporters in a tile-map, and manages their
 * target teleport locations.
 *
 * @author Marcel Schoen
 */
public class Teleporter {

    private static Map<TiledMapTileLayer.Cell, Array<Vector2>> teleporterTargets = null;
    private static Map<TiledMapTileLayer.Cell, Integer> selectedTeleporter = null;
    private static Map<TiledMapTileLayer.Cell, Vector2> tempMap = null;

    /**
     * 1. Must be invoked first when initializing a new level.
     */
    public static void startLevel() {
        teleporterTargets = new HashMap<>();
        selectedTeleporter = new HashMap<>();
        tempMap = new HashMap<>();
    }

    /**
     * 2. Invoke while processing tile map.
     *
     * @param tile Teleporter-tile to add to internal maps.
     * @param x The x-coordinate on screen in pixels.
     * @param y The y-coordinate on screen in pixels.
     */
    public static void addTeleporter(TiledMapTileLayer.Cell tile, float x, float y) {
        tempMap.put(tile, new Vector2(x, y + Tiles.TILE_WIDTH));
        selectedTeleporter.put(tile, 0);
    }

    /**
     * 3. Finalize level initialization be creating teleporter target maps.
     */
    public static void initializeTargets() {
        int numberOfTargets = tempMap.size() - 1;
        for(TiledMapTileLayer.Cell cell : tempMap.keySet()) {
            Array<Vector2> targets = new Array<>(numberOfTargets);
            teleporterTargets.put(cell, targets);
            for(TiledMapTileLayer.Cell cell2 : tempMap.keySet()) {
                if(cell2 != cell) {
                    targets.add(tempMap.get(cell2));
                }
            }
        }
    }

    /**
     * @param fromTeleporter The teleporter tile which Mr. Robot is standing on
     * @return The target teleporting position.
     */
    public static Vector2 getTeleportTarget(TiledMapTileLayer.Cell fromTeleporter) {
        Array<Vector2> targets = teleporterTargets.get(fromTeleporter);
        int currentTarget = selectedTeleporter.get(fromTeleporter) + 1;
        if(currentTarget >= targets.size) {
            currentTarget = 0;
        }
        selectedTeleporter.put(fromTeleporter, currentTarget);
        return targets.get(currentTarget);
    }
}
