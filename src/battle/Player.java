package battle;

import processing.core.PApplet;

/**
 * Player character positioned near the bottom of the screen.
 *
 * Occupies one of 5 lanes. Switches lanes with a wind-up + recovery
 * animation. Collision and parry logic is handled externally by Road.
 */
public class Player
{
	/** Frames of wind-up before the lane actually switches. */
	final static private int WIND_UP_FRAMES = 15;
	/** Frames of recovery after the lane switches (total move = WIND_UP + RECOVERY). */
	final static private int RECOVERY_FRAMES = 15;

	/** Display width as fraction of lane width. */
	final static private float WIDTH_RATIO = 0.75f;
	/** Display height as fraction of screen height. */
	final static private float HEIGHT_RATIO = 0.25f;
	/** Collision box width as fraction of display width. */
	final static private float COLLISION_WIDTH_RATIO = 0.7f;
	/** Collision box height as fraction of display height. */
	final static private float COLLISION_HEIGHT_RATIO = 0.5f;
	/** Distance from screen bottom as fraction of screen height. */
	final static private float BOTTOM_MARGIN_RATIO = 0.05f;

	private Property property;
	private PlayerAnimation animation;

	/** Current lane index (0 = leftmost). */
	private int lane;
	private float laneWidth;
	/** Top-left corner of the visible sprite bounding box. */
	private float displayX, displayY;
	private float displayW, displayH;
	/** Precomputed collision box dimensions. */
	private float colW, colH;

	/** Total movement timer: counts down from WIND_UP + RECOVERY to 0. */
	private int actionTimer;
	/** Target lane, applied when actionTimer hits RECOVERY. */
	private int pendingLane;

	/** Remaining frames for hit/parry animation playback. */
	private int hitTimer;
	private int parryTimer;
	/** True after HP reaches 0 — locks DEATH animation. */
	private boolean dead;

	public Player(PApplet p)
	{
		float sw = p.width;
		float sh = p.height;

		laneWidth = sw / GameConfig.LANE_COUNT;
		displayW = laneWidth * WIDTH_RATIO;
		displayH = sh * HEIGHT_RATIO;
		colW = displayW * COLLISION_WIDTH_RATIO;
		colH = displayH * COLLISION_HEIGHT_RATIO;

		lane = 2;   // start in centre lane
		// Centre the sprite horizontally in its lane
		displayX = lane * laneWidth + (laneWidth - displayW) / 2;
		// Position near the bottom of the screen
		displayY = sh - displayH - sh * BOTTOM_MARGIN_RATIO;
		pendingLane = lane;

		property = new Property();
		animation = new PlayerAnimation(p);

		hitTimer = 0;
		parryTimer = 0;
		dead = false;
	}

	/** Queue a lane switch. Ignored if mid-animation or dead. */
	public void setLane(int lane)
	{
		if (dead) return;
		if (lane >= 0 && lane < GameConfig.LANE_COUNT && actionTimer == 0)
		{
			pendingLane = lane;
			actionTimer = WIND_UP_FRAMES + RECOVERY_FRAMES;
		}
	}

	/** Advance all timers and update animation state. Called each frame. */
	public void update()
	{
		property.update();    // tick invincibility + shield regen

		// Decrement independent animation timers
		if (hitTimer > 0) hitTimer--;
		if (parryTimer > 0) parryTimer--;
		if (actionTimer > 0)
		{
			actionTimer--;
			// Switch lane exactly at the recovery point
			if (actionTimer == RECOVERY_FRAMES)
			{
				this.lane = pendingLane;
				displayX = lane * laneWidth + (laneWidth - displayW) / 2;
			}
		}

		// Determine visual state (higher priority first)
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

	/**
	 * Called when an obstacle collides with the player.
	 * @param damage            amount subtracted from HP/SH
	 * @param invincibleFrames  post-hit invulnerability duration
	 */
	public void takeDamage(int damage, int invincibleFrames)
	{
		if (dead) return;
		property.calculateDamage(damage);           // apply to HP/SH
		property.setInvicible(invincibleFrames);     // brief immunity
		hitTimer = PlayerAnimation.HIT_FRAMES * PlayerAnimation.FRAME_DELAY;
		animation.restart();                         // restart hit anim

		if (property.getHP() <= 0) dead = true;      // trigger death
	}

	/** Execute a parry — plays animation, no invincibility. */
	public void doParry()
	{
		if (dead) return;
		parryTimer = PlayerAnimation.PARRY_FRAMES * PlayerAnimation.FRAME_DELAY;
		animation.restart();
	}

	public boolean isDead() { return dead; }

	/** True when the death animation has finished playing. */
	public boolean isDeathAnimationFinished()
	{
		return dead && animation.isFinished();
	}

	/** Collision box left edge. */
	public float getX() { return displayX + (displayW - colW) / 2; }
	/** Collision box top edge. */
	public float getY() { return displayY + (displayH - colH) / 2; }
	public float getW() { return colW; }
	public float getH() { return colH; }

	public int getLane() { return lane; }
	public int getHP() { return property.getHP(); }
	public int getSH() { return property.getSH(); }

	public void setInvicible(int frames) { property.setInvicible(frames); }

	/** Draw the player sprite (or fallback rectangle). */
	public void draw()
	{
		animation.draw(displayX, displayY, displayW, displayH, property.isInvincible());
	}

	/** Debug overlay: green collision box + HP/SH text. */
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
