package pages.implement;


import processing.core.PApplet;

import game_main.GameMain;
import pages.*;

/**
 *
 * @author ohjun
 */
public class EndPage extends Page
{
	static final String BACKGROUND_PATH = "background/end.png";
	static final float BUTTON_W_RATIO = 0.2f;
	static final float BUTTON_H_RATIO = 0.1f;
	static final float BUTTON_X_RATIO = 0.75f;
	static final float BUTTON_Y_RATIO = 0.85f;

	public EndPage(PApplet p)
	{
		super(p);
		float bw = p.width * BUTTON_W_RATIO;
		float bh = p.height * BUTTON_H_RATIO;
		this.setBackground(BACKGROUND_PATH);
		this.addButton(new Button(p.width * BUTTON_X_RATIO, p.height * BUTTON_Y_RATIO, bw, bh, "CONTINUE"));
	}

	public void run()
	{
		this.display();
	}

	public void getMouseReleased()
	{
		String action = this.handleMouseReleased();
		if (action.equals("CONTINUE"))
		{
			GameMain.currentState = GameMain.Gamestate.TITLE;
		}
	}
}
