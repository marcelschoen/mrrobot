package games.play4ever.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

import java.util.Map;
import java.util.Properties;

import games.play4ever.libgdx.Assets;
import games.play4ever.libgdx.DebugOutput;
import games.play4ever.libgdx.FileUtil;
import games.play4ever.libgdx.FontID;
import games.play4ever.libgdx.SoundID;
import games.play4ever.libgdx.SpriteID;
import games.play4ever.libgdx.TextureID;
import games.play4ever.libgdx.aseprite.Aseprite;
import games.play4ever.libgdx.music.MusicPlayer;
import games.play4ever.libgdx.music.MusicWrapper;

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
        GAMEPAD_OVERLAY,
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
        loadTexture("gamepad_overlay3.png", TEXTURE_ID.GAMEPAD_OVERLAY);
        loadTexture("loading.png", TEXTURE_ID.LOADING);
        loadTexture("mrrobot-title.png", TEXTURE_ID.TITLE);
        // Immediately start loading
        super.manager.update();
    }

    @Override
    public void loadingCompleted() {
        DebugOutput.initialize(getFont(MrRobotAssets.FONT_ID.DEBUG));
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

        // Read animation info
        Properties animatedSpriteList = FileUtil.readConfigPropertyFile("animationlist.properties");
        for(Map.Entry<Object, Object> entry : animatedSpriteList.entrySet()) {
            FileHandle animationFile = Gdx.files.internal( "animation/" + (String)entry.getValue() );
            setAnimationMap(Aseprite.fromFile(animationFile.path()));
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
