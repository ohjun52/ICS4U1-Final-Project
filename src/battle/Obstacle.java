package battle;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * A falling obstacle occupying one lane.
 * Each obstacle has a visible sprite, an AABB collision box, and a narrow
 * parry zone just below its visible bottom edge. Collision/parry box
 * dimensions are precomputed at construction since only the y coordinate
 * changes during movement.
 */
public class Obstacle
{
	/** Path prefix for obstacle sprite sheets. */
	final static private String IMAGE_PREFIX = "animations/obstacles/obstacle_";
	/** Number of distinct obstacle sprites available. */
	final static private int IMAGE_COUNT = 5;

	/** Fraction of display width used for the collision box. */
	final static private float COLLISION_WIDTH_RATIO = 1f;
	/** Fraction of display height used for the collision box. */
	final static private float COLLISION_HEIGHT_RATIO = 0.7f;
	/** Fraction of display width used for the parry zone. */
	final static private float PARRY_WIDTH_RATIO = 1f;
	/**
	 * Parry zone height as fraction of screen height.
	 * Using screen height keeps the zone a consistent pixel size regardless
	 * of the obstacle's own varying height.
	 */
	final static private float PARRY_HEIGHT_RATIO = 0.06f;

	/** Sprite array shared across all Obstacle instances. Loaded once. */
	private static PImage[] obstacleImgs;
	private static boolean imagesLoaded = false;

	private PApplet p;
	/** Display dimensions (full sprite size). */
	private float w, h;
	/** Top-left position. y changes each frame via move(). */
	private float x, y;
	/** Pixels to move down per frame. */
	private float speed;
	/** Randomly chosen sprite from the shared pool. */
	private PImage img;
	/** True after parry() or collision — queued for removal next frame. */
	private boolean parried;

	// Precomputed box dimensions (independent of y)
	private float colW, colH, colX;       // collision box
	private float parryW, parryH, parryX; // parry zone

	public Obstacle(PApplet p, float w, float h, float x, float speed)
	{
		this.p = p;
		this.w = w;
		this.h = h;
		this.x = x;
		this.y = -h;          // start above the visible screen
		this.speed = speed;
		this.parried = false;

		// Precompute collision box (centered horizontally, shorter vertically)
		colW = w * COLLISION_WIDTH_RATIO;
		colH = h * COLLISION_HEIGHT_RATIO;
		colX = x + (w - colW) / 2;

		// Precompute parry zone (narrow strip below the obstacle)
		parryW = w * PARRY_WIDTH_RATIO;
		parryH = p.height * PARRY_HEIGHT_RATIO;
		parryX = x + (w - parryW) / 2;

		// Load sprite pool once (static, shared by all instances)
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

	/** Move down by speed pixels each frame. */
	public void move() { y += speed; }

	/** Draw the sprite, or a red rectangle as fallback. */
	public void draw()
	{
		if (img != null)
			p.image(img, x, y, w, h);
		else
			{ p.fill(255, 0, 0); p.noStroke(); p.rect(x, y, w, h); }
	}

	/** True when the obstacle has completely left the bottom of the screen. */
	public boolean isOffScreen(float screenHeight) { return y - h > screenHeight; }

	/** Raw y for spawn-overlap check (package-private). */
	float getDisplayY() { return y; }
	/** Raw h for spawn-overlap check (package-private). */
	float getDisplayH() { return h; }

	// Collision box getters (using precomputed values)
	public float getX() { return colX; }
	public float getY() { return y + (h - colH) / 2; }
	public float getW() { return colW; }
	public float getH() { return colH; }

	// Parry zone getters
	public float getParryX() { return parryX; }
	public float getParryY() { return y + h; }        // starts at bottom of sprite
	public float getParryW() { return parryW; }
	public float getParryH() { return parryH; }

	/**
	 * Compute invincibility frames granted after the player passes through.
	 * Based on the combined height of both collision boxes divided by speed.
	 */
	public int framesToPass(float playerCollisionH)
	{
		return speed > 0 ? (int)((playerCollisionH + colH) / speed) : 0;
	}

	/** Mark for removal (used for both parry and collision). */
	public void parry() { parried = true; }
	public boolean isParried() { return parried; }
	public void setSpeed(float speed) { this.speed = speed; }

	/** Debug overlay: red = collision box, yellow = parry zone. */
	public void drawDebug()
	{
		p.noFill();
		p.stroke(255, 0, 0);   p.rect(getX(), getY(), colW, colH);
		p.stroke(255, 255, 0); p.rect(parryX, getParryY(), parryW, parryH);
	}
}
