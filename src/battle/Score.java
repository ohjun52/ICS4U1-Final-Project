package battle;

/**
 * 路程计分：每隔一段时间分数自动增加
 */
public class Score
{
	final static private int INTERVAL = 30;		// 每隔 N 帧加一次分
	final static private int INCREMENT = 1;		// 每次加分值

	private int distance;
	private int timer;

	public Score()
	{
		distance = 0;
		timer = 0;
	}

	public void update()
	{
		timer++;
		if (timer >= INTERVAL)
		{
			timer = 0;
			distance += INCREMENT;
		}
	}

	public int getDistance()
	{
		return distance;
	}
}
