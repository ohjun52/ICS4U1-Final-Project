package battle;

/**
 * 共享游戏常量，避免类间直接引用
 */
public class GameConfig
{
	final static public int LANE_COUNT = 5;

	public enum GameMode { STORY, ENDLESS }

	final static public int STORY_TARGET = 100;		// 剧情模式目标分数
}
