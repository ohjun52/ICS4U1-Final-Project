package battle;

import javax.sound.sampled.*;
import java.io.File;

public class SoundManager
{
	private Clip bgm;
	private Clip parry;

	public SoundManager()
	{
		bgm = loadClip("sounds/bgm.wav");
		parry = loadClip("sounds/parry.wav");
	}

	private Clip loadClip(String path)
	{
		try
		{
			AudioInputStream stream = AudioSystem.getAudioInputStream(new File(path));
			Clip clip = AudioSystem.getClip();
			clip.open(stream);
			stream.close();
			return clip;
		}
		catch (Exception e)
		{
			System.err.println("Failed to load sound: " + path);
			return null;
		}
	}

	public void playBGM()
	{
		if (bgm != null)
		{
			bgm.setFramePosition(0);
			bgm.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	public void stopBGM()
	{
		if (bgm != null) bgm.stop();
	}

	public void playParry()
	{
		if (parry != null)
		{
			parry.stop();
			parry.setFramePosition(0);
			parry.start();
		}
	}
}
