package battle;

/**
 * Health and shield component.
 *
 * Damage is absorbed by shield first, then HP. A short invincibility window
 * prevents repeated hits from the same obstacle. Shield regenerates slowly
 * over time up to a cap.
 */
public class Property
{
	/** Player dies when HP reaches 0. */
	final static int INITIAL_HP = 60;
	/** Shield starts empty and must regenerate during play. */
	final static int INITIAL_SH = 0;
	/** Maximum shield points — prevents infinite stacking. */
	final static int MAX_SH = 20;
	/** Frames between each +1 shield regen tick (60 = 1 second at 60 fps). */
	final static private int SH_REGEN_INTERVAL = 60;

	private int HP;
	private int SH;
	/** Remaining frames of post-hit invulnerability. */
	private int invincibleTimer;
	/** Counts up to SH_REGEN_INTERVAL, then resets and adds 1 shield. */
	private int regenTimer;

	public Property()
	{
		this.HP = INITIAL_HP;
		this.SH = INITIAL_SH;
		this.invincibleTimer = 0;
		this.regenTimer = 0;
	}

	/** Called every frame: tick invincibility and regen shield. */
	public void update()
	{
		if (invincibleTimer > 0) invincibleTimer--;
		regenShield();
	}

	/** Gradually restore shield up to MAX_SH. */
	private void regenShield()
	{
		regenTimer++;
		if (regenTimer >= SH_REGEN_INTERVAL)
		{
			regenTimer = 0;          // reset counter
			if (SH < MAX_SH) SH++;   // only up to cap
		}
	}

	/** Overwrite invincibility frames (new hit replaces old). */
	public void setInvicible(int frame)
	{
		invincibleTimer = frame;
	}

	/**
	 * Apply incoming damage. Invincibility makes this a no-op.
	 * Shield absorbs first; excess passes through to HP.
	 */
	public void calculateDamage(int damage)
	{
		if (invincibleTimer > 0) return;   // still flashing, ignore

		if (damage <= SH)
		{
			SH -= damage;                  // shield fully absorbs
		}
		else
		{
			int remainingDamage = damage - SH;
			SH = 0;                        // shield depleted
			HP -= remainingDamage;         // remainder hits health
		}
	}

	public boolean isInvincible() { return invincibleTimer > 0; }

	public int getHP() { return HP; }
	public void setHP(int val) { HP = val; }
	public void addHP(int val) { HP += val; }

	public int getSH() { return SH; }
	public void setSH(int val) { SH = val; }
	public void addSH(int val) { SH += val; }
}
