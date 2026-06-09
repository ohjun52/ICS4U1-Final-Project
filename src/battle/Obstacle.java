package battle;

import processing.core.PApplet;

public class Obstacle
{
	final static private float COLLISION_WIDTH_RATIO = 0.6f;		// 碰撞箱宽度占显示宽度比例
	final static private float COLLISION_HEIGHT_RATIO = 1f;	// 碰撞箱高度占显示高度比例

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
		return x + (w - w * COLLISION_WIDTH_RATIO) / 2;
	}

	public float getY()
	{
		return y + (h - h * COLLISION_HEIGHT_RATIO) / 2;
	}

	public float getW()
	{
		return w * COLLISION_WIDTH_RATIO;
	}

	public float getH()
	{
		return h * COLLISION_HEIGHT_RATIO;
	}

	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
}
