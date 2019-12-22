/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.joypad;

import games.play4ever.libgdx.PlatformUtil;

/**
 * Handles instantiating controller instances.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class Controllers implements IControllerListener {

	/** Stores controller listeners. */
	private ControllerListenerAdapter listenerAdapter = new ControllerListenerAdapter();
	
	/** The list of available controllers. */
	private IController[] controllers = new IController[0];
	
	/** The controller provider implementation. */
	private static IControllerProvider controllerProvider = null;
	
	/** Singleton instance of this class. */
	private static Controllers instance = new Controllers();
	
	/**
	 * Initializes the controller factory. Must be called once
	 * before the controllers can be used.
	 */
	public static void initialize() {
		String providerType = "Desktop";
		if(PlatformUtil.isOuya()) {
			providerType = "Ouya";
		}
		// TODO: Add more Android types (generic 4.x, Xperia Play, GameStik)
		try {
			String providerClassName = "com.jplay.gdx.controller." + providerType + "ControllerProvider";
			Class providerClass = Class.forName(providerClassName);
			controllerProvider = (IControllerProvider)providerClass.newInstance();
			controllerProvider.addListener(instance);
			controllerProvider.initialize();
			System.out.println("Controller provider ready: " + controllerProvider.getClass().getName());
			if(!controllerProvider.supportsCallbacks()) {
				Thread checkThread = new Thread(new ControllerCheckThread());
				checkThread.start();
				System.out.println("Controller check thread started.");
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to initialize controller provider instance: " + e);
		}
	}
	
	/**
	 * Returns the Controllers instance.
	 */
	public static Controllers instance() {
		return instance;
	}

	/**
	 * Lets the backend check the state of all controllers.
	 */
	public static void checkControllers() {
		controllerProvider.checkControllers();
	}
	
	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerListener#connected(com.jplay.gdx.joypad.IController)
	 */
	@Override
	public void connected(IController controller) {
		// First make sure it's not already in the list
		boolean addIt = true;
		for(IController oldController : this.controllers) {
			if(oldController.getID().equals(controller.getID())) {
				addIt = false;
			}
		}
		if(addIt) {
			IController[] newControllers = new IController[this.controllers.length + 1];
			System.arraycopy(this.controllers, 0, newControllers, 0, this.controllers.length);
			newControllers[newControllers.length - 1] = controller;
			this.controllers = newControllers;
			for(IControllerListener listener : this.listenerAdapter.getListeners()) {
				listener.connected(controller);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerListener#disConnected(com.jplay.gdx.joypad.IController)
	 */
	@Override
	public void disConnected(IController controller) {
		if(this.controllers.length > 0) {
			IController[] newControllers = new IController[this.controllers.length - 1];
			int ct = 0;
			for(IController oldController : this.controllers) {
				if(!oldController.getID().equals(controller.getID())) {
					newControllers[ct] = oldController;
				}
			}
			this.controllers = newControllers;
			for(IControllerListener listener : this.listenerAdapter.getListeners()) {
				listener.disConnected(controller);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerListener#buttonDown(com.jplay.gdx.joypad.IController, com.jplay.gdx.joypad.IButton, com.jplay.gdx.joypad.ButtonID)
	 */
	@Override
	public void buttonDown(IController controller, IButton button,
			ButtonID buttonID) {
		for(IControllerListener listener : this.listenerAdapter.getListeners()) {
			listener.buttonDown(controller, button, buttonID);
		}
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerListener#buttonUp(com.jplay.gdx.joypad.IController, com.jplay.gdx.joypad.IButton, com.jplay.gdx.joypad.ButtonID)
	 */
	@Override
	public void buttonUp(IController controller, IButton button,
			ButtonID buttonID) {
		for(IControllerListener listener : this.listenerAdapter.getListeners()) {
			listener.buttonUp(controller, button, buttonID);
		}
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IControllerListener#moveStick(com.jplay.gdx.joypad.IController, com.jplay.gdx.joypad.StickID)
	 */
	@Override
	public void moveStick(IController controller, StickID stick) {
		for(IControllerListener listener : this.listenerAdapter.getListeners()) {
			listener.moveStick(controller, stick);
		}
	}

	/**
	 * Returns all the available controllers.
	 * 
	 * @return The available controllers.
	 */
	public static IController[] getControllers() {
		return instance.controllers;
	}
	
	/**
	 * Registers a listener for controller events.
	 * 
	 * @param listener The controller listener.
	 */
	public void addListener(IControllerListener listener) {
		this.listenerAdapter.addListener(listener);
	}
	
	/**
	 * Removes a listener for controller events.
	 * 
	 * @param listener The controller listener to remove.
	 */
	public void removeListener(IControllerListener listener) {
		this.listenerAdapter.removeListener(listener);
	}
}
