package battle;

import processing.core.PApplet;

public class Player
{
	final static private int WIND_UP_FRAMES = 15;
	final static private int RECOVERY_FRAMES = 15;

	final static private float WIDTH_RATIO = 0.75f;				// 显示宽度占轨道宽度比例
	final static private float HEIGHT_RATIO = 0.25f;			// 显示高度占屏幕高度比例

	final static private float COLLISION_WIDTH_RATIO = 0.7f;	// 碰撞箱宽度占显示宽度比例
	final static private float COLLISION_HEIGHT_RATIO = 0.5f;	// 碰撞箱高度占显示高度比例

	final static private float BOTTOM_MARGIN_RATIO = 0.05f;		// 底部边距占屏幕高度比例

	private Property property;
	private PlayerAnimation animation;

	private int lane;
	private float laneWidth;
	private float displayX, displayY;
	private float displayW, displayH;
	private float colW, colH;		// 碰撞箱预计算

	private int actionTimer;
	private int pendingLane;

	private int hitTimer;			// 受击动画剩余帧
	private int parryTimer;			// 跨跃动画剩余帧
	private boolean dead;			// 死亡标记，锁定 DEATH 状态

	public Player(PApplet p)
	{
		float sw = p.width;
		float sh = p.height;

		laneWidth = sw / GameConfig.LANE_COUNT;
		displayW = laneWidth * WIDTH_RATIO;
		displayH = sh * HEIGHT_RATIO;
		colW = displayW * COLLISION_WIDTH_RATIO;
		colH = displayH * COLLISION_HEIGHT_RATIO;

		lane = 2;
		displayX = lane * laneWidth + (laneWidth - displayW) / 2;
		displayY = sh - displayH - sh * BOTTOM_MARGIN_RATIO;
		pendingLane = lane;

		property = new Property();
		animation = new PlayerAnimation(p);

		hitTimer = 0;
		parryTimer = 0;
		dead = false;
	}

	public void setLane(int lane)
	{
		if (dead) return;
		if (lane >= 0 && lane < GameConfig.LANE_COUNT && actionTimer == 0)
		{
			pendingLane = lane;
			actionTimer = WIND_UP_FRAMES + RECOVERY_FRAMES;
		}
	}

	public void update()
	{
		property.update();

		// 所有计时器独立递减，不受视觉状态影响
		if (hitTimer > 0) hitTimer--;
		if (parryTimer > 0) parryTimer--;
		if (actionTimer > 0)
		{
			actionTimer--;
			if (actionTimer == RECOVERY_FRAMES)
			{
				this.lane = pendingLane;
				displayX = lane * laneWidth + (laneWidth - displayW) / 2;
			}
		}

		// 视觉状态优先级：死亡 > 移动 > 跨跃 > 受击 > 待机
		if (dead)
			animation.setState(PlayerAnimation.DEATH);
		else if (actionTimer > 0)
			animation.setState(PlayerAnimation.MOVING);
		else if (parryTimer > 0)
			animation.setState(PlayerAnimation.PARRY);
		else if (hitTimer > 0)
			animation.setState(PlayerAnimation.HIT);
		else
			animation.setState(PlayerAnimation.IDLE);

		animation.update();
	}

	public void takeDamage(int damage, int invincibleFrames)
	{
		if (dead) return;
		property.calculateDamage(damage);
		property.setInvicible(invincibleFrames);
		hitTimer = PlayerAnimation.HIT_FRAMES * PlayerAnimation.FRAME_DELAY;
		animation.restart();

		if (property.getHP() <= 0) dead = true;
	}

	public void doParry()
	{
		if (dead) return;
		parryTimer = PlayerAnimation.PARRY_FRAMES * PlayerAnimation.FRAME_DELAY;
		animation.restart();
	}

	public boolean isDead()
	{
		return dead;
	}

	public boolean isDeathAnimationFinished()
	{
		return dead && animation.isFinished();
	}

	public float getX() { return displayX + (displayW - colW) / 2; }
	public float getY() { return displayY + (displayH - colH) / 2; }
	public float getW() { return colW; }
	public float getH() { return colH; }

	public int getLane() { return lane; }
	public int getHP() { return property.getHP(); }
	public int getSH() { return property.getSH(); }

	public void setInvicible(int frames)
	{
		property.setInvicible(frames);
	}

	public void draw()
	{
		animation.draw(displayX, displayY, displayW, displayH, property.isInvincible());
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
}
