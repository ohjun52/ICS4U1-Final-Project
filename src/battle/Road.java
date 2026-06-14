package battle;

import processing.core.PApplet;
import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Manages five lanes of falling obstacles.
 * Handles spawning with increasing difficulty, movement, off-screen removal,
 * and collision/parry detection against the player.
 */
public class Road
{
	// ---- Difficulty curve ----
	/** Starting difficulty (1.0 = base values). */
	final static private float INITIAL_DIFFICULTY = 1.0f;
	/** Difficulty increase per frame. */
	final static private float DIFFICULTY_RATE = 0.002f;
	/** Maximum difficulty multiplier. */
	final static private float MAX_DIFFICULTY = 4.0f;

	// ---- Spawn timing (frames) ----
	/** Base frames between spawns. At difficulty 1.0: 90 frames = 1.5 sec. */
	final static private float BASE_SPAWN_INTERVAL = 90;
	/** Fastest allowed spawn rate. At difficulty 4.0: 90/4 = 22.5 → clamped to 25. */
	final static private float MIN_SPAWN_INTERVAL = 25;

	// ---- Obstacle size ----
	/** Base obstacle height in pixels. */
	final static private float BASE_OBSTACLE_HEIGHT = 30;
	/** Additional height per difficulty point. At max: 30 + 4*8 = 62. */
	final static private float HEIGHT_SCALE = 8;
	/** Upper bound on obstacle height to keep parry zones reachable. */
	final static private float MAX_OBSTACLE_HEIGHT = 65;

	// ---- Fall speed (pixels per frame) ----
	/** Base speed. At difficulty 1.0: 3 + 1*1.2 = 4.2 px/frame. */
	final static private float BASE_SPEED = 3;
	/** Speed increase per difficulty point. At max: 3 + 4*1.2 = 7.8 px/frame. */
	final static private float SPEED_SCALE = 1.2f;
	/** Speed cap to keep the game playable. */
	final static private float MAX_SPEED = 8;

	private PApplet p;
	/** One deque per lane, storing obstacles top-to-bottom. */
	private ArrayDeque<Obstacle>[] lanes;
	/** Pixel width of a single lane. */
	private float laneWidth;

	/** Accumulator for spawn timing. */
	private float spawnTimer;
	/** Current difficulty, grows from INITIAL to MAX. */
	private float difficulty;

	@SuppressWarnings("unchecked")
	public Road(PApplet p)
	{
		this.p = p;
		this.laneWidth = p.width / GameConfig.LANE_COUNT;
		this.lanes = new ArrayDeque[GameConfig.LANE_COUNT];
		for (int i = 0; i < GameConfig.LANE_COUNT; i++)
			lanes[i] = new ArrayDeque<>();
		this.spawnTimer = 0;
		this.difficulty = INITIAL_DIFFICULTY;
	}

	/** Advance difficulty, spawn, move obstacles, remove off-screen/parried. */
	public void update()
	{
		// Ramp difficulty toward cap
		difficulty += DIFFICULTY_RATE;
		if (difficulty > MAX_DIFFICULTY) difficulty = MAX_DIFFICULTY;

		// Spawn interval shortens as difficulty rises
		float spawnInterval = BASE_SPAWN_INTERVAL / difficulty;
		if (spawnInterval < MIN_SPAWN_INTERVAL) spawnInterval = MIN_SPAWN_INTERVAL;

		spawnTimer++;
		if (spawnTimer >= spawnInterval)
		{
			spawnTimer = 0;
			addObstacle();
		}

		// Move every obstacle; remove those past the bottom or already parried
		for (int i = 0; i < GameConfig.LANE_COUNT; i++)
		{
			Iterator<Obstacle> it = lanes[i].iterator();
			while (it.hasNext())
			{
				Obstacle o = it.next();
				o.move();
				if (o.isOffScreen(p.height) || o.isParried())
					it.remove();   // safe removal during iteration
			}
		}
	}

	/** Try to place a new obstacle in a random unobstructed lane. */
	private void addObstacle()
	{
		// Compute size and speed from current difficulty, clamping to caps
		float h = BASE_OBSTACLE_HEIGHT + difficulty * HEIGHT_SCALE;
		if (h > MAX_OBSTACLE_HEIGHT) h = MAX_OBSTACLE_HEIGHT;
		float speed = BASE_SPEED + difficulty * SPEED_SCALE;
		if (speed > MAX_SPEED) speed = MAX_SPEED;
		float w = laneWidth;

		// Try up to LANE_COUNT*2 random lanes to avoid overlap
		int tries = GameConfig.LANE_COUNT * 2;
		while (tries-- > 0)
		{
			int lane = (int) (Math.random() * GameConfig.LANE_COUNT);
			float x = lane * laneWidth;
			if (canSpawn(lane, h))
			{
				lanes[lane].add(new Obstacle(p, w, h, x, speed));
				return;
			}
		}
		// All lanes blocked — skip this spawn cycle
	}

	/**
	 * Prevent overlapping spawns. Only checks the most recent obstacle
	 * in the lane, because earlier ones have already fallen past the
	 * spawn zone.
	 */
	private boolean canSpawn(int lane, float newH)
	{
		Obstacle last = lanes[lane].peekLast();
		if (last == null || last.isParried()) return true;
		// Block if the last obstacle is still at the top and would overlap
		return !(last.getDisplayY() < 0 && last.getDisplayY() + last.getDisplayH() > -newH);
	}

	/**
	 * Check AABB overlap between player and all obstacles in their lane.
	 * @return invincibility frames for the first collision (0 if none);
	 *         the hit obstacle is removed.
	 */
	public int checkCollision(Player player)
	{
		int lane = player.getLane();
		float px = player.getX(), py = player.getY(), pw = player.getW(), ph = player.getH();
		for (Obstacle o : lanes[lane])
		{
			if (o.isParried()) continue;   // skip already removed
			if (rectOverlap(px, py, pw, ph, o.getX(), o.getY(), o.getW(), o.getH()))
			{
				o.parry();                     // remove the obstacle
				return o.framesToPass(ph);     // grant invincibility frames
			}
		}
		return 0;
	}

	/**
	 * Check all obstacles in the player's lane for parry-zone overlap.
	 * Every matching obstacle is removed (bulk parry).
	 * @return true if at least one obstacle was parried.
	 */
	public boolean checkParry(Player player)
	{
		int lane = player.getLane();
		float py = player.getY();   // top of player collision box
		float px = player.getX();
		float pw = player.getW();
		boolean parried = false;
		for (Obstacle o : lanes[lane])
		{
			if (o.isParried()) continue;
			// Player top edge inside parry zone vertically, overlapping horizontally
			if (py >= o.getParryY() && py <= o.getParryY() + o.getParryH()
				&& px < o.getParryX() + o.getParryW() && px + pw > o.getParryX())
			{
				o.parry();
				parried = true;
			}
		}
		return parried;
	}

	/** Standard AABB overlap test. */
	private boolean rectOverlap(float x1, float y1, float w1, float h1,
	                            float x2, float y2, float w2, float h2)
	{
		return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
	}

	/** Draw all active obstacles. */
	public void draw()
	{
		for (int i = 0; i < GameConfig.LANE_COUNT; i++)
			for (Obstacle o : lanes[i])
				o.draw();
	}

	/** Draw debug overlays for all active obstacles. */
	public void drawDebug()
	{
		for (int i = 0; i < GameConfig.LANE_COUNT; i++)
			for (Obstacle o : lanes[i])
				o.drawDebug();
	}
}
