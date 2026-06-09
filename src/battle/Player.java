package battle;

import processing.core.PApplet;

public class Player
{
	final static private int WIND_UP_FRAMES = 8;
	final static private int RECOVERY_FRAMES = 6;

	final static private float WIDTH_RATIO = 0.50f;		// 显示宽度占轨道宽度比例
	final static private float HEIGHT_RATIO = 0.2f;			// 显示高度占屏幕高度比例
	final static private float BOTTOM_MARGIN_RATIO = 0.05f;		// 底部边距占屏幕高度比例
	final static private float COLLISION_WIDTH_RATIO = 0.5f;	// 碰撞箱宽度占显示宽度比例
	final static private float COLLISION_HEIGHT_RATIO = 0.8f;	// 碰撞箱高度占显示高度比例

	final static private int INITIAL_HP = 100;
	final static private int INITIAL_SP = 0;

	private Property property;
	private PlayerAnimation animation;

	private int lane;
	private float laneWidth;
	private float displayX, displayY;
	private float displayW, displayH;

	private int actionTimer;
	private int pendingLane;

	public Player(PApplet p)
	{
		float sw = p.width;
		float sh = p.height;

		laneWidth = sw / Road.LANE_COUNT;
		displayW = laneWidth * WIDTH_RATIO;
		displayH = sh * HEIGHT_RATIO;

		lane = 2;
		displayX = lane * laneWidth + (laneWidth - displayW) / 2;
		displayY = sh - displayH - sh * BOTTOM_MARGIN_RATIO;
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
				displayX = lane * laneWidth + (laneWidth - displayW) / 2;
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

	public float getX() { return displayX + (displayW - displayW * COLLISION_WIDTH_RATIO) / 2; }
	public float getY() { return displayY + (displayH - displayH * COLLISION_HEIGHT_RATIO) / 2; }
	public float getW() { return displayW * COLLISION_WIDTH_RATIO; }
	public float getH() { return displayH * COLLISION_HEIGHT_RATIO; }
	public int getHP() { return property.getHP(); }
	public int getSH() { return property.getSH(); }

	public void draw()
	{
		animation.draw(displayX, displayY, displayW, displayH, property.isInvincible());
	}
}
