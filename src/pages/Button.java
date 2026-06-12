package pages;

import processing.core.PApplet;

/**
 *
 * @author ohjun
 */
public class Button
{
	// Position and dimensions
    private float x, y, width, height;
    private String label;

    public Button(float x, float y, float width, float height, String label)
	{
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
    }

    public void draw(PApplet p)
	{
        // Check if mouse is hovering over the button for visual feedback
        if(isMouseOver(p.mouseX, p.mouseY)) 
		{
            p.fill(200); // Lighter gray on hover
        }
		else 
		{
            p.fill(100); // Darker gray default
        }
        
        p.stroke(255);
        p.rect(x, y, width, height);

        // Draw text label centered in the button
        p.fill(0);
        p.textAlign(PApplet.CENTER, PApplet.CENTER);
        p.textSize(20);
		
        // Center text based on button position and size
        p.text(label, x + width / 2, y + height / 2);
    }

    public boolean isMouseOver(float mx, float my)
	{
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }
	
	public String getLable()
	{
		return this.label;
	}
}
