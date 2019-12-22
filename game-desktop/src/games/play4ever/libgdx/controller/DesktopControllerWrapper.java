/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.controller;

import games.play4ever.libgdx.joypad.AbstractBaseController;
import games.play4ever.libgdx.joypad.AxisID;
import games.play4ever.libgdx.joypad.DpadDirection;
import games.play4ever.libgdx.joypad.IStick;
import games.play4ever.libgdx.joypad.StickID;

/**
 * Wrapper class for a JInput game controller.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class DesktopControllerWrapper extends AbstractBaseController {

	/** Reference to the wrapped controller. */
	private Controller wrappedController = null;
	
	/** Reference to analog sticks. */
	private IStick[] sticks = new IStick[2];
	
	/** Constants for accessing sticks array. */
	private final static int LEFT_STICK = 0;
	private final static int RIGHT_STICK = 1;

	/** Array with possible value combinations for d-pad. */
	private final static DpadDirection[] directions = new DpadDirection[] {
		DpadDirection.NONE, // 0
		DpadDirection.UP, // 1
		DpadDirection.RIGHT, // 2
		DpadDirection.UP_RIGHT, // 3 (1 + 2)
		DpadDirection.DOWN, // 4
		null,
		DpadDirection.DOWN_RIGHT, // 6 (4 + 2)
		null,
		DpadDirection.LEFT, // 8
		DpadDirection.UP_LEFT, // 9 (8 + 1)
		null,
		null,
		DpadDirection.DOWN_LEFT // 12 (8 + 4)
	};

	/**
	 * Creates a wrapper for a controller.
	 * 
	 * @param wrappedController The wrapped controller.
	 */
	public DesktopControllerWrapper(Controller wrappedController) {
		super("");
//		super(String.valueOf("Desktop:" + wrappedController.getName() + ":" + wrappedController.getPortNumber()));
		this.wrappedController = wrappedController;
	}
	
	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IController#getID()
	 */
	@Override
	public String getID() {
		return "";
//		return this.wrappedController.getName() + ":" + this.wrappedController.getPortNumber();
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IController#getSticks()
	 */
	@Override
	public IStick[] getSticks() {
		return this.sticks;
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IController#getStick(com.jplay.gdx.joypad.StickID)
	 */
	@Override
	public IStick getStick(StickID stick) throws IllegalArgumentException {
		if(stick == StickID.LEFT_ANALOG) {
			return this.sticks[LEFT_STICK];
		} else if(stick == StickID.RIGHT_ANALOG) {
			return this.sticks[RIGHT_STICK];
		}
		throw new IllegalArgumentException("Stick '" + stick.name() + "' not supported by OUYA controller.");
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IController#getAxis(com.jplay.gdx.joypad.StickID, com.jplay.gdx.joypad.AxisID)
	 */
	@Override
	public float getAxis(StickID stick, AxisID axis) {
		/*
		if(stick == StickID.LEFT_ANALOG) {
			if(axis == AxisID.X) {
				return this.wrappedController.getAxisValue(Controller.AXIS_LS_X);
			} else if(axis == AxisID.Y) {
				return this.wrappedController.getAxisValue(Controller.AXIS_LS_Y);
			}
		} else if(stick == StickID.RIGHT_ANALOG) {
			if(axis == AxisID.X) {
				return this.wrappedController.getAxisValue(Controller.AXIS_RS_X);
			} else if(axis == AxisID.Y) {
				return this.wrappedController.getAxisValue(Controller.AXIS_RS_Y);
			}
		}
		throw new IllegalArgumentException("Stick '" + stick.name() + "' with axis '" 
				+ axis.name() + "' not supported by OUYA controller.");
				*/
		return 0f;
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IController#getDpadDirection()
	 */
	@Override
	public DpadDirection getDpadDirection() {
		int direction = 0;
		/*
		if(this.wrappedController.getButton(Controller.BUTTON_DPAD_UP)) {
			direction += 1;
		}
		if(this.wrappedController.getButton(Controller.BUTTON_DPAD_RIGHT)) {
			direction += 2;
		}
		if(this.wrappedController.getButton(Controller.BUTTON_DPAD_DOWN)) {
			direction += 4;
		}
		if(this.wrappedController.getButton(Controller.BUTTON_DPAD_LEFT)) {
			direction += 8;
		}
		*/
		return directions[direction];
	}
}
