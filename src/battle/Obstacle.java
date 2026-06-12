package battle;

import processing.core.PApplet;
import processing.core.PImage;

public class Obstacle
{
	final static private String IMAGE_PREFIX = "animations/obstacles/obstacle_";
	final static private int IMAGE_COUNT = 5;
	
	final static private float COLLISION_WIDTH_RATIO = 1f;		// 碰撞箱宽度占显示宽度比例
	final static private float COLLISION_HEIGHT_RATIO = 0.7f;		// 碰撞箱高度占显示高度比例
	final static private float PARRY_WIDTH_RATIO = 0.8f;		// 跨跃判定宽度占显示宽度比例
	final static private float PARRY_HEIGHT_RATIO = 0.2f;		// 跨跃判定高度占显示高度比例（障碍物上方窄带）

	private static PImage[] obstacleImgs;
	private static boolean imagesLoaded = false;

	private PApplet p;
	private float w, h;
	private float x, y;
	private float speed;
	private PImage img;
	private boolean parried;

	public Obstacle(PApplet p, float w, float h, float x, float speed)
	{
		this.p = p;
		this.w = w;
		this.h = h;
		this.x = x;
		this.y = -h;
		this.speed = speed;
		this.parried = false;

		if (!imagesLoaded)
		{
			imagesLoaded = true;
			obstacleImgs = new PImage[IMAGE_COUNT];
			for (int i = 0; i < IMAGE_COUNT; i++)
			{
				String path = IMAGE_PREFIX + i + ".png";
				try { obstacleImgs[i] = p.loadImage(path); }
				catch (Exception e) { System.err.println("Failed to load: " + path); obstacleImgs[i] = null; }
			}
		}

		img = obstacleImgs[(int)(Math.random() * obstacleImgs.length)];
	}

	public void move()
	{
		y += speed;
	}

	public void draw()
	{
		if (img != null)
		{
			p.image(img, x, y, w, h);
		}
		else
		{
			p.fill(255, 0, 0);
			p.noStroke();
			p.rect(x, y, w, h);
		}
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

	public float getParryX()
	{
		return x + (w - w * PARRY_WIDTH_RATIO) / 2;
	}

	public float getParryY()
	{
		return y + h;
	}

	public float getParryW()
	{
		return w * PARRY_WIDTH_RATIO;
	}

	public float getParryH()
	{
		return h * PARRY_HEIGHT_RATIO;
	}

	public int framesToPass(float playerCollisionH)
	{
		return speed > 0 ? (int)((playerCollisionH + getH()) / speed) : 0;
	}

	public void parry()
	{
		parried = true;
	}

	public boolean isParried()
	{
		return parried;
	}

	// 调试：碰撞箱+跨跃判定区
	public void drawDebug()
	{
		p.noFill();
		p.stroke(255, 0, 0);
		p.rect(getX(), getY(), getW(), getH());
		p.stroke(255, 255, 0);
		p.rect(getParryX(), getParryY(), getParryW(), getParryH());
	}

	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
}
