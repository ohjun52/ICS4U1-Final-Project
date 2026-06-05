package pages.implement;

import processing.core.PApplet;

import game_main.GameMain;
import pages.*;

/**
 *
 * @author ohjun
 */
public class TitlePage extends Page
{
	static final String BACKGROUND_PATH = "background_images/title_background.png";
	
	public TitlePage(PApplet p)
	{
		super(p);
		this.setBackground(BACKGROUND_PATH);
		this.addButton(new Button(650, 500, 400, 100, "START"));
		this.addButton(new Button(650, 700, 400, 100, "EXIT"));
	}
	
	public void run()
	{
		this.display();
	}
	
	public void getMouseReleased()
	{
		if(this.handleMouseReleased().equals("START"))
			this.changeGameStatus(GameMain.Gamestate.BATTLE);
		if(this.handleMouseReleased().equals("EXIT"))
			this.changeGameStatus(GameMain.Gamestate.EXIT);
	}
	
}
