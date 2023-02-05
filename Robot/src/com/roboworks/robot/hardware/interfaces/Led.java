package com.roboworks.robot.hardware.interfaces;

import com.roboworks.robot.util.Updatable;

import java.awt.*;

/**
 * Controls Leds.
 */
public interface Led extends Updatable {
	/**
	 * Set the led to the specified color.
	 *
	 * @param color Color to set the led to.
	 */
	void setColor(Color color);

	/**
	 * Set the led to the specified color.
	 *
	 * @param red   Red value to set the led to.
	 * @param green Green value to set the led to.
	 * @param blue  Blue value to set the led to.
	 */
	void setColor(int red, int green, int blue);

	/**
	 * Returns the current color of the led.
	 *
	 * @return Color of the led.
	 */
	Color getColor();
}
