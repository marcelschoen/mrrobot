/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/Assets.java,v 1.7 2013/02/17 23:26:11 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

import games.play4ever.libgdx.sprites.AnimatedSprite;

/**
 * Handles loading / caching and accessing game assets, such as
 * graphics, textures, sprites etc.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.7 $
 */
public abstract class Assets {
	
	/** The singleton reference of the assets handler. */
	private static Assets assets = null;

	/** Singleton asset manager instance. */
	protected AssetManager manager = null;

	private Map<FontID, String> fontFileNameMap = new HashMap<FontID, String>();
	private Map<FontID, BitmapFont> fontMap = new HashMap<FontID, BitmapFont>();

	private Map<SoundID, Sound> soundMap = new HashMap<SoundID, Sound>();
	private Map<TextureID, Texture> textureMap = new HashMap<TextureID, Texture>();

	private Map<String, Animation<TextureRegion>> animationHashMap = null;

	private static HashMap<String, AnimatedSprite> spriteCache = new HashMap<>();

	/**
	 * Returns the singleton assets instance.
	 * 
	 * @return The assets handler instance. 
	 *         null, if "initialize()" had not been invoked before.
	 */
	public static Assets instance() {
		return assets;
	}
	
	/**
	 * Initializes the asset handler.
	 */
	public void initialize() {
		assets = this;
		manager = new AssetManager();
		this.animationHashMap = new HashMap<>();

		doLoadAssets();
		
		// Start loading...
		manager.update();
		Gdx.app.log("Assets.initialize()", "> Game assets loading...");
	}

	/**
	 * Starts loading textures, fonts etc.
	 */
	public abstract void doLoadAssets();

	public void addAnimationMap(HashMap<String, Animation<TextureRegion>> animationHashMap) {
		for(String name : animationHashMap.keySet()) {
			String animationName = name.substring(name.lastIndexOf("/") + 1);
			Gdx.app.log("Assets", "Add animation '" + animationName + "'");
			this.animationHashMap.put(animationName, animationHashMap.get(name));
		}
	}

	public Animation<TextureRegion> getAnimation(String name) {
		Animation<TextureRegion> result = this.animationHashMap.get(name);
		if(result == null) {
			throw new IllegalArgumentException("No animation found with name '" + name + "'");
		}
		return result;
	}

	/**
	 * Loads a sound from the given file. Also stores the sound
	 * under the given ID in the sound map, so that it can be accessed later
	 * using that ID.
	 * 
	 * @param filename The filename with the sound.
	 * @param id The ID of the sound to load.
	 */
	public Sound loadSound(String filename, SoundID id) {
		FileHandle soundFile = Gdx.files.internal("sounds/" + filename);
		if(!soundFile.exists()) {
			throw new IllegalStateException("** soundfile not found: " + filename);
		}
		Sound sound = Gdx.audio.newSound(soundFile);
		soundMap.put(id, sound);
		return sound;
	}
	
	/**
	 * Loads a texture from the given file. Also stores the texture
	 * under the given ID in the texture map, so that it can be accessed later
	 * using that ID.
	 * 
	 * @param filename The filename with the texture. MUST BE A FILE WITH DIMENSIONS
	 *                 EACH A POWER OF 2, E.G. 512x256, 1024x1024 etc.
	 * @param id The ID of the texture to load.
	 */
	public Texture loadTexture(String filename, TextureID id) {
		FileHandle textureFile = Gdx.files.internal("textures/" + filename);
		if(!textureFile.exists()) {
			throw new IllegalStateException("** texturefile not found: " + filename);
		}
		Texture texture = new Texture(textureFile);
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		textureMap.put(id, texture);
		return texture;
	}

	/**
	 * Returns the given sound from the caching map.
	 * 
	 * @param id The ID of the sound.
	 * @return The sound, if it was loaded before using "loadSound()".
	 * @throws IllegalStateException If the sound has not been loaded yet.
	 */
	public Sound getSound(SoundID id) {
		Sound result = soundMap.get(id); 
		if(result == null) {
			throw new IllegalStateException("Sound not available: " + id);
		}
		return result;
	}

	/**
	 * Returns the given texture from the caching map.
	 * 
	 * @param id The ID of the texture.
	 * @return The texture, if it was loaded before using "loadTexture()".
	 * @throws IllegalStateException If the texture has not been loaded yet.
	 */
	public Texture getTexture(TextureID id) {
		Texture result = textureMap.get(id); 
		if(result == null) {
			throw new IllegalStateException("Texture not available: " + id);
		}
		return result;
	}

	/**
	 * Loads a bitmap font from the given file. Should be a *.fnt file, created
	 * with a tools like "Hiero". Also stores the font
	 * under the given ID in the font map, so that it can be accessed later
	 * using that ID.
	 * 
	 * @param filename The absolute path of the *.fnt bitmap font file.
	 * @param id The ID of the bitmap font.
	 */
	protected void loadFont(String filename, FontID id) {
		String fontName = "fonts/" + filename;
		fontFileNameMap.put(id, fontName);
		Gdx.app.log("Assets.loadFont()",">> LOAD FONT: " + fontName);
		manager.load(fontName, BitmapFont.class);
	}
	
	/**
	 * Returns the given bitmap font from the caching map.
	 * 
	 * @param id The ID of the font.
	 * @return The font, if it was loaded before using "loadFont()".
	 * @throws IllegalStateException If the font has not been created yet.
	 */
	public BitmapFont getFont(FontID id) {
		BitmapFont font = fontMap.get(id);
		if(font == null) {
			font = manager.get(fontFileNameMap.get(id), BitmapFont.class);
			if(font != null) {
				/////////////////////////////////////////font.setScale(1, -1);
				fontMap.put(id, font);
				Gdx.app.log("Assets.getFont()","Font available: " + id);
			}
		}
		if(font == null) {
			throw new IllegalStateException("Font not available: " + id);
		}
		return font;
	}
	
	/**
	 * Disposes all assets.
	 */
	public void dispose() {
		manager.clear();
	}
	
	/**
	 * Allows to determine if all assets have been loaded.
	 * 
	 * @return True if loading has completed.
	 */
	public boolean isLoadingCompleted() {
		if(manager.getProgress() == 1.0) {
			return true;
		}
		return false;
	}

	/**
	 * Is invoked from the loading screen once loading has finished.
	 */
	public void loadingCompleted() {
		// do nothing
	}
	
	/**
	 * Continue loading the assets (happens asynchronously). Use
	 * "boolean isLoadingCompleted()" to check if loading has finished.
	 */
	public void load() {
		manager.update();
	}
	
	/**
	 * Returns the asset manager instance.
	 * 
	 * @return The singleton asset manager instance.
	 */
	public AssetManager getAssetManager() {
		return manager;
	}
}
