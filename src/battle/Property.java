package battle;

public class Property
{
	private int HP;
	private int SH;
	private int invincibleTimer;

	public Property(int HP, int SH)
	{
		this.HP = HP;
		this.SH = SH;
		this.invincibleTimer = 0;
	}

	public void update()
	{
		if (invincibleTimer > 0) invincibleTimer--;
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
