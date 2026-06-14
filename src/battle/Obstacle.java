package battle;

import processing.core.PApplet;
import processing.core.PImage;

public class Obstacle
{
	final static private String IMAGE_PREFIX = "animations/obstacles/obstacle_";
	final static private int IMAGE_COUNT = 5;

	final static private float COLLISION_WIDTH_RATIO = 1f;
	final static private float COLLISION_HEIGHT_RATIO = 0.7f;
	final static private float PARRY_WIDTH_RATIO = 1f;
	final static private float PARRY_HEIGHT_RATIO = 0.06f;

	private static PImage[] obstacleImgs;
	private static boolean imagesLoaded = false;

	private PApplet p;
	private float w, h;
	private float x, y;
	private float speed;
	private PImage img;
	private boolean parried;

	// 预计算的碰撞箱/跨跃判定箱常量（不随 y 变化）
	private float colW, colH, colX;
	private float parryW, parryH, parryX;

	public Obstacle(PApplet p, float w, float h, float x, float speed)
	{
		this.p = p;
		this.w = w;
		this.h = h;
		this.x = x;
		this.y = -h;
		this.speed = speed;
		this.parried = false;

		// 预计算固定偏移
		colW = w * COLLISION_WIDTH_RATIO;
		colH = h * COLLISION_HEIGHT_RATIO;
		colX = x + (w - colW) / 2;
		parryW = w * PARRY_WIDTH_RATIO;
		parryH = p.height * PARRY_HEIGHT_RATIO;
		parryX = x + (w - parryW) / 2;

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
		if (img != null) p.image(img, x, y, w, h);
		else { p.fill(255, 0, 0); p.noStroke(); p.rect(x, y, w, h); }
	}

	public boolean isOffScreen(float screenHeight) { return y - h > screenHeight; }

	float getDisplayY() { return y; }
	float getDisplayH() { return h; }

	public float getX() { return colX; }
	public float getY() { return y + (h - colH) / 2; }
	public float getW() { return colW; }
	public float getH() { return colH; }

	public float getParryX() { return parryX; }
	public float getParryY() { return y + h; }
	public float getParryW() { return parryW; }
	public float getParryH() { return parryH; }

	public int framesToPass(float playerCollisionH)
	{
		return speed > 0 ? (int)((playerCollisionH + colH) / speed) : 0;
	}

	public void parry() { parried = true; }
	public boolean isParried() { return parried; }
	public void setSpeed(float speed) { this.speed = speed; }

	public void drawDebug()
	{
		p.noFill();
		p.stroke(255, 0, 0);
		p.rect(getX(), getY(), colW, colH);
		p.stroke(255, 255, 0);
		p.rect(parryX, getParryY(), parryW, parryH);
	}
}
