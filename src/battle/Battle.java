package battle;

import processing.core.PApplet;
import processing.core.PImage;

public class Battle
{
	final static private String BACKGROUND_PATH = "background/battle_background.png";

	private PApplet p;
	private PImage background;
	
	private Road road;
	private Player player;

	public Battle(PApplet p)
	{
		this.p = p;
		try { this.background = p.loadImage(BACKGROUND_PATH); }
		catch (Exception e) { System.err.println("Failed to load: " + BACKGROUND_PATH); this.background = null; }
		this.road = new Road(p);
		this.player = new Player(p);
	}

	public void update()
	{
		road.update();
		player.update();
		int collisionFrames = road.checkCollision(player);
		if(collisionFrames > 0)
		{
			player.takeDamage(10, collisionFrames);
		}
	}

	public void draw()
	{
		if(background != null) p.image(background, 0, 0, p.width, p.height);
		else p.background(0);

		road.draw();
		player.draw();

		// 调试渲染
		road.drawDebug();
		player.drawDebug(p);
	}

	public void playerParry()
	{
		int frames = road.checkParry(player);
		if (frames > 0)
		{
			player.setInvicible(frames);
		}
	}

	public void setPlayerLane(int lane)
	{
		player.setLane(lane);
	}
}
