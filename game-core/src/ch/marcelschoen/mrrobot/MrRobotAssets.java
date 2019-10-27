package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.jplay.gdx.AnimationInfo;
import com.jplay.gdx.Assets;
import com.jplay.gdx.FileUtil;
import com.jplay.gdx.FontID;
import com.jplay.gdx.JPlaySprite;
import com.jplay.gdx.SoundID;
import com.jplay.gdx.TextureID;
import com.jplay.gdx.darkfunction.AnimationSheet;
import com.jplay.gdx.darkfunction.SpriteSheet;
import com.jplay.gdx.music.MusicPlayer;
import com.jplay.gdx.music.MusicWrapper;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MrRobotAssets extends Assets {

    /** Stores the loaded music. */
    private static Music gdxMusic = null;


    /** IDs of sounds. */
    public static enum SOUND_ID implements SoundID {
        DEBRIS
        ;
        public static SOUND_ID fromString(String name) {
            for(SOUND_ID id : values()) {
                if(id.name().equals(name)) {
                    return id;
                }
            }
            throw new IllegalArgumentException("** invalid FONT_ID: " + name);
        }
    }

    /** IDs of bitmap fonts. */
    public static enum FONT_ID implements FontID {
        MENU,
        LOADING,
        SETTINGS,
        DEBUG,
        GAME;
        public static FONT_ID fromString(String name) {
            for(FONT_ID id : values()) {
                if(id.name().equals(name)) {
                    return id;
                }
            }
            throw new IllegalArgumentException("** invalid FONT_ID: " + name);
        }
    }

    /** IDs of textures. */
    private static enum TEXTURE_ID implements TextureID {
        LOADING;
        public static TEXTURE_ID fromString(String name) {
            for(TEXTURE_ID id : values()) {
                if(id.name().equals(name)) {
                    return id;
                }
            }
            throw new IllegalArgumentException("** invalid TEXTURE_ID: " + name);
        }
    }

    /**
     * Initializes the asset handler.
     */
    public void doLoadAssets() {
        // Load font for loading screen first
        loadTexture("loading.png", TEXTURE_ID.LOADING);
        getSpriteFromTexture(getTexture(TEXTURE_ID.LOADING), 0, 0, 174, 32, SPRITE_ID.LOADING);
        // Immediately start loading
        super.manager.update();
    }

    /**
     * Continues loading assets.
     */
    public void continueLoadingAssets() {
        // List and load all game fonts
        super.loadSpritesFromSpriteSheet(new SpriteSheet("title.sprites"));

        FileHandle dir = Gdx.files.internal(AnimationSheet.getAnimationsFolder());
        System.out.println("--> DIRECTORY: " + dir.path() + ", exists: " + dir.exists() + ", is dir: " + dir.isDirectory());
        FileHandle[] entries = dir.list();
        System.out.println("Entries: " + entries.length);
        for(FileHandle entry : entries) {
            System.out.println(" Entry suffix: " + entry.extension());
            if(entry.extension().equals("anim")) {
                System.out.println("Sprite animation sheet: " + entry.name());
            }
        }

        // Load all bitmap fonts
        Properties fontList = FileUtil.readConfigPropertyFile("fontlist.properties");
        for(Map.Entry<Object, Object> entry : fontList.entrySet()) {
            String fontName = (String)entry.getKey();
            String fontFile = (String)entry.getValue();
            loadFont(fontFile, FONT_ID.fromString(fontName));
        }

        // Load sounds
        Properties soundList = FileUtil.readConfigPropertyFile("soundlist.properties");
        for(Map.Entry<Object, Object> entry : soundList.entrySet()) {
            String soundName = (String)entry.getKey();
            String soundFile = (String)entry.getValue();
            loadSound(soundFile, SOUND_ID.fromString(soundName));
        }

        // Read animation and sprite info
        processAnimationSheet("mrrobot.anim");
/*
        processAnimationSheet("BombsAndItems.anim");
        processAnimationSheet("joypads.anim");
        processAnimationSheet("BombExplosion.anim");
        processAnimationSheet("explosion_item_wall.anim");

        // Load sprites of joypads separately for rendering non-animated sprites
        super.loadSpritesFromSpriteSheet(new SpriteSheet("joypads.sprites"));

        // Load textures
        Properties textureList = FileUtil.readConfigPropertyFile("texturelist.properties");
        for(Map.Entry<Object, Object> entry : textureList.entrySet()) {
            String textureName = (String)entry.getKey();
            String textureFile = (String)entry.getValue();
            loadTexture(textureFile, TEXTURE_ID.fromString(textureName));
        }

        // Load sprites (single images)
        int arrowSize = 64;
        getSpriteFromTexture(getTexture(TEXTURE_ID.MENUARROW), 0, 0, arrowSize, arrowSize, SPRITE_ID.MENU_ARROW);

        getJPlaySpriteFromTexture(getTexture(TEXTURE_ID.FLOORS_AND_WALLS), Tile.WIDTH * 0, 0, Tile.WIDTH, Tile.HEIGHT, SPRITE_ID.TILE_WALL_DESTRUCTIBLE);
        getJPlaySpriteFromTexture(getTexture(TEXTURE_ID.FLOORS_AND_WALLS), Tile.WIDTH * 1, 0, Tile.WIDTH, Tile.HEIGHT, SPRITE_ID.TILE_WALL_HARD);
        getJPlaySpriteFromTexture(getTexture(TEXTURE_ID.FLOORS_AND_WALLS), Tile.WIDTH * 2, 0, Tile.WIDTH, Tile.HEIGHT, SPRITE_ID.TILE_FLOOR);
        getJPlaySpriteFromTexture(getTexture(TEXTURE_ID.FLOORS_AND_WALLS), Tile.WIDTH * 3, 0, Tile.WIDTH, Tile.HEIGHT, SPRITE_ID.TILE_FLOOR_SHADOWED);


        PlayerCharacter.initializeCharacters();

 */
    }

    /**
     * Reads an animation sheet.
     */
    public void processAnimationSheet(String name) {
        AnimationSheet sheet = new AnimationSheet(name);
        // Add all the single sprites
        loadSpritesFromSpriteSheet(sheet.getCurrentSpriteSheet());
        // Add all the animations
        Set<String> animationNames = sheet.getAnimationNames();
        for(String alias : animationNames) {
            AnimationInfo animationInfo = sheet.getAnimationInfo(alias);
            System.out.println("Animation: " + alias);
            addJPlaySprite(new JPlaySprite(animationInfo, true), alias);
        }
    }

    /**
     * Start playing menu background music.
     */
    public static void playMenuMusic() {
        System.out.println("Start playing music...");
        if(gdxMusic == null) {
            gdxMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Happy Bee.mp3"));
        }
        MusicWrapper music = new MusicWrapper(gdxMusic);
        music.setLooping(true);
        MusicPlayer.instance().play(music);
    }

    /**
     * Stop playing menu background music.
     */
    public static void stopMenuMusic() {
        MusicPlayer.instance().stop();
    }
}
