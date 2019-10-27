/*
 * $Header: $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx.darkfunction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds data from a sprite sheet created with the
 * DarkFunctions Editor.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class SpriteSheet {

	/** The texture which holds the sprite data. */
	private Texture texture = null;

	/** Temporary holder for string prefix. */
	private String dir = "/";

	/** The sprites in this sheet. */
//	private Map<String, List<Sprite>> sprites = new HashMap<String, List<Sprite>>();
	private Map<String, Sprite> spriteMap = new HashMap<String, Sprite>();
	
	/**
	 * Creates a new spritesheet holder.
	 * 
	 * @param filename The name of the spritesheet file (in the animations folder subdirectory).
	 */
	public SpriteSheet(String filename) {
		XmlReader reader = new XmlReader();
		FileHandle spriteSheetFile = Gdx.files.internal(AnimationSheet.getAnimationsFolder() + filename);
		System.out.println("...try to read spritesheet file: " + filename);
		XmlReader.Element topNode = reader.parse(spriteSheetFile);
		System.out.println("Spritesheet file parsed, process...");

		processNode(topNode);
	}
	
	/**
	 * Returns the map of all sprites in this sheet.
	 * 
	 * @return The map, where the key is the alias of a sprite.
	 */
	public Map<String, Sprite> getSprites() {
		return this.spriteMap;
	}

	/**
	 * Returns a certain sprite from this sheet.
	 * 
	 * @param alias The alias of this sprite.
	 * @return The sprite instance (never null).
	 * @throws IllegalArgumentException If the given alias does not exist.
	 */
	public Sprite getSprite(String alias) {
		Sprite result = this.spriteMap.get(alias); 
		if(result == null) {
			throw new IllegalArgumentException("No such sprite in this sheet: " + alias);
		}
		return result;
	}
	
	/**
	 * Processes a node of the XML file.
	 * 
	 * @param node A node of the XML data.
	 */
	private void processNode(XmlReader.Element node) {
		if(node.getName().equals("img")) {
			FileHandle textureFile = Gdx.files.internal(AnimationSheet.getAnimationsFolder() + node.getAttribute("name"));
			if(!textureFile.exists()) {
				throw new IllegalStateException("** texturefile not found: " + node.getAttribute("name"));
			}
			this.texture = new Texture(textureFile);
			texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		} else if(node.getName().equals("dir")) {
			if(!node.getAttribute("name").equals("/")) {
				dir = dir + node.getAttribute("name") + "/";
			}
		} else if(node.getName().equals("spr")) {
			String alias = dir + node.getAttribute("name");
			int x = node.getIntAttribute("x");
			int y = node.getIntAttribute("y");
			int w = node.getIntAttribute("w");
			int h = node.getIntAttribute("h");
			TextureRegion region = new TextureRegion(this.texture, x, y, w, h);
			region.flip(false, true);
			Sprite sprite = new Sprite(region);
			spriteMap.put(alias, sprite);
			System.out.println("Sprite '" + alias + "' loaded into sprite sheet.");
		}
		
		// Recursively process all child nodes
		int children = node.getChildCount();
		if(children > 0) {
			for(int i = 0; i < children; i++) {
				processNode(node.getChild(i));
			}
		}
		
		// When exiting a "dir" node, cut back path
		// by first cutting of the trailing "/", then
		// the current "dir" value
		if(node.getName().equals("dir")) {
			dir = dir.substring(0, dir.lastIndexOf("/"));
			dir = dir.substring(0, dir.lastIndexOf("/") + 1);
		}
	}
}
