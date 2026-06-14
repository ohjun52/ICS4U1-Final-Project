package pages;

import processing.core.PApplet;

/**
 * A clickable rectangular button with a centred text label.
 */
public class Button
{
	/** Top-left corner and dimensions in pixels. */
	private float x, y, width, height;
	/** Text displayed on the button. */
	private String label;

	public Button(float x, float y, float width, float height, String label)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.label = label;
	}

	/**
	 * Draw the button. Lighter shade on hover for visual feedback.
	 */
	public void draw(PApplet p)
	{
		if (isMouseOver(p.mouseX, p.mouseY))
			p.fill(200);   // light grey — hover
		else
			p.fill(100);   // dark grey — idle

		p.stroke(255);     // white outline
		p.rect(x, y, width, height);

		// Centred black label
		p.fill(0);
		p.textAlign(PApplet.CENTER, PApplet.CENTER);
		p.textSize(20);
		p.text(label, x + width / 2, y + height / 2);
	}

	/** True if the given point is inside this button. */
	public boolean isMouseOver(float mx, float my)
	{
		return mx >= x && mx <= x + width && my >= y && my <= y + height;
	}

	public String getLable() { return this.label; }
}
