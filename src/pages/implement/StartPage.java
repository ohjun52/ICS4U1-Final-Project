package pages.implement;


import processing.core.PApplet;

import battle.GameConfig;
import game_main.GameMain;
import pages.*;

/**
 *
 * @author ohjun
 */
public class StartPage extends Page
{
	static final String BACKGROUND_PATH = "123";

	public StartPage(PApplet p)
	{
		super(p);
		this.setBackground(BACKGROUND_PATH);
		this.addButton(new Button(650, 500, 400, 100, "STORY"));
		this.addButton(new Button(650, 700, 400, 100, "ENDLESS"));
	}

	public void run()
	{
		this.display();
	}

	public void getMouseReleased()
	{
		String action = this.handleMouseReleased();
		if (action.equals("STORY"))
		{
			GameMain.selectedMode = GameConfig.GameMode.STORY;
			GameMain.currentState = GameMain.Gamestate.BATTLE;
		}
		else if (action.equals("ENDLESS"))
		{
			GameMain.selectedMode = GameConfig.GameMode.ENDLESS;
			GameMain.currentState = GameMain.Gamestate.BATTLE;
		}
	}
}
