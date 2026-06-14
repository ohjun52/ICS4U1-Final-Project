package pages;

import game_main.GameMain;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all menu / UI pages.
 *
 * Each page has an optional background image and a list of clickable
 * buttons. Subclasses override {@code run()} and {@code getMouseReleased()}
 * to define page-specific behaviour.
 */
public class Page
{
	/** Processing applet for drawing and input. */
	public PApplet p;
	/** Background image drawn behind buttons (may be null). */
	private PImage backgroundImage;
	/** Clickable buttons on this page. */
	private List<Button> buttons;

	public Page(PApplet p)
	{
		this.p = p;
		this.buttons = new ArrayList<>();
	}

	/**
	 * Load and set the background image.
	 * Failure is non-fatal — a grey fallback is used instead.
	 */
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

	/** Register a button to be drawn and receive clicks. */
	public void addButton(Button button)
	{
		buttons.add(button);
	}

	/** Draw background then all buttons. */
	public void display()
	{
		if (backgroundImage != null)
			p.image(backgroundImage, 0, 0, p.width, p.height);
		else
			p.background(100);   // mid-grey fallback

		for (Button b : buttons)
			b.draw(p);
	}

	/**
	 * Check every button for a mouse-over on release.
	 * @return the label of the clicked button, or empty string if none matched.
	 */
	public String handleMouseReleased()
	{
		for (Button b : buttons)
		{
			if (b.isMouseOver(p.mouseX, p.mouseY))
				return b.getLable();
		}
		return "";
	}

	/** Convenience shortcut to change the global game state. */
	public void changeGameStatus(GameMain.Gamestate new_status)
	{
		GameMain.currentState = new_status;
	}
}
