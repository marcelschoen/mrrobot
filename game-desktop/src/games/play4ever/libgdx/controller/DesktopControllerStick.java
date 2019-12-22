/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.controller;

import games.play4ever.libgdx.joypad.AbstractBaseStick;
import games.play4ever.libgdx.joypad.AxisID;
import games.play4ever.libgdx.joypad.IAxis;
import games.play4ever.libgdx.joypad.StickID;

/**
 * Stick implementation for Desktop controllers.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class DesktopControllerStick extends AbstractBaseStick {

	/** Constants for axes array. */
	private final static int X_AXIS = 0;
	private final static int Y_AXIS = 1;
	
	/** Stores references to the axes. */
	private IAxis[] axes = new IAxis[2];

	/**
	 * Creates a Desktop analog stick wrapper.
	 * 
	 * @param id The ID of the stick.
	 * @param controller The wrapped controller.
	 */
	public DesktopControllerStick(StickID id, Controller controller) {
		super(id);
		axes[X_AXIS] = new DesktopAxis(AxisID.X, this, controller);
		axes[Y_AXIS] = new DesktopAxis(AxisID.Y, this, controller);
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.AbstractBaseStick#getAxes()
	 */
	@Override
	public IAxis[] getAxes() {
		return this.axes;
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.AbstractBaseStick#getAxis(com.jplay.gdx.joypad.AxisID)
	 */
	@Override
	public IAxis getAxis(AxisID axis) throws IllegalArgumentException {
		if(axis == AxisID.X) {
			return this.axes[X_AXIS];
		}
		if(axis == AxisID.Y) {
			return this.axes[Y_AXIS];
		}
		throw new IllegalArgumentException("Axis '" + axis.name() + "' not supported by Desktop controller.");
	}
}
