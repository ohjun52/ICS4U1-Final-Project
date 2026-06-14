package game_main;

import processing.core.PApplet;

import battle.Battle;
import battle.GameConfig;
import pages.implement.*;

/**
 * Application entry point and top-level state machine.
 *
 * State flow:
 * TITLE → (STORY) → START → BATTLE → END → TITLE
 * TITLE → (ENDLESS) → TUTORIAL → BATTLE → TITLE
 * TITLE → (EXIT) → exit()
 */
public class GameMain extends PApplet {

	/** Target frame rate. */
	final static public int GAME_FRAME = 60;

	/** All possible application states. */
	public static enum Gamestate
	{
		TITLE, START, TUTORIAL, BATTLE, END, EXIT
	}
	/** Currently active state — drives the draw() switch. */
	public static Gamestate currentState = Gamestate.TITLE;
	/** Game mode chosen on the title screen. */
	public static GameConfig.GameMode selectedMode = GameConfig.GameMode.ENDLESS;

	// Pre-instantiated pages (created once at startup)
	private TitlePage titlePage;
	private StartPage startPage;
	private TutorialPage tutorialPage;
	private EndPage endPage;
	/** Created lazily when entering BATTLE, nulled on TITLE for reset. */
	private Battle battle;

	@Override
	public void settings()
	{
		fullScreen();          // use entire monitor
		size(1920, 1080);      // design resolution
		noSmooth();            // crisp pixel art
	}

	@Override
	public void setup()
	{
		frameRate(GAME_FRAME);

		titlePage = new TitlePage(this);
		startPage = new StartPage(this);
		tutorialPage = new TutorialPage(this);
		endPage = new EndPage(this);
	}

	@Override
	public void draw()
	{
		background(0);   // black filler behind pages
		switch (currentState)
		{
			case TITLE:
				battle = null;          // reset for a fresh game
				titlePage.run();
				break;
			case START:
				startPage.run();
				break;
			case TUTORIAL:
				tutorialPage.run();
				break;
			case BATTLE:
				if (battle == null) battle = new Battle(this, selectedMode);
				battle.update();
				battle.draw();
				break;
			case END:
				endPage.run();
				break;
			case EXIT:
				exit();                 // terminate the application
				break;
		}
	}

	@Override
	public void keyReleased()
	{
		// Only the battle scene uses keyboard input
		if (currentState == Gamestate.BATTLE)
		{
			if (key >= '1' && key <= '5')
				battle.setPlayerLane(key - '1');   // lane 1-5 → index 0-4
			else if (key == ' ')
				battle.playerParry();              // space = parry
		}
	}

	@Override
	public void mouseReleased()
	{
		if (mouseButton == LEFT)
		{
			// Delegate click to the active page
			switch (currentState)
			{
				case TITLE:     titlePage.getMouseReleased();    break;
				case START:     startPage.getMouseReleased();    break;
				case TUTORIAL:  tutorialPage.getMouseReleased(); break;
				case END:       endPage.getMouseReleased();      break;
				default:        break;   // BATTLE, EXIT ignore mouse
			}
		}
	}

	public static void main(String[] args)
	{
		PApplet.main("game_main.GameMain");
	}
}
