package battle;

public class Property
{
	final static int INITIAL_HP = 100;
	final static int INITIAL_SH = 0;
	final static int MAX_SH = 50;
	final static private int SH_REGEN_INTERVAL = 60;

	private int HP;
	private int SH;
	private int invincibleTimer;
	private int regenTimer;

	public Property()
	{
		this.HP = INITIAL_HP;
		this.SH = INITIAL_SH;
		this.invincibleTimer = 0;
		this.regenTimer = 0;
	}

	public void update()
	{
		if (invincibleTimer > 0) invincibleTimer--;
		regenShield();
	}

	private void regenShield()
	{
		regenTimer++;
		if (regenTimer >= SH_REGEN_INTERVAL)
		{
			regenTimer = 0;
			if (SH < MAX_SH) SH++;
		}
	}
	
	public void setInvicible(int frame)
	{
		invincibleTimer = frame;
	}

	public void calculateDamage(int damage)
	{
		if (invincibleTimer > 0) return;
		if (damage <= SH)
		{
			SH -= damage;
		}
		else
		{
			int remainingDamage = damage - SH;
			SH = 0;
			HP -= remainingDamage;
		}
	}

	public boolean isInvincible()
	{
		return invincibleTimer > 0;
	}

	public int getHP() { return HP; }
	public void setHP(int val) { HP = val; }
	public void addHP(int val) { HP += val; }

	public int getSH() { return SH; }
	public void setSH(int val) { SH = val; }
	public void addSH(int val) { SH += val; }
}
