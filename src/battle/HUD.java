package battle;

import processing.core.PApplet;

public class HUD
{
	final static private float BAR_W_RATIO = 0.2f;
	final static private float BAR_H_RATIO = 0.02f;
	final static private float X_RATIO = 0.03f;
	final static private float Y_RATIO = 0.03f;
	final static private float GAP_RATIO = 0.02f;
	final static private float TEXT_SMALL_RATIO = 0.015f;
	final static private float TEXT_BIG_RATIO = 0.03f;

	final static private int HP_FILL = 0xFF00FF00;
	final static private int HP_BG   = 0xFF500000;
	final static private int SH_FILL = 0xFF0096FF;
	final static private int SH_BG   = 0xFF000050;
	final static private int PANEL   = 0x96000000;
	final static private int TEXT    = 0xFFFFFFFF;

	private Player player;
	private Score score;
	private int maxHp, maxSh;

	// 预计算
	private float barW, barH, x, y, gap, shY, pad;
	private float textSmall, textBig;
	private float scoreX, scoreY;

	public HUD(Player player, Score score, int maxHp, int maxSh)
	{
		this.player = player;
		this.score = score;
		this.maxHp = maxHp;
		this.maxSh = maxSh;
		// 尺寸在首次 draw 时根据 PApplet 计算
	}

	public void draw(PApplet p)
	{
		if (barW == 0)
		{
			barW = p.width * BAR_W_RATIO;
			barH = p.height * BAR_H_RATIO;
			x = p.width * X_RATIO;
			y = p.height * Y_RATIO;
			gap = p.height * GAP_RATIO;
			shY = y + barH + gap;
			pad = barH * 0.25f;
			textSmall = p.height * TEXT_SMALL_RATIO;
			textBig = p.height * TEXT_BIG_RATIO;
			scoreX = p.width - x;
			scoreY = y;
		}

		float hpW = barW * player.getHP() / (float) maxHp;
		float shW = barW * player.getSH() / (float) maxSh;

		p.noStroke();

		p.fill(PANEL);
		p.rect(x - pad, y - pad, barW + pad * 2, barH * 2 + gap + pad * 2);

		p.fill(HP_BG);  p.rect(x, y, barW, barH);
		p.fill(HP_FILL); p.rect(x, y, hpW, barH);

		p.fill(SH_BG);  p.rect(x, shY, barW, barH);
		p.fill(SH_FILL); p.rect(x, shY, shW, barH);

		p.fill(TEXT);
		p.textSize(textSmall);
		p.textAlign(PApplet.CENTER, PApplet.CENTER);
		p.text("HP " + player.getHP(), x + barW / 2, y + barH / 2);
		p.text("SH " + player.getSH(), x + barW / 2, shY + barH / 2);

		p.textSize(textBig);
		p.textAlign(PApplet.RIGHT, PApplet.TOP);
		p.text(score.getDistance() + " km", scoreX, scoreY);
		p.textAlign(PApplet.LEFT, PApplet.TOP);
	}
}
