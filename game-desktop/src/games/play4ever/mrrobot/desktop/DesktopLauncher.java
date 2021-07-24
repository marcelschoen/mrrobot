package games.play4ever.mrrobot.desktop;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.play4ever.mrrobot.MrRobotGame;

public class DesktopLauncher {
	public static void main (String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "mrrobot-gdx";
		cfg.width = 1920;
		cfg.height = 1080;
		cfg.fullscreen = true;
		if(args != null && args.length > 0 && args[0].equalsIgnoreCase("fullscreen")) {
			cfg.fullscreen = true;
		}
		System.setProperty("desktop", "true");

		if(cfg.fullscreen) {
			Graphics.DisplayMode displayMode = findMatchingMode(cfg.width, cfg.height);
			cfg.width = displayMode.width;
			cfg.height = displayMode.height;
			System.out.println("Running game in fullscreen mode: " + cfg.width + "x" + cfg.height);
			new LwjglApplication(new MrRobotGame(displayMode), cfg);
		} else {
			System.out.println("Running game in window mode...");
			new LwjglApplication(new MrRobotGame(null), cfg);
		}

	}

	/**
	 * Detects the display mode is most close to the game's HD resolution,
	 * while still allowing for the same aspect ratio when stretching the
	 * game vieport.
	 *
	 * @param width The internal game screen width.
	 * @param height The internal game screen height.
	 * @return The matching display resolution to use (for fullscreen mode only).
	 */
	private static Graphics.DisplayMode findMatchingMode(int width, int height) {
		Graphics.DisplayMode displayMode = null;
		float targetRatio = (float)width / (float)height;

		System.out.println("Find matching mode for: " + width + " x " + height);

		List<Graphics.DisplayMode> validModes = new ArrayList<Graphics.DisplayMode>();
		Map<Graphics.DisplayMode, Float> modes = new HashMap<Graphics.DisplayMode, Float>();

		Graphics.DisplayMode perfectMode = null, nearPerfectMode = null;
		for(Graphics.DisplayMode mode: LwjglApplicationConfiguration.getDisplayModes()) {
			if(mode.width >= width && mode.height >= height) {
				float modeRatio = (float)mode.width / (float)mode.height;
				System.out.println("Valid mode: " + mode + ", ratio: " + modeRatio);
				validModes.add(mode);
				modes.put(mode, modeRatio);
				if(mode.width == width && mode.height == height) {
					perfectMode = mode;
				}
				if(modeRatio == targetRatio) {
					if(nearPerfectMode != null) {
						if(mode.width >= width && mode.height >= height ) {
							nearPerfectMode = mode;
						}
					} else {
						nearPerfectMode = mode;
					}
				}
			}
		}

		float currentRatio = -1;
		for(Map.Entry<Graphics.DisplayMode, Float> mode : modes.entrySet()) {
			if(displayMode == null) {
				displayMode = mode.getKey();
				currentRatio = mode.getValue();
			}
			if(displayMode != mode.getKey()) {
				float diffCurrent = Math.abs(targetRatio - currentRatio);
				float diffNew = Math.abs(targetRatio - mode.getValue());
				if(diffNew < diffCurrent) {
					displayMode = mode.getKey();
					currentRatio = mode.getValue();
				}
			}
		}

		displayMode = perfectMode == null ? nearPerfectMode : perfectMode;
		displayMode = displayMode == null? validModes.get(0) : displayMode;

		System.out.println("---- USE RESOLUTION: " + displayMode + " -------");
		return displayMode;
	}
}
