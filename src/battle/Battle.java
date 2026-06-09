package battle;

import processing.core.PApplet;

public class Battle
{
	private PApplet p;
	private Road road;
	private Player player;

	public Battle(PApplet p)
	{
		this.p = p;
		this.road = new Road(p);
		this.player = new Player(p);
	}

	public void update()
	{
		road.update();
		player.update();
		if (road.checkCollision(player.getX(), player.getY(), player.getW(), player.getH()))
		{
			player.takeDamage(10);
		}
	}

	public void draw()
	{
		p.background(0);
		road.draw();
		player.draw();
	}

	public void setPlayerLane(int lane)
	{
		player.setLane(lane);
	}
}
