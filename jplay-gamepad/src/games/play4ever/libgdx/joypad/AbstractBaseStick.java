/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.joypad;

/**
 * Abstract base class for stick wrappers.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public abstract class AbstractBaseStick implements IStick {

	/** Stores the ID of this stick. */
	protected StickID ID = StickID.UNKNOWN;

	/** The stick position data holder. */
	protected StickPosition position = new StickPosition();
	
	/** Holds direct reference to the X-axis. */
	protected IAxis xAxis = null;
	
	/** Holds direct reference to the Y-axis. */
	protected IAxis yAxis = null;
	
	/**
	 * Creates a stick wrapper.
	 * 
	 * @param ID The ID of this stick.
	 */
	protected AbstractBaseStick(StickID ID) {
		this.ID = ID;
	}
	
	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IStick#getID()
	 */
	@Override
	public StickID getID() {
		return this.ID;
	}

	/**
	 * Convenience method which provides pre-processed status
	 * information about the stick, such as the degree in which
	 * it is currently held, and the distance to the center.
	 * 
	 * @return The stick position data holder.
	 */
	public StickPosition getPosition() {
		if(this.xAxis == null) {
			this.xAxis = getAxis(AxisID.X);
			this.yAxis = getAxis(AxisID.Y);
		}
		this.position.update(this.xAxis.getValue(), this.yAxis.getValue());
		return this.position;
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IStick#getAxes()
	 */
	@Override
	public abstract IAxis[] getAxes();

	/* (non-Javadoc)
	 * @see com.jplay.gdx.joypad.IStick#getAxis(com.jplay.gdx.joypad.AxisID)
	 */
	@Override
	public abstract IAxis getAxis(AxisID axis) throws IllegalArgumentException;

}
