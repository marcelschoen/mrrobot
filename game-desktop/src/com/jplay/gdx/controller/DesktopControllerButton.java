/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx.controller;

import com.jplay.gdx.joypad.AbstractBaseButton;
import com.jplay.gdx.joypad.ButtonID;

/**
 * Wrapper for Desktop controller button.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class DesktopControllerButton extends AbstractBaseButton {

	/** Controller to which this button belongs. */
	private Controller controller = null;

	/** The numeric code of the OUYA controller button. */
	private int code = -1;
	
	/**
	 * Creates a new OUYA controller button wrapper.
	 * 
	 * @param id The ID of the button.
	 * @param isAnalog True if it is an analog button.
	 * @param label The english default text label for this button.
	 * @param labelKey The message resource key for the text label (may be null,
	 *                 in which case the given label will be used). If a valid
	 *                 key is provided, it can be used later to show localized
	 *                 button labels to the player.
	 * @param controller The OUYA controller to which this button belongs.
	 */
	public DesktopControllerButton(ButtonID id, String label, String labelKey, Controller controller) {
		super(id, false, label, labelKey);
		this.controller = controller;
		/*
		if(id == ButtonID.ACCEPT) {
			this.code = Controller.BUTTON_O;
		} else if(id == ButtonID.BACK) {
			this.code = Controller.BUTTON_A;
		} else if(id == ButtonID.CANCEL) {
			this.code = Controller.BUTTON_A;
		} else if(id == ButtonID.FACE_DOWN) {
			this.code = Controller.BUTTON_O;
		} else if(id == ButtonID.FACE_LEFT) {
			this.code = Controller.BUTTON_U;
		} else if(id == ButtonID.FACE_RIGHT) {
			this.code = Controller.BUTTON_A;
		} else if(id == ButtonID.FACE_UP) {
			this.code = Controller.BUTTON_Y;
		} else if(id == ButtonID.TRIGGER1_LEFT) {
			this.code = Controller.BUTTON_L1;
		} else if(id == ButtonID.TRIGGER2_LEFT) {
			this.isAnalog = true;
			this.code = Controller.AXIS_L2;
		} else if(id == ButtonID.TRIGGER1_RIGHT) {
			this.code = Controller.BUTTON_R1;
		} else if(id == ButtonID.TRIGGER2_RIGHT) {
			this.isAnalog = true;
			this.code = Controller.AXIS_R2;
		} else if(id == ButtonID.TRIGGER1_LEFT) {
			this.code = Controller.BUTTON_L1;
		} else if(id == ButtonID.TRIGGER1_LEFT) {
			this.code = Controller.BUTTON_L1;
		} else if(id == ButtonID.MENU) {
			this.code = Controller.BUTTON_MENU;
		} else if(id == ButtonID.HOME) {
			this.code = Controller.BUTTON_MENU;
		}
		*/
	}
	
	/**
	 * Creates a new OUYA controller button wrapper.
	 * 
	 * @param code The numeric code of the button.
	 * @param isAnalog True if it is an analog button.
	 * @param label The english default text label for this button.
	 * @param labelKey The message resource key for the text label (may be null,
	 *                 in which case the given label will be used). If a valid
	 *                 key is provided, it can be used later to show localized
	 *                 button labels to the player.
	 * @param controller The OUYA controller to which this button belongs.
	 */
	public DesktopControllerButton(int code, boolean isAnalog, String label, String labelKey, Controller controller) {
		super(code, isAnalog, label, labelKey);
		this.controller = controller;
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IButton#isPressed()
	 */
	@Override
	public boolean isPressed() {
		return false;
//		return this.controller.getButton(this.code);
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IButton#analogValue()
	 */
	@Override
	public float analogValue() {
		return 0f;
//		return this.controller.getAxisValue(this.code);
	}
}
