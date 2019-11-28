package com.jplay.gdx.sprites;

public class SpriteEvent {

    private static SpriteEvent instance = new SpriteEvent();

    private Animated2DSprite source = null;
    private Animated2DSprite target = null;

    public enum TYPES {
        TARGET_REACHED,
        ANIMATION_COMPLETED,
        SPRITE_DESTROYED,
        COLLISION
    };

    private TYPES type;

    /**
     * Private constructor in order to enforce singleton or pooled instances.
     */
    private SpriteEvent() {
    }

    public static SpriteEvent getEvent(TYPES type) {
        SpriteEvent event = getInstance();
        event.type = type;
        event.source = null;
        event.target = null;
        return event;
    }

    public static SpriteEvent getEvent(TYPES type, Animated2DSprite source) {
        SpriteEvent event = getInstance();
        event.type = type;
        event.source = source;
        event.target = null;
        return event;
    }

    public static SpriteEvent getEvent(TYPES type, Animated2DSprite source, Animated2DSprite target) {
        SpriteEvent event = getInstance();
        event.type = type;
        event.source = source;
        event.target = target;
        return event;
    }

    public Animated2DSprite getSource() {
        return this.source;
    }

    public TYPES getType() {
        return this.type;
    }

    private static SpriteEvent getInstance() {
        // TBD: Support pooling - maybe
        return instance;
    }
}
