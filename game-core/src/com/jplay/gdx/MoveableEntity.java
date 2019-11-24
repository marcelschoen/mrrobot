package com.jplay.gdx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;
import java.util.Map;

import ch.marcelschoen.aseprite.Animated2DSprite;

public class MoveableEntity {

    private String name;
    private float x;
    private float y;

    private Animated2DSprite sprite;

    private Map<String, Animated2DSprite> spritesMap = new HashMap<>();

    public MoveableEntity(String name, float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSprite(String name, Animated2DSprite sprite) {
        spritesMap.put(name, sprite);
    }

    public void setState(String name) {
        this.sprite = spritesMap.get(name);
        if(this.sprite == null) {
            throw new IllegalArgumentException("No sprite set for name '" + name + "'");
        }
    }

    public Animated2DSprite getSprite() {
        return this.sprite;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void draw(SpriteBatch batch, float delta) {
        this.sprite.draw(batch, x, y, delta);
    }
}
