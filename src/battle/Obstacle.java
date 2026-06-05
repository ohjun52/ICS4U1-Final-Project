package battle;

import processing.core.PApplet;

public class Obstacle
{
	private PApplet p;
	private float w, h;
	private float x, y;
	private float speed;

	public Obstacle(PApplet p, float w, float h, float x, float speed)
	{
		this.p = p;
		this.w = w;
		this.h = h;
		this.x = x;
		this.y = -h;
		this.speed = speed;
	}

	public void move()
	{
		y += speed;
	}

	public void draw()
	{
		p.fill(255, 0, 0);
		p.noStroke();
		p.rect(x, y, w, h);
	}

	public boolean isOffScreen(float screenHeight)
	{
		return y - h > screenHeight;
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public float getW()
	{
		return w;
	}

	public float getH()
	{
		return h;
	}

	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
}
