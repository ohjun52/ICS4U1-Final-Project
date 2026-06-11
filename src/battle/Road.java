package battle;

import processing.core.PApplet;
import java.util.ArrayDeque;
import java.util.Iterator;

public class Road
{
	final static public int LANE_COUNT = 5;						// 轨道数量
	final static private float INITIAL_DIFFICULTY = 1.0f;		// 初始难度系数
	final static private float DIFFICULTY_RATE = 0.002f;		// 难度增长速度（每帧）
	final static private float BASE_SPAWN_INTERVAL = 60;		// 基础生成间隔（帧）
	final static private float MIN_SPAWN_INTERVAL = 15;			// 最小生成间隔（帧）
	final static private float BASE_OBSTACLE_HEIGHT = 30;		// 基础障碍物高度
	final static private float HEIGHT_SCALE = 15;				// 障碍物高度随难度增长系数
	final static private float BASE_SPEED = 5;					// 基础下落速度
	final static private float SPEED_SCALE = 0.5f;				// 速度随难度增长系数

	private PApplet p;
	private ArrayDeque<Obstacle>[] lanes;
	private float laneWidth;

	private float spawnTimer;
	private float difficulty;
	
	public Road(PApplet p)
	{
		this.p = p;
		this.laneWidth = p.width / LANE_COUNT;
		this.lanes = new ArrayDeque[LANE_COUNT];
		for (int i = 0; i < LANE_COUNT; i++)
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

		for (int i = 0; i < LANE_COUNT; i++)
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
		int lane = (int) (Math.random() * LANE_COUNT);
		float x = lane * laneWidth;
		float w = laneWidth;
		float h = BASE_OBSTACLE_HEIGHT + difficulty * HEIGHT_SCALE;
		float speed = BASE_SPEED + difficulty * SPEED_SCALE;
		lanes[lane].add(new Obstacle(p, w, h, x, speed));
	}

	public void draw()
	{
		for (int i = 0; i < LANE_COUNT; i++)
			for (Obstacle o : lanes[i])
				o.draw();
	}

	// 调试：所有障碍物碰撞箱+跨跃区
	public void drawDebug()
	{
		for (int i = 0; i < LANE_COUNT; i++)
			for (Obstacle o : lanes[i])
				o.drawDebug();
	}

	public boolean checkCollision(int lane, float px, float py, float pw, float ph)
	{
		Obstacle o = lanes[lane].peekFirst();
		if (o == null) return false;
		return rectOverlap(px, py, pw, ph, o.getX(), o.getY(), o.getW(), o.getH());
	}

	public boolean checkParry(int lane, float px, float py, float pw, float ph)
	{
		Obstacle o = lanes[lane].peekFirst();
		if (o == null || o.isParried()) return false;
		if (rectOverlap(px, py, pw, ph, o.getParryX(), o.getParryY(), o.getParryW(), o.getParryH()))
		{
			o.parry();
			return true;
		}
		return false;
	}

	private boolean rectOverlap(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2)
	{
		return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
	}
}
