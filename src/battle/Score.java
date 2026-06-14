package battle;

/**
 * Accumulates distance score over time.
 */
public class Score
{
	/** Number of frames between each score increment (30 = 0.5 sec at 60 fps). */
	final static private int INTERVAL = 30;
	/** Points added per tick. */
	final static private int INCREMENT = 1;

	private int distance;
	/** Counts frames since last increment. */
	private int timer;

	public Score()
	{
		distance = 0;
		timer = 0;
	}

	/** Advance timer; add INCREMENT points every INTERVAL frames. */
	public void update()
	{
		timer++;
		if (timer >= INTERVAL)
		{
			timer = 0;                // reset accumulator
			distance += INCREMENT;    // 1 km per tick
		}
	}

	public int getDistance() { return distance; }
}
