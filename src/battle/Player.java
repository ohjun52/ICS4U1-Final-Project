package battle;

import processing.core.PApplet;

public class Player
{
	final static private int WIND_UP_FRAMES = 10;
	final static private int RECOVERY_FRAMES = 10;

	final static private float WIDTH_RATIO = 0.75f;				// 显示宽度占轨道宽度比例
	final static private float HEIGHT_RATIO = 0.25f;			// 显示高度占屏幕高度比例
	
	final static private float COLLISION_WIDTH_RATIO = 0.7f;	// 碰撞箱宽度占显示宽度比例
	final static private float COLLISION_HEIGHT_RATIO = 0.4f;	// 碰撞箱高度占显示高度比例
	
	final static private float BOTTOM_MARGIN_RATIO = 0.05f;		// 底部边距占屏幕高度比例
		

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

		laneWidth = sw / GameConfig.LANE_COUNT;
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
		if (lane >= 0 && lane < GameConfig.LANE_COUNT && actionTimer == 0)
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

	public void takeDamage(int damage, int invincibleFrames)
	{
		property.calculateDamage(damage);
		property.setInvicible(invincibleFrames);
	}

	public boolean isDead()
	{
		return property.getHP() <= 0;
	}

	public float getX() { return displayX + (displayW - displayW * COLLISION_WIDTH_RATIO) / 2; }
	public float getY() { return displayY + (displayH - displayH * COLLISION_HEIGHT_RATIO) / 2; }
	public float getW() { return displayW * COLLISION_WIDTH_RATIO; }
	public float getH() { return displayH * COLLISION_HEIGHT_RATIO; }
	
	public int getLane() { return lane; }
	public int getHP() { return property.getHP(); }
	public int getSH() { return property.getSH(); }
	
	public void setInvicible(int frames)
	{
		property.setInvicible(frames);
	}

	// 调试：碰撞箱+血量护甲
	public void drawDebug(PApplet p)
	{
		p.noFill();
		p.stroke(0, 255, 0);
		p.rect(getX(), getY(), getW(), getH());
		p.fill(255);
		p.textSize(24);
		p.text("HP:" + property.getHP() + " SH:" + property.getSH(), 100, 100);
	}

	public void draw()
	{
		animation.draw(displayX, displayY, displayW, displayH, property.isInvincible());
	}
}
