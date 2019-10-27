/*
 * $Header:  $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */
package com.jplay.gdx;

import java.lang.reflect.Field;

/**
 * Misc utility stuff.
 *
 * @author Marcel Schoen
 */
public class PlatformUtil {
	
	/** Runtime OS detection. */
	private static String OS = System.getProperty("os.name").toLowerCase();

	/** Holds value of OUYA runtime detection. */
	private static Boolean isOuya = null;
	
	/**
	 * Check if game runs on OUYA.
	 * 
	 * @return True if its running on OUYA.
	 */
	public static boolean isOuya() {
		if(isOuya == null) {
			try {
				System.out.println("Create class 'android.os.Build'");
				Class<?> buildClass = Class.forName("android.os.Build");
				Field deviceField = buildClass.getDeclaredField("DEVICE");
				Object device = deviceField.get(null);
				System.out.println("Device Type: '" + device + "'");
				String deviceName = device.toString().trim().toLowerCase();
				isOuya = new Boolean(deviceName.indexOf("ouya") != -1);
				System.out.println("is OUYA: " + isOuya);
			} catch(Exception e) {
				return false;
			}
		}
		return isOuya.booleanValue();
	}
	
	/**
	 * Check if game runs on Win32.
	 * 
	 * @return True if its running on Windows.
	 */
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	/**
	 * Check if game runs on MacOS.
	 * 
	 * @return True if it runs on a Mac.
	 */
	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	/**
	 * Check if game runs on Unix / Linux.
	 * 
	 * @return True if it runs on a Unix system.
	 */
	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
}
