package battle;

import processing.core.PApplet;

/**
 *
 * @author ohjun
 */
public class Character
{
	private PApplet p;
	private Property property;
		
	public Character(PApplet p, int HP, int SP)
	{
        this.p = p;
		property = new Property(HP, SP);
    }
	
	public void calculateDamage(int damage)
	{
		property.calculateDamage(damage);
	}
	
	public boolean isDead()
	{return property.getHP() <= 0;}
}
