package battle;

import processing.core.PApplet;

public class Battle
{
	private PApplet p;
	private Road road;

	public Battle(PApplet p)
	{
		this.p = p;
		this.road = new Road(p, p.width, p.height);
	}

	public void update()
	{
		road.update();
	}

	public void draw()
	{
		p.background(0);
		road.draw();
	}
}
