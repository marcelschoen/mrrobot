/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx.controller;

import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;
import com.jplay.gdx.joypad.ControllerListenerAdapter;
import com.jplay.gdx.joypad.IController;
import com.jplay.gdx.joypad.IControllerListener;
import com.jplay.gdx.joypad.IControllerProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides controller instances on Desktop OS's (Linux, Windows, MacOS).
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class DesktopControllerProvider implements IControllerProvider {
	
	private static final int MAX_CONTROLLERS = 8;
	
	/** Stores controller listeners. */
	private ControllerListenerAdapter listeners = new ControllerListenerAdapter();

	/** Map of all connected OUYA controllers. */
	private Map<Controller, IController> connected = new HashMap<Controller, IController>();
	
	private static HIDManager hidManager = null;
	
	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerProvider#initialize()
	 */
	@Override
	public void initialize() {
		try {
			com.codeminders.hidapi.ClassPathLibraryLoader.loadNativeHIDLibrary();
			hidManager = HIDManager.getInstance();
			updateControllers();
			System.out.println("Desktop controller provider ready: " + hidManager);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to initialize Desktop controller provider: " + e, e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerProvider#release()
	 */
	@Override
	public void release() {
		// do nothing
		if(hidManager != null) {
			System.out.println("Release HID manager.");
			hidManager.release();
			hidManager = null;
		}
	}

	/**
	 * Updates the array of controllers. Invoke only when
	 * an update is really necessary (because this method creates new
	 * wrapper objects and changes the map of controllers).
	 */
	private void updateControllers() {
		try {
			this.connected.clear();
			HIDDeviceInfo[] devices;
			devices = hidManager.listDevices();
			for(HIDDeviceInfo device : devices) {
				System.out.println("------------ DEVICE INFO -------------");
				System.out.println(">          Product: " + device.getProduct_string());
				System.out.println(">       Product ID: " + device.getProduct_id());
				System.out.println(">   Release number: " + device.getRelease_number());
				System.out.println(">     Manufacturer: " + device.getManufacturer_string());
				System.out.println("> Interface number: " + device.getInterface_number());
				System.out.println(">    Serial number: " + device.getSerial_number());
				System.out.println(">            Usage: " + device.getUsage());
				System.out.println(">        Usage_page:" + device.getUsage_page());
				System.out.println(">           Vendor: " + device.getVendor_id());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Failed to update controllers: " + e, e);
		}
/*		
		Controller[] actualControllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		System.out.println("Number of controllers found: " + actualControllers.length);
		for(int ct = 0; ct < MAX_CONTROLLERS; ct++) {
			if(ct < actualControllers.length && actualControllers[ct] != null) {
				Controller desktopController = actualControllers[ct];
				IController controller = new DesktopControllerWrapper(desktopController);
				this.connected.put(desktopController, controller);
				for(IControllerListener listener : this.listeners.getListeners()) {
					listener.connected(controller);
				}
				printControllerInfo(false, desktopController);
			}
		}
		*/
	}
/*	
	public static void printControllerInfo(boolean isChild, Controller controller) {
		if(!isChild) {
			System.out.println("-------------------- CONTROLLER INFO ---------------------");
		}
		System.out.println("Controller: " + controller.getName() + " / Type: " + controller.getType()
				+ ", Port: " + controller.getPortNumber() + ", Port type: " + controller.getPortType()
				+ ", Rumblers: " + controller.getRumblers());
		Component[] components = controller.getComponents();
		for(Component component : components) {
			String name = component.getIdentifier().getName();
			System.out.println("-> component name: " + name + ", ID: " + component.getIdentifier()
					+ ", analog: " + component.isAnalog() + ", relative: " + component.isRelative()
					+ ", dead zone: " + component.getDeadZone() + ", value: " + component.getPollData());
		}
		if(controller.getControllers() != null && controller.getControllers().length > 0) {
			Controller[] childControllers = controller.getControllers();
			for(Controller child : childControllers) {
				printControllerInfo(true, child);
			}
		}
	}
*/
	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerProvider#checkControllers()
	 */
	@Override
	public void checkControllers() {
		boolean update = false;
/*
		Controller[] actualControllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(int ct = 0; ct < MAX_CONTROLLERS; ct++) {
			if(ct < actualControllers.length && actualControllers[ct] != null) {
				if(connected.get(actualControllers[ct]) == null) {
					// Newly connected controller found
					update = true;
					System.out.println("Newly connected Desktop controller found.");
				}
			}
		}
		*/
		if(update) {
			updateControllers();
		}
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerProvider#supportsCallbacks()
	 */
	@Override
	public boolean supportsCallbacks() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerProvider#addListener(com.jplay.gdx.joypad.IControllerListener)
	 */
	@Override
	public void addListener(IControllerListener listener) {
		this.listeners.addListener(listener);
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerProvider#removeListener(com.jplay.gdx.joypad.IControllerListener)
	 */
	@Override
	public void removeListener(IControllerListener listener) {
		this.listeners.removeListener(listener);
	}

}
