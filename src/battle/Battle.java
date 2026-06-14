package battle;

import game_main.GameMain;
import processing.core.PApplet;
import processing.core.PImage;

public class Battle
{
	final static private String BACKGROUND_PATH = "background/battle_background.png";

	private PApplet p;
	private PImage background;

	private Road road;
	private Player player;
	private HUD hud;
	private Score score;
	private SoundManager sound;

	private GameConfig.GameMode mode;
	private boolean gameOver;
	private boolean win;

	public Battle(PApplet p, GameConfig.GameMode mode)
	{
		this.p = p;
		this.mode = mode;
		try { this.background = p.loadImage(BACKGROUND_PATH); }
		catch (Exception e) { System.err.println("Failed to load: " + BACKGROUND_PATH); this.background = null; }
		this.road = new Road(p);
		this.player = new Player(p);
		this.score = new Score();
		this.hud = new HUD(player, score, Property.INITIAL_HP, Property.MAX_SH);
		this.sound = new SoundManager();
		this.gameOver = false;
		this.win = false;
		sound.playBGM();
	}

	public void update()
	{
		if (gameOver)
		{
			player.update();
			if (player.isDeathAnimationFinished())
			{
					GameMain.currentState = GameMain.Gamestate.TITLE;
			}
			return;
		}

		road.update();
		player.update();
		score.update();

		int collisionFrames = road.checkCollision(player);
		if (collisionFrames > 0)
		{
			player.takeDamage(10, collisionFrames);
		}

		if (player.isDead())
		{
			gameOver = true;
			win = false;
			sound.stopBGM();
		}
		else if (mode == GameConfig.GameMode.STORY && score.getDistance() >= GameConfig.STORY_TARGET)
		{
			gameOver = true;
			win = true;
			sound.stopBGM();
			GameMain.currentState = GameMain.Gamestate.END;
		}
	}

	public boolean isGameOver() { return gameOver; }
	public boolean isWin() { return win; }

	public void draw()
	{
		if(background != null) p.image(background, 0, 0, p.width, p.height);
		else p.background(0);

		road.draw();
		player.draw();
		hud.draw(p);

		// 调试渲染
//		road.drawDebug();
//		player.drawDebug(p);
	}

	public void playerParry()
	{
		if (road.checkParry(player))
		{
			player.doParry();
			sound.playParry();
		}
	}

	public void setPlayerLane(int lane)
	{
		player.setLane(lane);
	}
}
