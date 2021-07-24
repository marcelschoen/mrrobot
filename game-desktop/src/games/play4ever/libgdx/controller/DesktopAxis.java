/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.controller;

import games.play4ever.libgdx.joypad.AxisID;
import games.play4ever.libgdx.joypad.IAxis;
import games.play4ever.libgdx.joypad.IStick;

/**
 * Wrapper for an axis of a Desktop gamepad analog stick.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class DesktopAxis implements IAxis {

	/** Stores the ID of the axis. */
	private AxisID id = null;

	/** Reference to the stick which this axis belongs to. */
	private IStick stick = null;
	
	/** Reference to the Desktop controller. */
	private Controller controller = null;
	
	/** Stores the Desktop axis ID. */
	private int desktopAxisId = -1;
	
	/**
	 * Creates a wrapper for an analog stick axis.
	 * 
	 * @param id The ID of the axis.
	 * @param stick The stick to which the axis belongs.
	 * @param controller The wrapped controller.
	 */
	public DesktopAxis(AxisID id, IStick stick, Controller controller) {
		this.id = id;
		this.stick = stick;
		this.controller = controller;
		/*
		Component[] components = controller.getComponents();
		Gdx.app.log("-------- Controller: " + controller.getName() + ":" + controller.getPortNumber() + " ---------");
		for(Component component : components) {
			if(component.isAnalog()) {
				Gdx.app.log("> Component: " + component.getName() + " / ID: " + component.getIdentifier());
			}
		}
		*/
		/*
		if(this.stick.getID() == StickID.LEFT_ANALOG) {
			if(id == AxisID.X) {
				this.desktopAxisId = Controller.AXIS_LS_X;
			} else {
				this.desktopAxisId = Controller.AXIS_LS_Y;
			}
		} else if(this.stick.getID() == StickID.RIGHT_ANALOG) {
			if(id == AxisID.X) {
				this.desktopAxisId = Controller.AXIS_RS_X;
			} else {
				this.desktopAxisId = Controller.AXIS_RS_Y;
			}
		} else {
			throw new IllegalArgumentException("Stick not yet supported: " + stick.getID().name());
		}
		*/
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IAxis#getID()
	 */
	@Override
	public AxisID getID() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IAxis#getValue()
	 */
	@Override
	public float getValue() {
		/*
		float value = this.controller.getAxisValue(this.desktopAxisId);
        value = Math.min(value, 1.0f);
		return value;
		*/
		return 0f;
	}

}
