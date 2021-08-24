/*
 * $Header: $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * File-handling related utility stuff.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class FileUtil {

	/**
	 * Reads a text file from the assets subdirectory "config/".
	 *
	 * @param textFilename The name of the text file.
	 * @return The property holder.
	 */
	public static Array<String> readConfigTextFile(String textFilename) {
		Array<String> lines = new Array<>();
		BufferedInputStream in = null;
		try {
			FileHandle textFileHandle = Gdx.files.internal( "config/" + textFilename );
			in = textFileHandle.read(1024);
			BufferedReader rd = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line = rd.readLine()) != null) {
				line = line.trim();
				if(!line.startsWith("#")) {
					lines.add(line);
				}
			}
		} catch(Exception ex) {
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
		return lines;
	}

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
