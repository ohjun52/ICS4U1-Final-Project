package pages.implement;

import processing.core.PApplet;

import game_main.GameMain;
import pages.*;

/**
 * Intermediate screen shown after selecting STORY mode.
 * Player clicks CONTINUE to start the battle.
 */
public class StartPage extends Page
{
	static final String BACKGROUND_PATH = "background/start.png";
	/** Button width as fraction of screen width. */
	static final float BUTTON_W_RATIO = 0.2f;
	/** Button height as fraction of screen height. */
	static final float BUTTON_H_RATIO = 0.1f;
	/** Y position of the single CONTINUE button (near bottom). */
	static final float BUTTON_Y_RATIO = 0.75f;

	public StartPage(PApplet p)
	{
		super(p);
		float bw = p.width * BUTTON_W_RATIO;
		float bh = p.height * BUTTON_H_RATIO;
		float bx = (p.width - bw) / 2;
		this.setBackground(BACKGROUND_PATH);
		this.addButton(new Button(bx, p.height * BUTTON_Y_RATIO, bw, bh, "CONTINUE"));
	}

	public void run() { this.display(); }

	public void getMouseReleased()
	{
		if (this.handleMouseReleased().equals("CONTINUE"))
			GameMain.currentState = GameMain.Gamestate.TUTORIAL;
	}
}
