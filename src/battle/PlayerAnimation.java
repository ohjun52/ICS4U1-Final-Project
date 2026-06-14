package battle;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Sprite-sheet animation system with five states.
 *
 * Looping states: IDLE (12 frames), MOVING (61 frames).
 * One-shot states: HIT (6 frames), PARRY (11 frames), DEATH (10 frames).
 * Frame advancement is throttled by FRAME_DELAY to keep animations at
 * a readable speed regardless of the 60 fps game loop.
 */
public class PlayerAnimation
{
	// ---- State constants ----
	final static public int IDLE = 0;
	final static public int MOVING = 1;
	final static public int HIT = 2;
	final static public int PARRY = 3;
	final static public int DEATH = 4;

	// ---- Sprite sheet paths and frame counts ----
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

	/** Game frames to wait before advancing to the next sprite frame. */
	final static int FRAME_DELAY = 5;

	/**
	 * Per-state position offsets, as fraction of display size.
	 * Used to align sprite sheets that have different character positions.
	 */
	final static private float[] OFFSET_X = {0, 0, 0, 0, 0};
	final static private float[] OFFSET_Y = {-0.08f, 0, 0, 0, -0.08f};

	private PApplet p;
	// Loaded sprite sheets (one per state)
	private PImage idleSheet, moveSheet, hitSheet, parrySheet, deathSheet;
	// Pixel dimensions of a single frame in each sheet
	private int idleFrameWidth, idleFrameHeight;
	private int moveFrameWidth, moveFrameHeight;
	private int hitFrameWidth, hitFrameHeight;
	private int parryFrameWidth, parryFrameHeight;
	private int deathFrameWidth, deathFrameHeight;

	/** True only if all five sheets loaded without error. */
	private boolean hasImages;

	/** Current animation state. */
	private int state;
	/** Index of the current frame within the sprite sheet. */
	private int currentFrame;
	/** Counts up to FRAME_DELAY, then advances frame and resets. */
	private int frameTimer;
	/** True after a one-shot animation reaches its last frame. */
	private boolean looped;

	public PlayerAnimation(PApplet p)
	{
		this.p = p;
		this.state = IDLE;
		this.currentFrame = 0;
		this.frameTimer = 0;
		this.hasImages = false;
		this.looped = false;

		// Load each sheet independently; missing sheets → null → fallback rect
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

		// Derive per-frame dimensions from sheet width / frame count
		if (idleSheet != null && idleSheet.width > 0)
		{ idleFrameWidth = idleSheet.width / IDLE_FRAMES; idleFrameHeight = idleSheet.height; }
		if (moveSheet != null && moveSheet.width > 0)
		{ moveFrameWidth = moveSheet.width / MOVE_FRAMES; moveFrameHeight = moveSheet.height; }
		if (hitSheet != null && hitSheet.width > 0)
		{ hitFrameWidth = hitSheet.width / HIT_FRAMES; hitFrameHeight = hitSheet.height; }
		if (parrySheet != null && parrySheet.width > 0)
		{ parryFrameWidth = parrySheet.width / PARRY_FRAMES; parryFrameHeight = parrySheet.height; }
		if (deathSheet != null && deathSheet.width > 0)
		{ deathFrameWidth = deathSheet.width / DEATH_FRAMES; deathFrameHeight = deathSheet.height; }

		hasImages = idleSheet != null && moveSheet != null
			&& hitSheet != null && parrySheet != null && deathSheet != null;
	}

	/** Change animation state. Resets to frame 0 only if the state changes. */
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

	/** Force the current animation to restart from frame 0. */
	public void restart()
	{
		this.currentFrame = 0;
		this.frameTimer = 0;
		this.looped = false;
	}

	/** True when a one-shot animation has reached its final frame. */
	public boolean isFinished()
	{
		if (state == IDLE || state == MOVING) return false;  // these loop forever
		return looped;
	}

	/** Advance the frame counter; cycle or freeze depending on state type. */
	public void update()
	{
		frameTimer++;
		if (frameTimer >= FRAME_DELAY)
		{
			frameTimer = 0;                       // reset throttle
			int frames = frameCount();
			if (looped) return;                   // one-shot already finished → stay

			currentFrame++;
			if (currentFrame >= frames)            // reached end of sheet
			{
				if (state == IDLE || state == MOVING)
					currentFrame = 0;              // loop back to start
				else
				{ currentFrame = frames - 1; looped = true; }  // freeze on last frame
			}
		}
	}

	/** Number of frames in the current state's sprite sheet. */
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

	/**
	 * Clip and draw the current sprite frame.
	 * @param invincible  if true, flicker every 3 frames for visual feedback
	 */
	public void draw(float x, float y, float w, float h, boolean invincible)
	{
		// Flicker effect: skip drawing every other 3-frame block
		if (invincible && p.frameCount % 6 < 3) return;

		// Apply per-state position offset
		float ox = w * OFFSET_X[state];
		float oy = h * OFFSET_Y[state];

		if (hasImages && sheetForState() != null)
		{
			PImage sheet = sheetForState();
			int fw = frameWidthForState();
			int fh = frameHeightForState();
			int sx = currentFrame * fw;   // left edge of current frame in sheet
			p.image(sheet, x + ox, y + oy, w, h, sx, 0, sx + fw, fh);
		}
		else
		{
			// Fallback: coloured rectangle when images are missing
			p.noStroke();
			switch (state)
			{
				case DEATH:  p.fill(80, 80, 80);    break;   // grey
				case HIT:    p.fill(255, 80, 80);   break;   // red
				case PARRY:  p.fill(255, 255, 80);  break;   // yellow
				case MOVING: p.fill(255, 200, 0);   break;   // orange
				default:     p.fill(invincible ? 255 : 0, 255, 255); break;  // cyan / yellow
			}
			p.rect(x + ox, y + oy, w, h);
		}
	}

	/** Return the sprite sheet for the current state. */
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

	/** Pixel width of a single frame in the current state's sheet. */
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

	/** Pixel height of a single frame in the current state's sheet. */
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
