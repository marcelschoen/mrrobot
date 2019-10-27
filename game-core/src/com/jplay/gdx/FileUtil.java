/*
 * $Header: $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * File-handling related utility stuff.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class FileUtil {

	/**
	 * Reads a property file from the assets subdirectory "config/".
	 * 
	 * @param propertyFilename The name of the property file.
	 * @return The property holder.
	 */
	public static Properties readConfigPropertyFile(String propertyFilename) {
		Properties result = new Properties();
		BufferedInputStream in = null;
		try {
			FileHandle fontlistProperties = Gdx.files.internal( "config/" + propertyFilename );
			in = fontlistProperties.read(1024);
			result.load(in);
		} catch(IOException ex) {
			ex.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch(IOException ex) {
					// ignore
				}
			}
		}
		return result;
	}
}
