/*
 * $Header: $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package ch.marcelschoen.darkfunction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Holds data from an animation sheet created with the
 * DarkFunctions Editor.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class AnimationSheet {

	/** The spritesheet which contains the sprite data. */
	private SpriteSheet currentSpriteSheet = null;

	/** Stateful animation keyframe holder used for XML parsing. */
	private List<TextureRegion> currentKeyFrames = new ArrayList<TextureRegion>();
	
	/** Stateful delay time holder used for XML parsing. */
	private float currentDuration = 0;
	
	/** Stateful name holder used for XML parsing. */
	private String currentAnimationName = null;
	
	/** Stores the animations read from the file. */
	private Map<String, AnimationInfo> animations = new HashMap<String, AnimationInfo>();
	private Set<String> animationNames = new HashSet<String>();

	private static String animationsFolder = null;

	public static void initialize(String animationsFolderPath) {
		animationsFolder = animationsFolderPath;
		if(!animationsFolder.endsWith("/")) {
			animationsFolder += "/";
		}
	}

	public static String getAnimationsFolder() {
		return animationsFolder;
	}

	/**
	 * Creates a new animation holder.
	 * 
	 * @param filename The name of the animation file (in the animations folder subdirectory).
	 */
	public AnimationSheet(String filename) {
		FileHandle spriteAnimFile = Gdx.files.internal(animationsFolder + filename);
		if(!spriteAnimFile.exists()) {
			throw new IllegalArgumentException("Invalid filename, file does not exist: " + filename);
		}
		XmlReader reader = new XmlReader();
		XmlReader.Element topNode = reader.parse(spriteAnimFile);
		processNode(topNode);
		storeCurrentAnimation();
	}

	/**
	 * Returns the map of all animations in this sheet.
	 * 
	 * @return The map, where the key is the alias of an animation.
	 */
	public Set<String> getAnimationNames() {
		return this.animationNames;
	}
	
	/**
	 * @return the currentSpriteSheet
	 */
	public SpriteSheet getCurrentSpriteSheet() {
		return currentSpriteSheet;
	}

	/**
	 * @param currentSpriteSheet the currentSpriteSheet to set
	 */
	public void setCurrentSpriteSheet(SpriteSheet currentSpriteSheet) {
		this.currentSpriteSheet = currentSpriteSheet;
	}

	/**
	 * Returns a specific animation.
	 * 
	 * @param alias The alias of the animation.
	 * @return The animation (never null).
	 * @throws IllegalArgumentException If the given alias does not exist.
	 */
	public Animation getAnimation(String alias) {
		AnimationInfo result = animations.get(alias);
		if(result == null) {
			throw new IllegalArgumentException("No such animation: " + alias);
		}
		// We have to return a new instance of "Animation", because each
		// instance is stateful (so a 100 bombs on screen cannot all use
		// the same "Animation" class instance).
		return new Animation(result.frameDuration, result.keyFrames);
	}
	
	/**
	 * Returns a specific animation info holder.
	 * 
	 * @param alias The alias of the animation.
	 * @return The animation (never null).
	 * @throws IllegalArgumentException If the given alias does not exist.
	 */
	public AnimationInfo getAnimationInfo(String alias) {
		AnimationInfo result = animations.get(alias);
		if(result == null) {
			throw new IllegalArgumentException("No such animation: " + alias);
		}
		return result;
	}
	
	/**
	 * Processes a node of the XML file.
	 * 
	 * @param node A node of the XML data.
	 */
	private void processNode(XmlReader.Element node) {
//		System.out.println("Animation node: " + node.getName());
		if(node.getName().equals("animations")) {
			FileHandle spriteSheetFile = Gdx.files.internal(AnimationSheet.getAnimationsFolder() + node.getAttribute("spriteSheet"));
			if(!spriteSheetFile.exists()) {
				throw new IllegalStateException("** spritesheet file not found: " + node.getAttribute("name"));
			}
			this.currentSpriteSheet = new SpriteSheet(node.getAttribute("spriteSheet"));
		} else if(node.getName().equals("anim")) {
			storeCurrentAnimation();
			this.currentAnimationName = node.getAttribute("name");
			this.currentDuration = 0;
			this.currentKeyFrames = new ArrayList<TextureRegion>();
		} else if(node.getName().equals("cell")) {
			this.currentDuration = node.getIntAttribute("delay");
		} else if(node.getName().equals("spr")) {
			TextureRegion textureRegion = this.currentSpriteSheet.getSprite(node.getAttribute("name"));
			boolean flipX = node.getIntAttribute("flipH", 0) == 1;
			boolean flipY = node.getIntAttribute("flipV", 0) == 1;
			if(flipX || flipY) {
				textureRegion = new TextureRegion(textureRegion);
				textureRegion.flip(flipX, flipY);
			}
			System.out.println("Add sprite '" + node.getAttribute("name") + "' to animation '"
					+ this.currentAnimationName + "' / xflip: " + flipX + ", yflip: " + flipY);
			this.currentKeyFrames.add(textureRegion);

		}
		int children = node.getChildCount();
		if(children > 0) {
			for(int i = 0; i < children; i++) {
				processNode(node.getChild(i));
			}
		}
	}

	/**
	 * Creates and stores an animation in the animations map.
	 */
	private void storeCurrentAnimation() {
		if(this.currentKeyFrames.size() > 0) {
			Array<TextureRegion> frames = new Array<TextureRegion>();
			for(TextureRegion region : this.currentKeyFrames) {
				frames.add(region);
			}
			// In the "DarkFunctions" editor, the speed value 34 results in approximately (!)
			// one frame per second. Since 1 is the smallest possible value there (and the fastest
			// one) it means that if there's a value < 34 we must calculate how much of a second it is.
			System.out.println("Store animation '" + currentAnimationName + "' with " + currentKeyFrames.size() + " frames. Speed: " + (1.0f / 34.0f * this.currentDuration));
			this.animations.put(currentAnimationName, new AnimationInfo(1.0f / 34.0f * this.currentDuration, frames));
			this.animationNames.add(currentAnimationName);
		}
	}
}
