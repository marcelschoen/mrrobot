/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx.music;

import com.badlogic.gdx.audio.Music;

/**
 * Wrapper for the LIBGDX music; adds handling of
 * the volume state and changes for using with
 * a Tween manager.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class MusicWrapper {
	
	/** Stores the current volume of the music. */
    private float volume = 0.2f;

    /** The wrapped LIBGDX music holder. */
    private Music music;
    
    /**
     * Creates a wrapper for a given music.
     * 
     * @param music The LIBGDX music holder to wrap.
     */
    public MusicWrapper(Music music) {
        this.music = music;
    }

    /**
     * Sets the volume of the wrapped music.
     * 
     * @param volume The new volume.
     */
    public void setVolume(float volume) {
    	if(this.music != null) {
            this.volume = volume;
            music.setVolume(volume);
    	}
    }

    /**
     * Returns the volume of the wrapped music.
     * 
     * @return The current volume.
     */
    public float getVolume() {
    	return this.volume;
    }

    /**
     * Disposes the wrapped music.
     */
    public void dispose() {
    	if(this.music != null) {
        	this.music.dispose();
        	this.music = null;
    	}
    }

    /**
     * Stops playing the wrapped music.
     */
    public void stop() {
    	if(this.music != null) {
        	this.music.stop();
    	}
    }

    /**
     * Starts playing the wrapped music.
     */
    public void play() {
    	this.music.play();
    }

    /**
     * Sets the looping attribute of the wrapped music.
     * 
     * @param value True if the music should play in a loop.
     */
    public void setLooping(boolean value) {
    	this.music.setLooping(value);
    }
}
