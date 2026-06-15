package battle;

import game_main.GameMain;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Core game coordinator. Owns all subsystems and drives per-frame
 * update logic and rendering.
 */
public class Battle
{
	final static private String BACKGROUND_PATH = "background/battle_background.png";

	/** Amount of damage per collision. */
	final static private int COLLISION_DAMAGE = 10;

	private PApplet p;
	private PImage background;

	private Road road;
	private Player player;
	private HUD hud;
	private Score score;
	private SoundManager sound;

	private GameConfig.GameMode mode;
	/** True after death or story completion — blocks further gameplay. */
	private boolean gameOver;
	/** True if story target was reached (not death). */
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
		sound.playBGM();   // start looping music
	}

	/** Called every frame by GameMain. Handles game-over delay. */
	public void update()
	{
		// Post-death: keep updating animation, then switch state
		if (gameOver)
		{
			player.update();   // let death animation play out
			if (player.isDeathAnimationFinished())
			{
				GameMain.currentState = GameMain.Gamestate.TITLE;
			}
			return;   // no gameplay updates after game over
		}

		road.update();
		player.update();
		score.update();

		// Collision check: hits deal damage, grant immunity, destroy obstacle
		int collisionFrames = road.checkCollision(player);
		if (collisionFrames > 0)
		{
			player.takeDamage(COLLISION_DAMAGE, collisionFrames);
		}

		// Death → wait for animation before transitioning
		if (player.isDead())
		{
			gameOver = true;
			win = false;
			sound.stopBGM();
		}
		// Story mode win → transition immediately (no death animation)
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
	public int getScore() { return score.getDistance(); }

	/** Render the battle scene: background, obstacles, player, HUD. */
	public void draw()
	{
		if (background != null)
			p.image(background, 0, 0, p.width, p.height);
		else
			p.background(0);

		road.draw();
		player.draw();
		hud.draw(p);
	}

	/** SPACE key handler: attempt to parry all matching obstacles. */
	public void playerParry()
	{
		if (road.checkParry(player))
		{
			player.doParry();
			sound.playParry();
		}
	}

	/** 1-5 key handler: queue a lane switch. */
	public void setPlayerLane(int lane)
	{
		player.setLane(lane);
	}
}
