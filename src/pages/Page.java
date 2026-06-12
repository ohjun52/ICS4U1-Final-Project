package pages;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.List;

import game_main.GameMain;

/**
 *
 * @author ohjun
 */
public class Page 
{
    public PApplet p;
    private PImage backgroundImage;
    private List<Button> buttons;

    public Page(PApplet p) 
	{
        this.p = p;
        this.buttons = new ArrayList<>();
    }

    public void setBackground(String image_path) 
	{
        try 
		{
			this.backgroundImage = p.loadImage(image_path);
        } 
		catch (Exception e) 
		{
            System.err.println("Failed to load background: " + image_path);
        }
    }

    public void addButton(Button button) 
	{
        buttons.add(button);
    }

    public void display() 
	{
        // Draw background
        if(backgroundImage != null) 
		{
            p.image(backgroundImage, 0, 0, p.width, p.height);
        } 
		else 
		{
            // Fallback color if no image is set
            p.background(100);
        }

        // Draw all buttons
        for (Button b : buttons) 
		{
            b.draw(p);
        }
    }

    public String handleMouseReleased() 
	{
		for (Button b : buttons) 
		{
			if (b.isMouseOver(p.mouseX, p.mouseY))
				return b.getLable(); 
		}
		return "";      
    }
	
	public void changeGameStatus(GameMain.Gamestate new_status)
	{
		GameMain.currentState = new_status;
	}
}