package battle;

/**
 *
 * @author ohjun
 */
public class Property
{
	private int HP;
	private int SH; //护盾量
	
	public Property(int HP, int SH)
	{
		this.HP = HP;
		this.SH = SH;
	}
	
	public void calculateDamage(int damage)
	{
		if(damage <= SH) SH -= damage;
		else
		{
			SH = 0;
			HP -= (damage - SH);
		}
	}
	
	public void setHP(int val)
	{
		HP = val;
	}
	
	public void addHP(int val)
	{
		HP += val;
	}
	
	public int getHP()
	{
		return HP;
	}
	
	public void setSH(int val)
	{
		SH = val;
	}
	
	public void addSH(int val)
	{
		SH += val;
	}
	
	public int getSH()
	{
		return SH;
	}
}
