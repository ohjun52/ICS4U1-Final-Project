package battle;

import processing.core.PApplet;

public class Player
{
	final static private int WIND_UP_FRAMES = 8;
	final static private int RECOVERY_FRAMES = 6;

	final static private float HEIGHT_RATIO = 0.075f;
	final static private float BOTTOM_MARGIN_RATIO = 0.05f;

	final static private int INITIAL_HP = 100;
	final static private int INITIAL_SP = 0;

	private Property property;
	private PlayerAnimation animation;

	private int lane;
	private float laneWidth;
	private float x, y;
	private float w, h;

	private int actionTimer;
	private int pendingLane;

	public Player(PApplet p)
	{
		float sw = p.width;
		float sh = p.height;

		laneWidth = sw / Road.LANE_COUNT;
		w = laneWidth;
		h = sh * HEIGHT_RATIO;

		lane = 2;
		x = lane * laneWidth;
		y = sh - h - sh * BOTTOM_MARGIN_RATIO;
		pendingLane = lane;

		property = new Property(INITIAL_HP, INITIAL_SP);
		animation = new PlayerAnimation(p);
	}

	public void setLane(int lane)
	{
		if (lane >= 0 && lane < Road.LANE_COUNT && actionTimer == 0)
		{
			pendingLane = lane;
			actionTimer = WIND_UP_FRAMES + RECOVERY_FRAMES;
		}
	}

	public void update()
	{
		property.update();

		if (actionTimer > 0)
		{
			animation.setState(PlayerAnimation.MOVING);
			actionTimer--;
			if (actionTimer == RECOVERY_FRAMES)
			{
				this.lane = pendingLane;
				x = lane * laneWidth;
			}
		}
		else
		{
			animation.setState(PlayerAnimation.IDLE);
		}

		animation.update();
	}

	public void takeDamage(int damage)
	{
		property.calculateDamage(damage);
	}

	public boolean isDead()
	{
		return property.getHP() <= 0;
	}

	public float getX() { return x; }
	public float getY() { return y; }
	public float getW() { return w; }
	public float getH() { return h; }
	public int getHP() { return property.getHP(); }
	public int getSH() { return property.getSH(); }

	public void draw()
	{
		animation.draw(x, y, w, h, property.isInvincible());
	}
}
