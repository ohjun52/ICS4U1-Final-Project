package battle;

/**
 * Shared game constants. Centralised here to avoid cross-class coupling.
 */
public class GameConfig
{
	/** Number of vertical lanes the player can switch between. */
	final static public int LANE_COUNT = 5;

	/** Win condition for the two supported game modes. */
	public enum GameMode { STORY, ENDLESS }

	/** Distance (km) the player must reach to complete story mode. */
	final static public int STORY_TARGET = 150;
}
