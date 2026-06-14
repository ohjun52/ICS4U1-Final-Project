package battle;

import processing.core.PApplet;
import java.util.ArrayDeque;
import java.util.Iterator;

public class Road
{
	final static private float INITIAL_DIFFICULTY = 1.0f;
	final static private float DIFFICULTY_RATE = 0.002f;		// ~2.8分钟到最高难度
	final static private float MAX_DIFFICULTY = 4.0f;
	final static private float BASE_SPAWN_INTERVAL = 90;		// 起始 1.5s 间隔
	final static private float MIN_SPAWN_INTERVAL = 25;			// 最快 0.42s 间隔
	final static private float BASE_OBSTACLE_HEIGHT = 30;
	final static private float HEIGHT_SCALE = 8;
	final static private float MAX_OBSTACLE_HEIGHT = 65;
	final static private float BASE_SPEED = 3;
	final static private float SPEED_SCALE = 1.2f;
	final static private float MAX_SPEED = 8;

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
		if (difficulty > MAX_DIFFICULTY) difficulty = MAX_DIFFICULTY;

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
		float h = BASE_OBSTACLE_HEIGHT + difficulty * HEIGHT_SCALE;
		if (h > MAX_OBSTACLE_HEIGHT) h = MAX_OBSTACLE_HEIGHT;
		float speed = BASE_SPEED + difficulty * SPEED_SCALE;
		if (speed > MAX_SPEED) speed = MAX_SPEED;
		float w = laneWidth;

		// 尝试随机轨道，跳过已有障碍物未完全入场的轨道，避免重叠
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
		// 所有轨道都被占用，放弃本次生成
	}

	// 只检查队列末尾（最新生成的块），前面的块入队更早必然已下落更远
	private boolean canSpawn(int lane, float newH)
	{
		Obstacle last = lanes[lane].peekLast();
		if (last == null || last.isParried()) return true;
		return !(last.getDisplayY() < 0 && last.getDisplayY() + last.getDisplayH() > -newH);
	}

	// 遍历本列所有障碍物，返回第一个碰撞的无敌帧数，碰撞的障碍物直接消失
	public int checkCollision(Player player)
	{
		int lane = player.getLane();
		float px = player.getX(), py = player.getY(), pw = player.getW(), ph = player.getH();
		for (Obstacle o : lanes[lane])
		{
			if (o.isParried()) continue;
			if (rectOverlap(px, py, pw, ph, o.getX(), o.getY(), o.getW(), o.getH()))
			{
				o.parry();
				return o.framesToPass(ph);
			}
		}
		return 0;
	}

	// 跨跃判定：遍历所有障碍物，跨跃全部匹配的（障碍物直接消失）
	public boolean checkParry(Player player)
	{
		int lane = player.getLane();
		float py = player.getY();
		float px = player.getX();
		float pw = player.getW();
		boolean parried = false;
		for (Obstacle o : lanes[lane])
		{
			if (o.isParried()) continue;
			if (py >= o.getParryY() && py <= o.getParryY() + o.getParryH() && px < o.getParryX() + o.getParryW() && px + pw > o.getParryX())
			{
				o.parry();
				parried = true;
			}
		}
		return parried;
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
