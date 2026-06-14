package battle;

import processing.core.PApplet;
import processing.core.PImage;

public class PlayerAnimation
{
	final static public int IDLE = 0;
	final static public int MOVING = 1;
	final static public int HIT = 2;
	final static public int PARRY = 3;
	final static public int DEATH = 4;

	final static private String IDLE_PATH = "animations/run.png";
	final static private int IDLE_FRAMES = 12;
	final static private String MOVE_PATH = "animations/move.png";
	final static private int MOVE_FRAMES = 61;
	final static private String HIT_PATH = "animations/hit.png";
	final static int HIT_FRAMES = 6;
	final static private String PARRY_PATH = "animations/parry.png";
	final static int PARRY_FRAMES = 11;
	final static private String DEATH_PATH = "animations/death.png";
	final static private int DEATH_FRAMES = 10;

	final static int FRAME_DELAY = 5;

	final static private float[] OFFSET_X = {0, 0, 0, 0, 0};
	final static private float[] OFFSET_Y = {-0.08f, 0, 0, 0, -0.08f};

	private PApplet p;
	private PImage idleSheet, moveSheet, hitSheet, parrySheet, deathSheet;
	private int idleFrameWidth, idleFrameHeight;
	private int moveFrameWidth, moveFrameHeight;
	private int hitFrameWidth, hitFrameHeight;
	private int parryFrameWidth, parryFrameHeight;
	private int deathFrameWidth, deathFrameHeight;

	private boolean hasImages;

	private int state;
	private int currentFrame;
	private int frameTimer;
	private boolean looped;

	public PlayerAnimation(PApplet p)
	{
		this.p = p;
		this.state = IDLE;
		this.currentFrame = 0;
		this.frameTimer = 0;
		this.hasImages = false;
		this.looped = false;

		try { idleSheet = p.loadImage(IDLE_PATH); }
		catch (Exception e) { System.err.println("Failed to load: " + IDLE_PATH); idleSheet = null; }

		try { moveSheet = p.loadImage(MOVE_PATH); }
		catch (Exception e) { System.err.println("Failed to load: " + MOVE_PATH); moveSheet = null; }

		try { hitSheet = p.loadImage(HIT_PATH); }
		catch (Exception e) { System.err.println("Failed to load: " + HIT_PATH); hitSheet = null; }

		try { parrySheet = p.loadImage(PARRY_PATH); }
		catch (Exception e) { System.err.println("Failed to load: " + PARRY_PATH); parrySheet = null; }

		try { deathSheet = p.loadImage(DEATH_PATH); }
		catch (Exception e) { System.err.println("Failed to load: " + DEATH_PATH); deathSheet = null; }

		if (idleSheet != null && idleSheet.width > 0)
		{
			idleFrameWidth = idleSheet.width / IDLE_FRAMES;
			idleFrameHeight = idleSheet.height;
		}
		if (moveSheet != null && moveSheet.width > 0)
		{
			moveFrameWidth = moveSheet.width / MOVE_FRAMES;
			moveFrameHeight = moveSheet.height;
		}
		if (hitSheet != null && hitSheet.width > 0)
		{
			hitFrameWidth = hitSheet.width / HIT_FRAMES;
			hitFrameHeight = hitSheet.height;
		}
		if (parrySheet != null && parrySheet.width > 0)
		{
			parryFrameWidth = parrySheet.width / PARRY_FRAMES;
			parryFrameHeight = parrySheet.height;
		}
		if (deathSheet != null && deathSheet.width > 0)
		{
			deathFrameWidth = deathSheet.width / DEATH_FRAMES;
			deathFrameHeight = deathSheet.height;
		}

		hasImages = idleSheet != null && moveSheet != null
			&& hitSheet != null && parrySheet != null && deathSheet != null;
	}

	public void setState(int state)
	{
		if (state < IDLE || state > DEATH) return;
		if (this.state != state)
		{
			this.state = state;
			this.currentFrame = 0;
			this.frameTimer = 0;
			this.looped = false;
		}
	}

	public int getState() { return state; }

	public void restart()
	{
		this.currentFrame = 0;
		this.frameTimer = 0;
		this.looped = false;
	}

	public boolean isFinished()
	{
		if (state == IDLE || state == MOVING) return false;
		return looped;
	}

	public void update()
	{
		frameTimer++;
		if (frameTimer >= FRAME_DELAY)
		{
			frameTimer = 0;
			int frames = frameCount();
			if (looped) return;

			currentFrame++;
			if (currentFrame >= frames)
			{
				if (state == IDLE || state == MOVING)
					currentFrame = 0;
				else
				{
					currentFrame = frames - 1;
					looped = true;
				}
			}
		}
	}

	private int frameCount()
	{
		switch (state)
		{
			case MOVING: return MOVE_FRAMES;
			case HIT:    return HIT_FRAMES;
			case PARRY:  return PARRY_FRAMES;
			case DEATH:  return DEATH_FRAMES;
			default:     return IDLE_FRAMES;
		}
	}

	public void draw(float x, float y, float w, float h, boolean invincible)
	{
		if (invincible && p.frameCount % 6 < 3) return;

		float ox = w * OFFSET_X[state];
		float oy = h * OFFSET_Y[state];

		if (hasImages && sheetForState() != null)
		{
			PImage sheet = sheetForState();
			int fw = frameWidthForState();
			int fh = frameHeightForState();
			int sx = currentFrame * fw;
			p.image(sheet, x + ox, y + oy, w, h, sx, 0, sx + fw, fh);
		}
		else
		{
			p.noStroke();
			switch (state)
			{
				case DEATH:  p.fill(80, 80, 80);    break;
				case HIT:    p.fill(255, 80, 80);   break;
				case PARRY:  p.fill(255, 255, 80);  break;
				case MOVING: p.fill(255, 200, 0);   break;
				default:     p.fill(invincible ? 255 : 0, 255, 255); break;
			}
			p.rect(x + ox, y + oy, w, h);
		}
	}

	private PImage sheetForState()
	{
		switch (state)
		{
			case MOVING: return moveSheet;
			case HIT:    return hitSheet;
			case PARRY:  return parrySheet;
			case DEATH:  return deathSheet;
			default:     return idleSheet;
		}
	}

	private int frameWidthForState()
	{
		switch (state)
		{
			case MOVING: return moveFrameWidth;
			case HIT:    return hitFrameWidth;
			case PARRY:  return parryFrameWidth;
			case DEATH:  return deathFrameWidth;
			default:     return idleFrameWidth;
		}
	}

	private int frameHeightForState()
	{
		switch (state)
		{
			case MOVING: return moveFrameHeight;
			case HIT:    return hitFrameHeight;
			case PARRY:  return parryFrameHeight;
			case DEATH:  return deathFrameHeight;
			default:     return idleFrameHeight;
		}
	}
}
