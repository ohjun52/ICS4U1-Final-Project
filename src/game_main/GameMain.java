package game_main;

import processing.core.PApplet;

import battle.Battle;
import battle.GameConfig;
import pages.implement.*;

public class GameMain extends PApplet {

	final static public int GAME_FRAME = 60;

	public static enum Gamestate
	{
		TITLE, START, TUTORIAL, BATTLE, END, EXIT
    }
	public static Gamestate currentState = Gamestate.TITLE;
	public static GameConfig.GameMode selectedMode = GameConfig.GameMode.ENDLESS;

	private TitlePage titlePage;
	private StartPage startPage;
	private TutorialPage tutorialPage;
	private EndPage endPage;
	private Battle battle;

	@Override
    public void settings()
	{
        fullScreen();
        size(1920, 1080);
		noSmooth();
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
        background(0);
		switch(currentState)
		{
			case TITLE:
				battle = null;
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
				exit();
				break;
		}
    }

	@Override
	public void keyReleased()
	{
		if (currentState == Gamestate.BATTLE)
		{
			if (key >= '1' && key <= '5')
			{
				battle.setPlayerLane(key - '1');
			}
			else if (key == ' ')
			{
				battle.playerParry();
			}
		}
	}

	@Override
	public void mouseReleased() 
	{
		if (mouseButton == LEFT) 
		{
			switch (currentState) 
			{
				case TITLE:
					titlePage.getMouseReleased();
					break;
				case START:
					startPage.getMouseReleased();
					break;
				case TUTORIAL:
					tutorialPage.getMouseReleased();
					break;
				case END:
					endPage.getMouseReleased();
					break;
				default:
					break;
			}
		}
	}

    public static void main(String[] args)
	{
        PApplet.main("game_main.GameMain");
    }
}
