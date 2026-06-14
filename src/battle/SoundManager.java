package battle;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Wraps javax.sound.sampled for background music and sound effects.
 * Uses WAV format — no external libraries needed.
 */
public class SoundManager
{
	/** Looping background music clip. */
	private Clip bgm;
	/** One-shot parry sound effect clip. */
	private Clip parry;

	public SoundManager()
	{
		bgm = loadClip("sounds/bgm.wav");
		parry = loadClip("sounds/parry.wav");
	}

	/** Load a WAV file into a pre-opened Clip. Returns null on failure. */
	private Clip loadClip(String path)
	{
		try
		{
			AudioInputStream stream = AudioSystem.getAudioInputStream(new File(path));
			Clip clip = AudioSystem.getClip();
			clip.open(stream);
			stream.close();   // stream no longer needed after open
			return clip;
		}
		catch (Exception e)
		{
			System.err.println("Failed to load sound: " + path);
			return null;
		}
	}

	/** Start looping BGM from the beginning. */
	public void playBGM()
	{
		if (bgm != null)
		{
			bgm.setFramePosition(0);              // rewind to start
			bgm.loop(Clip.LOOP_CONTINUOUSLY);     // loop forever
		}
	}

	/** Stop BGM (called on game over). */
	public void stopBGM()
	{
		if (bgm != null) bgm.stop();
	}

	/**
	 * Play the parry sound. Stops and rewinds first so consecutive
	 * parries always trigger an audible effect.
	 */
	public void playParry()
	{
		if (parry != null)
		{
			parry.stop();                 // interrupt current playback
			parry.setFramePosition(0);    // rewind
			parry.start();                // play from start
		}
	}
}
