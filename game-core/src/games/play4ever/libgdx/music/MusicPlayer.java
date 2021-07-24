/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.music;

import com.badlogic.gdx.Gdx;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import games.play4ever.libgdx.tween.JPlayTweenManager;

/**
 * Music player utility which supports fade-in and -out of music
 * by using the UniversalTween framework. See
 * http://ruuhkis.com/2013/01/30/libgdx-playing-music-with-smooth-fade-in-out/
 * 
 * REQUIRED: Add the Universal Tween Engine jar to your classpath (Eclipse
 * build path variable "TWEEN_LIBS" must point to directory with unpacked zip file).
 * http://www.aurelienribon.com/blog/projects/universal-tween-engine/
 *
 * @author Pasi Matalamaeki 
 * @version $Revision: $
 */
public class MusicPlayer {
	
	/** Constant for normal menu music volume. */
    private static final float MENU_MUSIC_VOLUME = 0.2f;
    
    /** Flag which indicates if music is playing. */
    private boolean isPlaying = false;
    
    /** Wrapper for LIBGDX music. */
	private MusicWrapper currentMusic;
	
	/** The manager which controls it all. */
    private TweenManager manager;
    
    /** Singleton music player instance. */
    private static MusicPlayer instance = new MusicPlayer();

    /**
     * Returns the instance of the music player.
     * 
     * @return The music player instance.
     */
    public static MusicPlayer instance() {
    	return instance;
    }
    
    /**
     * Creates the singleton music player instance.
     */
    private MusicPlayer() {
        this.manager = JPlayTweenManager.instance();
        Tween.registerAccessor(MusicWrapper.class, new MusicTweenAccessor());
    }

    /**
     * Plays the given music with a smooth fade-in effect.
     * If a music is already playing, there will be a 
     * fade-out of it first.
     * 
     * @param music The new music to play.
     */
    public void play(final MusicWrapper music) {
    	isPlaying = true;
        if(currentMusic != null) {
            manager.killTarget(currentMusic);
            Tween.to(currentMusic, MusicTweenAccessor.VOLUME, MENU_MUSIC_VOLUME)
            .target(0f)
            .setCallbackTriggers(TweenCallback.ANY)
            .setCallback(new TweenCallback() {
                
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                    if((type & TweenCallback.COMPLETE) != 0) {
                        currentMusic.stop();
                        start(music);
                    }
                }
            }).start(manager);
        } else {
            start(music);
        }
    }

    /**
     * Stops playing the current music.
     */
    public void stop() {
    	if(isPlaying) {
            manager.killTarget(currentMusic);
            Tween.to(currentMusic, MusicTweenAccessor.VOLUME, 1.5f)
            .target(0f)
            .setCallbackTriggers(TweenCallback.ANY)
            .setCallback(new TweenCallback() {
                
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                    if((type & TweenCallback.COMPLETE) != 0) {
                    	Gdx.app.log("MusicPlayer", "--- stop music ---");
                        currentMusic.stop();
                    	isPlaying = false;
                    }
                }
            }).start(manager);
    	}
    }

    /**
     * Invoke this when the game exits.
     */
    public void dispose() {
    	if(currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
    	}
    }
    
    /**
     * Starts playing the given music with a fade-in.
     * 
     * @param music The music to play.
     */
    private void start(MusicWrapper music) {
        this.currentMusic = music;
        music.setVolume(0f);
        music.setLooping(true);
        music.play();
        // 3 second fade-in
        Tween.to(music, MusicTweenAccessor.VOLUME, 3.0f)
        .target(MENU_MUSIC_VOLUME)
        .start(manager);
    }

    /**
     * Pauses the music (instantaneously).
     */
    public void pause() {
        if(currentMusic != null) {
        	isPlaying = false;
            currentMusic.stop();
        }
    }

    /**
     * Resumes the paused music.
     */
    public void resume() {
        if(currentMusic != null) {
        	isPlaying = true;
            currentMusic.play();
        }
    }

    /**
     * Updates the Tween manager of the music player.
     * This method must be called within the "render"
     * method of a screen (otherwise, the player
     * won't do anything).
     * 
     * @param delta The number in seconds since last refresh.
     */
    public void update(float delta) {
    	this.manager.update(delta);
    }
}
