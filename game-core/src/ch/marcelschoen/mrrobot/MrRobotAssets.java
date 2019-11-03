package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.jplay.gdx.AnimationInfo;
import com.jplay.gdx.Assets;
import com.jplay.gdx.FileUtil;
import com.jplay.gdx.FontID;
import com.jplay.gdx.JPlaySprite;
import com.jplay.gdx.SoundID;
import com.jplay.gdx.SpriteID;
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
    public static enum TEXTURE_ID implements TextureID {
        TITLE,
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

    /** IDs of animated sprites. */
    public static enum SPRITE_ID implements SpriteID {
        MRROBOT("MRROBOT");
        String alias;
        SPRITE_ID(String alias) {
            this.alias = alias;
        }
        public String getAlias() {
            return alias;
        }
        public static SPRITE_ID fromString(String name) {
            for(SPRITE_ID id : values()) {
                if(id.name().equals(name)) {
                    return id;
                }
            }
            throw new IllegalArgumentException("** invalid SPRITE_ID: " + name);
        }
    }

    /**
     * Initializes the asset handler.
     */
    public void doLoadAssets() {
        // Load font for loading screen first
        loadTexture("loading.png", TEXTURE_ID.LOADING);
        loadTexture("mrrobot-title.png", TEXTURE_ID.TITLE);
        // Immediately start loading
        super.manager.update();
    }

    /**
     * Continues loading assets.
     */
    public void continueLoadingAssets() {
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

        // Read sprite info
        Properties spriteList = FileUtil.readConfigPropertyFile("spritelist.properties");
        for(Map.Entry<Object, Object> entry : spriteList.entrySet()) {
            String spriteFile = (String)entry.getValue();
            super.loadSpritesFromSpriteSheet(new SpriteSheet(spriteFile));
        }

        // Read animation info
        Properties animatedSpriteList = FileUtil.readConfigPropertyFile("animationlist.properties");
        for(Map.Entry<Object, Object> entry : animatedSpriteList.entrySet()) {
            String animationFile = (String)entry.getValue();
            processAnimationSheet(animationFile);
        }
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
