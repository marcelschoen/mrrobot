package games.play4ever.mrrobot.desktop;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.play4ever.mrrobot.MrRobotGame;

import static games.play4ever.mrrobot.MrRobotGame.GAME_TESTING;

/**
 * Launcher for desktop (Windows, Linux, MacOS)
 */
public class DesktopLauncher {

	public static void main(String[] args) {
		try {
			Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
			cfg.setTitle("mrrobot-gdx");
			if (args != null && args.length > 0 && args[0].equalsIgnoreCase("fullscreen")) {
			}
			System.setProperty("desktop", "true");
			System.setProperty(GAME_TESTING, "true");

			if (true) {
				Graphics.DisplayMode displayMode = findMatchingMode(1920, 1080);
				Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
				config.setFullscreenMode(displayMode);
				config.setResizable(false);
				//config.setHdpiMode(H);
				new Lwjgl3Application(new MrRobotGame(), config);
			} else {
				System.out.println("Running game in window mode...");
				new Lwjgl3Application(new MrRobotGame(), cfg);
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Detects the display mode is most close to the game's HD resolution,
	 * while still allowing for the same aspect ratio when stretching the
	 * game vieport.
	 *
	 * @param width  The internal game screen width.
	 * @param height The internal game screen height.
	 * @return The matching display resolution to use (for fullscreen mode only).
	 */

	private static Graphics.DisplayMode findMatchingMode(int width, int height) {
		Graphics.DisplayMode displayMode = null;
		float targetRatio = (float) width / (float) height;

		System.out.println("Find matching mode for: " + width + " x " + height);

		List<Graphics.DisplayMode> validModes = new ArrayList<Graphics.DisplayMode>();
		Map<Graphics.DisplayMode, Float> modes = new HashMap<Graphics.DisplayMode, Float>();


		Graphics.DisplayMode perfectMode = null, nearPerfectMode = null;
		for (Graphics.DisplayMode mode : Lwjgl3ApplicationConfiguration.getDisplayModes()) {
			if (mode.width >= width && mode.height >= height) {
				float modeRatio = (float) mode.width / (float) mode.height;
				System.out.println("Valid mode: " + mode + ", ratio: " + modeRatio);
				validModes.add(mode);
				modes.put(mode, modeRatio);
				if (mode.width == width && mode.height == height) {
					perfectMode = mode;
				}
				if (modeRatio == targetRatio) {
					if (nearPerfectMode != null) {
						if (mode.width >= width && mode.height >= height) {
							nearPerfectMode = mode;
						}
					} else {
						nearPerfectMode = mode;
					}
				}
			}
		}

		float currentRatio = -1;
		for (Map.Entry<Graphics.DisplayMode, Float> mode : modes.entrySet()) {
			if (displayMode == null) {
				displayMode = mode.getKey();
				currentRatio = mode.getValue();
			}
			if (displayMode != mode.getKey()) {
				float diffCurrent = Math.abs(targetRatio - currentRatio);
				float diffNew = Math.abs(targetRatio - mode.getValue());
				if (diffNew < diffCurrent) {
					displayMode = mode.getKey();
					currentRatio = mode.getValue();
				}
			}
		}

		displayMode = perfectMode == null ? nearPerfectMode : perfectMode;
		displayMode = displayMode == null ? validModes.get(0) : displayMode;

		System.out.println("---- USE RESOLUTION: " + displayMode + " -------");
		return displayMode;
	}
}
