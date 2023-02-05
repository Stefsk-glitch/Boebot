package com.roboworks.robot.hardware;

import TI.BoeBot;
import com.roboworks.robot.hardware.interfaces.Led;

import java.awt.*;

/**
 * Implementation of an led using a NeoPixel led.
 */
public class NeoPixel implements Led {
	private int id;
	private Color color;

	/**
	 * Constructs NeoPixel led with the specified id.
	 *
	 * @param id Id of the NeoPixel.
	 */
	public NeoPixel(int id) {
		this.id = id;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
		BoeBot.rgbSet(this.id, color);
		BoeBot.rgbShow();
	}

	@Override
	public void setColor(int red, int green, int blue) {
		this.color = new Color(red, green, blue);
		BoeBot.rgbSet(this.id, red, green, blue);
		BoeBot.rgbShow();
	}

	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public void update() {

	}
}
