package pages.implement;

import processing.core.PApplet;

import battle.GameConfig;
import game_main.GameMain;
import pages.*;

/**
 * Title screen. Player chooses game mode or exits.
 *
 * STORY → sets mode, goes to START page.
 * ENDLESS → sets mode, goes to TUTORIAL page.
 * EXIT → quits the application.
 */
public class TitlePage extends Page
{
	static final String BACKGROUND_PATH = "background/title.png";
	/** Button width as fraction of screen width (0.2 × 1920 = 384 px). */
	static final float BUTTON_W_RATIO = 0.2f;
	/** Button height as fraction of screen height (0.1 × 1080 = 108 px). */
	static final float BUTTON_H_RATIO = 0.1f;
	/** Y position of the first button (STORY). */
	static final float BUTTON_Y1_RATIO = 0.45f;
	/** Y position of the second button (ENDLESS). */
	static final float BUTTON_Y2_RATIO = 0.65f;
	/** Y position of the third button (EXIT). */
	static final float BUTTON_Y3_RATIO = 0.85f;

	public TitlePage(PApplet p)
	{
		super(p);
		float bw = p.width * BUTTON_W_RATIO;
		float bh = p.height * BUTTON_H_RATIO;
		float bx = (p.width - bw) / 2;   // centre horizontally
		this.setBackground(BACKGROUND_PATH);
		this.addButton(new Button(bx, p.height * BUTTON_Y1_RATIO, bw, bh, "STORY"));
		this.addButton(new Button(bx, p.height * BUTTON_Y2_RATIO, bw, bh, "ENDLESS"));
		this.addButton(new Button(bx, p.height * BUTTON_Y3_RATIO, bw, bh, "EXIT"));
	}

	public void run() { this.display(); }

	public void getMouseReleased()
	{
		String action = this.handleMouseReleased();
		if (action.equals("STORY"))
		{
			GameMain.selectedMode = GameConfig.GameMode.STORY;
			GameMain.currentState = GameMain.Gamestate.START;
		}
		else if (action.equals("ENDLESS"))
		{
			GameMain.selectedMode = GameConfig.GameMode.ENDLESS;
			GameMain.currentState = GameMain.Gamestate.TUTORIAL;
		}
		else if (action.equals("EXIT"))
		{
			GameMain.currentState = GameMain.Gamestate.EXIT;
		}
	}
}
