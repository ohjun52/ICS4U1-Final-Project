package pages.implement;


import processing.core.PApplet;

import game_main.GameMain;
import pages.*;

/**
 *
 * @author ohjun
 */
public class StartPage extends Page
{
	static final String BACKGROUND_PATH = "";
	
	public StartPage(PApplet p)
	{ 
		super(p);
		this.setBackground(BACKGROUND_PATH);
		this.addButton(new Button(650, 700, 400, 100, "CONTINUE"));
	}
	
	public void run()
	{
		this.display();
	}
	
	public void getMouseReleased()
	{
		if(this.handleMouseReleased().equals("CONTINUE"))
			this.changeGameStatus(GameMain.Gamestate.EXIT);
	}
}
