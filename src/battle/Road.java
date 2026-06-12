package battle;

import processing.core.PApplet;
import java.util.ArrayDeque;
import java.util.Iterator;

public class Road
{
	final static private float INITIAL_DIFFICULTY = 1.0f;
	final static private float DIFFICULTY_RATE = 0.002f;
	final static private float BASE_SPAWN_INTERVAL = 60;
	final static private float MIN_SPAWN_INTERVAL = 15;
	final static private float BASE_OBSTACLE_HEIGHT = 30;
	final static private float HEIGHT_SCALE = 15;
	final static private float BASE_SPEED = 5;
	final static private float SPEED_SCALE = 0.5f;

	private PApplet p;
	private ArrayDeque<Obstacle>[] lanes;
	private float laneWidth;

	private float spawnTimer;
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

	public void update()
	{
		difficulty += DIFFICULTY_RATE;

		float spawnInterval = BASE_SPAWN_INTERVAL / difficulty;
		if (spawnInterval < MIN_SPAWN_INTERVAL) spawnInterval = MIN_SPAWN_INTERVAL;

		spawnTimer++;
		if (spawnTimer >= spawnInterval)
		{
			spawnTimer = 0;
			addObstacle();
		}

		for (int i = 0; i < GameConfig.LANE_COUNT; i++)
		{
			Iterator<Obstacle> it = lanes[i].iterator();
			while (it.hasNext())
			{
				Obstacle o = it.next();
				o.move();
				if (o.isOffScreen(p.height) || o.isParried())
					it.remove();
			}
		}
	}

	private void addObstacle()
	{
		int lane = (int) (Math.random() * GameConfig.LANE_COUNT);
		float x = lane * laneWidth;
		float w = laneWidth;
		float h = BASE_OBSTACLE_HEIGHT + difficulty * HEIGHT_SCALE;
		float speed = BASE_SPEED + difficulty * SPEED_SCALE;
		lanes[lane].add(new Obstacle(p, w, h, x, speed));
	}

	public int checkCollision(Player player)
	{
		int lane = player.getLane();
		Obstacle o = lanes[lane].peekFirst();
		if (o == null) return 0;
		if (rectOverlap(player.getX(), player.getY(), player.getW(), player.getH(), o.getX(), o.getY(), o.getW(), o.getH()))
			return o.framesToPass(player.getH());
		return 0;
	}

	public int checkParry(Player player)
	{
		int lane = player.getLane();
		Obstacle o = lanes[lane].peekFirst();
		if (o == null || o.isParried()) return 0;
		if (rectOverlap(player.getX(), player.getY(), player.getW(), player.getH(), o.getParryX(), o.getParryY(), o.getParryW(), o.getParryH()))
		{
			o.parry();
			return o.framesToPass(player.getH());
		}
		return 0;
	}

	private boolean rectOverlap(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2)
	{
		return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
	}

	public void draw()
	{
		for (int i = 0; i < GameConfig.LANE_COUNT; i++)
			for (Obstacle o : lanes[i])
				o.draw();
	}

	public void drawDebug()
	{
		for (int i = 0; i < GameConfig.LANE_COUNT; i++)
			for (Obstacle o : lanes[i])
				o.drawDebug();
	}
}
