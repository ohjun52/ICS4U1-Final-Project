package pages.implement;

import processing.core.PApplet;

import game_main.GameMain;
import pages.*;

/**
 * Game-over or story-completion screen.
 * Player clicks CONTINUE to return to the title screen.
 */
public class EndPage extends Page
{
	static final String BACKGROUND_PATH = "background/end.png";
	/** Button width as fraction of screen width. */
	static final float BUTTON_W_RATIO = 0.2f;
	/** Button height as fraction of screen height. */
	static final float BUTTON_H_RATIO = 0.1f;
	/** X position of the CONTINUE button (right-aligned). */
	static final float BUTTON_X_RATIO = 0.75f;
	/** Y position of the CONTINUE button (near bottom). */
	static final float BUTTON_Y_RATIO = 0.85f;

	public EndPage(PApplet p)
	{
		super(p);
		float bw = p.width * BUTTON_W_RATIO;
		float bh = p.height * BUTTON_H_RATIO;
		this.setBackground(BACKGROUND_PATH);
		this.addButton(new Button(p.width * BUTTON_X_RATIO, p.height * BUTTON_Y_RATIO, bw, bh, "CONTINUE"));
	}

	public void run() { this.display(); }

	public void getMouseReleased()
	{
		if (this.handleMouseReleased().equals("CONTINUE"))
			GameMain.currentState = GameMain.Gamestate.TITLE;
	}
}
