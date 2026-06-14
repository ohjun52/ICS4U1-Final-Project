package battle;

import processing.core.PApplet;

/**
 * Heads-up display rendered on top of the battle scene.
 *
 * Top-left: HP bar (green) and SH bar (blue) with numeric labels.
 * Top-right: distance score.
 * All positions use screen-ratio constants so the layout scales to any resolution.
 */
public class HUD
{
	/** Bar width as fraction of screen width. */
	final static private float BAR_W_RATIO = 0.2f;
	/** Bar height as fraction of screen height. */
	final static private float BAR_H_RATIO = 0.02f;
	/** Left margin as fraction of screen width. */
	final static private float X_RATIO = 0.03f;
	/** Top margin as fraction of screen height. */
	final static private float Y_RATIO = 0.03f;
	/** Vertical gap between HP and SH bars as fraction of screen height. */
	final static private float GAP_RATIO = 0.02f;
	/** Font size for bar labels (HP / SH). */
	final static private float TEXT_SMALL_RATIO = 0.015f;
	/** Font size for the score display. */
	final static private float TEXT_BIG_RATIO = 0.03f;

	// Colours (ARGB hex)
	final static private int HP_FILL = 0xFF00FF00;   // green
	final static private int HP_BG   = 0xFF500000;   // dark red
	final static private int SH_FILL = 0xFF0096FF;   // blue
	final static private int SH_BG   = 0xFF000050;   // dark navy
	final static private int PANEL   = 0x96000000;   // semi-transparent black
	final static private int TEXT    = 0xFFFFFFFF;   // white

	private Player player;
	private Score score;
	private int maxHp, maxSh;

	// Cached layout values, computed once on the first draw() call
	private float barW, barH, x, y, gap, shY, pad;
	private float textSmall, textBig;
	private float scoreX, scoreY;

	public HUD(Player player, Score score, int maxHp, int maxSh)
	{
		this.player = player;
		this.score = score;
		this.maxHp = maxHp;
		this.maxSh = maxSh;
	}

	public void draw(PApplet p)
	{
		// One-time initialisation using the actual screen dimensions
		if (barW == 0)
		{
			barW = p.width * BAR_W_RATIO;
			barH = p.height * BAR_H_RATIO;
			x = p.width * X_RATIO;
			y = p.height * Y_RATIO;
			gap = p.height * GAP_RATIO;
			shY = y + barH + gap;                   // SH bar sits below HP bar
			pad = barH * 0.25f;                     // padding inside the panel
			textSmall = p.height * TEXT_SMALL_RATIO;
			textBig = p.height * TEXT_BIG_RATIO;
			scoreX = p.width - x;                   // right-aligned score
			scoreY = y;
		}

		// Compute fill widths from current HP/SH ratios
		float hpW = barW * player.getHP() / (float) maxHp;
		float shW = barW * player.getSH() / (float) maxSh;

		p.noStroke();

		// Semi-transparent panel behind the bars
		p.fill(PANEL);
		p.rect(x - pad, y - pad, barW + pad * 2, barH * 2 + gap + pad * 2);

		// HP bar — background then fill
		p.fill(HP_BG);  p.rect(x, y, barW, barH);
		p.fill(HP_FILL); p.rect(x, y, hpW, barH);

		// SH bar — background then fill
		p.fill(SH_BG);  p.rect(x, shY, barW, barH);
		p.fill(SH_FILL); p.rect(x, shY, shW, barH);

		// Numeric labels centred inside each bar
		p.fill(TEXT);
		p.textSize(textSmall);
		p.textAlign(PApplet.CENTER, PApplet.CENTER);
		p.text("HP " + player.getHP(), x + barW / 2, y + barH / 2);
		p.text("SH " + player.getSH(), x + barW / 2, shY + barH / 2);

		// Score in top-right corner
		p.textSize(textBig);
		p.textAlign(PApplet.RIGHT, PApplet.TOP);
		p.text(score.getDistance() + " km", scoreX, scoreY);
		p.textAlign(PApplet.LEFT, PApplet.TOP);     // restore default
	}
}
