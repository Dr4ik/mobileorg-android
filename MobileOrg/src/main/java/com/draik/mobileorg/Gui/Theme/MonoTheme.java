package com.draik.mobileorg.Gui.Theme;

import android.graphics.Color;

public class MonoTheme extends DefaultTheme {

	public MonoTheme() {
		super();
		c0Black = Color.rgb(0xff, 0xff, 0xff);
		c1Red = Color.rgb(0xd0, 0x00, 0x00);
		c2Green = Color.rgb(0x00, 0x00, 0x00);
		c3Yellow = Color.rgb(0x00, 0x00, 0x00);
		c4Blue = Color.rgb(0x00, 0x00, 0x00);
		c5Purple = Color.rgb(0x00, 0x00, 0x00);
		c6Cyan = Color.rgb(0x00, 0x00, 0x00);
		c7White = Color.rgb(0x00, 0x00, 0x00);

		c9LRed = Color.rgb(0x00, 0x00, 0x00);
		caLGreen = Color.rgb(0x77, 0xff, 0x77);
		cbLYellow = Color.rgb(0x00, 0x00, 0x00);
		ccLBlue = Color.rgb(0x00, 0x00, 0x00);
		cdLPurple = Color.rgb(0x00, 0x00, 0x00);
		ceLCyan = Color.rgb(0x00, 0x00, 0x00);
		cfLWhite = Color.rgb(0x00, 0x00, 0x00);
		
		levelColors = new int[] { cfLWhite };
		
		defaultFontColor = "black";
		defaultBackground = Color.rgb(0xff, 0xff, 0xff);
		defaultForeground = Color.rgb(0x10, 0x10, 0x10);
	}
}
