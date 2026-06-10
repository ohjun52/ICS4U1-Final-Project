package battle;

import processing.core.PApplet;
import processing.core.PImage;

public class PlayerAnimation
{
	final static public int IDLE = 0;
	final static public int MOVING = 1;

	final static private String IDLE_PATH = "animations/run.png";
	final static private int IDLE_FRAMES = 12;
	final static private String MOVE_PATH = "animations/move.png";
	final static private int MOVE_FRAMES = 61;

	final static private int FRAME_DELAY = 4;

	private PApplet p;
	private PImage idleSheet;
	private PImage moveSheet;
	private int idleFrameWidth;
	private int idleFrameHeight;
	private int moveFrameWidth;
	private int moveFrameHeight;
	private boolean hasImages;

	private int state;
	private int currentFrame;
	private int frameTimer;

	public PlayerAnimation(PApplet p)
	{
		this.p = p;
		this.state = IDLE;
		this.currentFrame = 0;
		this.frameTimer = 0;
		this.hasImages = false;

		try
		{
			idleSheet = p.loadImage(IDLE_PATH);
			moveSheet = p.loadImage(MOVE_PATH);
			if (idleSheet != null && moveSheet != null && idleSheet.width > 0 && moveSheet.width > 0)
			{
				hasImages = true;
				idleFrameWidth = idleSheet.width / IDLE_FRAMES;
				idleFrameHeight = idleSheet.height;
				moveFrameWidth = moveSheet.width / MOVE_FRAMES;
				moveFrameHeight = moveSheet.height;
			}
		}
		catch (Exception e)
		{
			hasImages = false;
		}
	}

	public void setState(int state)
	{
		if (state == IDLE || state == MOVING)
		{
			if (this.state != state)
			{
				this.state = state;
				this.currentFrame = 0;
				this.frameTimer = 0;
			}
		}
	}

	public void update()
	{
		frameTimer++;
		if (frameTimer >= FRAME_DELAY)
		{
			frameTimer = 0;
			int frames = (state == MOVING) ? MOVE_FRAMES : IDLE_FRAMES;
			currentFrame = (currentFrame + 1) % frames;
		}
	}

	public void draw(float x, float y, float w, float h, boolean invincible)
	{
		if (invincible && p.frameCount % 6 < 3) return;

		if (hasImages)
		{
			if (state == MOVING)
			{
				int sx = currentFrame * moveFrameWidth;
				p.image(moveSheet, x, y, w, h, sx, 0, sx + moveFrameWidth, moveFrameHeight);
			}
			else
			{
				int sx = currentFrame * idleFrameWidth;
				p.image(idleSheet, x, y, w, h, sx, 0, sx + idleFrameWidth, idleFrameHeight);
			}
		}
		else
		{
			if (invincible)
			{
				p.fill(255, 255, 80);
			}
			else if (state == MOVING)
			{
				p.fill(255, 200, 0);
			}
			else
			{
				p.fill(0, 255, 255);
			}
			p.noStroke();
			p.rect(x, y, w, h);
		}
	}
}
